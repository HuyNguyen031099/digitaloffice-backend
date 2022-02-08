package com.datn.doffice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.datn.doffice.dao.CategoryCollection;
import com.datn.doffice.dao.DocumentCategoryCollection;
import com.datn.doffice.dao.DocumentCollection;
import com.datn.doffice.dao.DocumentKeywordCollection;
import com.datn.doffice.dao.FolderCollection;
import com.datn.doffice.dao.KeywordCollection;
import com.datn.doffice.dao.LockInfoCollection;
import com.datn.doffice.dao.NoteCollection;
import com.datn.doffice.dao.PermissionDocumentCollection;
import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.dao.UserSubscribeCollection;
import com.datn.doffice.dao.VersionCollection;
import com.datn.doffice.dto.SearchRequestDTO;
import com.datn.doffice.dto.UserPermissionDocumentDTO;
import com.datn.doffice.entity.CategoryEntity;
import com.datn.doffice.entity.DocumentCategoryEntity;
import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.DocumentKeywordEntity;
import com.datn.doffice.entity.KeywordEntity;
import com.datn.doffice.entity.LockInfoEntity;
import com.datn.doffice.entity.NoteEntity;
import com.datn.doffice.entity.PermissionDocumentEntity;
import com.datn.doffice.entity.ResponseDocumentEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.entity.UserSubscribeEntity;
import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.exceptions.FileExistException;
import com.datn.doffice.exceptions.LockException;
import com.datn.doffice.okm.OpenKMDocument;
import com.datn.doffice.okm.OpenKMRequest;
import com.datn.doffice.service.DocumentService;
import com.datn.doffice.service.MailService;
import com.datn.doffice.service.MyFileStorageService;
import com.datn.doffice.service.MyMailService;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Version;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private DocumentCollection documentCollection;

	@Autowired
	private OpenKMDocument openkmDocument;

	@Autowired
	private VersionCollection versionCollection;

	@Autowired
	private NoteCollection noteCollection;

	@Autowired
	private DocumentCategoryCollection documentCategoryCollection;

	@Autowired
	private UserCollection userCollection;

	@Autowired
	private UserSubscribeCollection userSubscribeCollection;

	@Autowired
	private DocumentKeywordCollection documentKeywordCollection;

	@Autowired
	private KeywordCollection keywordCollection;
	
	@Autowired
	private MyMailService myMailService;
	
	@Autowired
	private MailService mailService;

	@Autowired
	private LockInfoCollection lockInfoCollection;
	
	@Autowired
	private PermissionDocumentCollection permissionDocumentCollection;
	
	@Autowired
	private FolderCollection folderCollection;

	@Autowired
	private MyFileStorageService fileStorageService;

//	@Override
//	public DocumentEntity createDocument(OpenKMRequest request, String folderId, String filename) {
//		try {
//			Document doc = openkmDocument.createDocument(request.getDocPath(), folderId, filename,
//					request.getUsername(), request.getPassword());
//			String userId = userCollection.findByUsername(doc.getAuthor()).getId();
//			Version ver = doc.getActualVersion();
//			// insert new document
//			DocumentEntity newDocument = DocumentEntity.builder().id(doc.getUuid())
//					.nameDocument(extractNameFromPath(doc.getPath())).actualVersion(ver.getName())
//					.lastModified(calendarToString(doc.getLastModified()))
//					.timeCreated(calendarToString(doc.getCreated())).format(doc.getMimeType()).isApproved(0)
//					.isDeleted(false).isSubscribe(0).isLock(0).url(doc.getPath()).build();
//			documentCollection.insertObject(newDocument);
//			// insert new version
//			VersionEntity newVersion = VersionEntity.builder().name(ver.getName()).size(ver.getSize())
//					.author(ver.getAuthor()).documentId(newDocument.getId()).actual(true)
//					.created(calendarToString(ver.getCreated())).isDeleted(false).build();
//			versionCollection.insertObject(newVersion);
//
//			return newDocument;
//		} catch (Exception e) {
//			log.info(e.getMessage(), e);
//		}
//		return null;
//	}

	@Override
	public boolean uploads(MultipartFile file, List<String> categoriesId, String folderId, String type, String userId) {
		// insert new document
		String author = userCollection.findById(userId).getUserName();
		String url = "";
		boolean isApproved = false;
		boolean isExist = false;
		
		if(type.equals("private")) {
			fileStorageService.makePrivateFolder(author);
			url = fileStorageService.storePrivateDocument(file, author);
			isApproved = true;
		} else if(type.equals("public")) {
			isExist = fileStorageService.checkExist(file.getOriginalFilename());
			if(isExist) {
				return false;
			}
			url = fileStorageService.storeFile(file);
			isApproved = false;
		}
		
		if(author.equals("administrator")) isApproved = true;
		
		String filename = file.getOriginalFilename();
		String tmp[] = filename.split("\\.");
		String format = tmp[tmp.length - 1];
		List<String> shared = new ArrayList<>();
		
		DocumentEntity newDocument = DocumentEntity.builder()
				.nameDocument(filename)
				.userId(userId)
				.actualVersion("1.0")
				.lastModified(getTimeNow())
				.timeCreated(getTimeNow())
				.format(format)
				.label("document")
				.isSubscribe(0)
				.isLock(0)
				.isApproved(isApproved)
				.type(type)
				.folderId(folderId)
				.shared(shared)
				.url(url)
				.isDeleted(false)
				.build();
		documentCollection.insertObject(newDocument);
		
		// update child for parent folder
		folderCollection.addNewChild(folderId, newDocument.getId());

		// insert new version
		VersionEntity newVersion = VersionEntity.builder().name("1.0").size(file.getSize()).author(author)
				.documentId(newDocument.getId()).actual(true).created(getTimeNow()).isDeleted(false).build();
		versionCollection.insertObject(newVersion);
		if(type.equals("public")) {
			fileStorageService.createFolderVersion(file, newVersion.getName());
		} else if(type.equals("private")) {
			fileStorageService.storePrivateVersion(file, newVersion.getName(), author);
		}
		
		// insert document-category
		for(String categoryId: categoriesId) {
			DocumentCategoryEntity dce = DocumentCategoryEntity.builder()
					.documentId(newDocument.getId())
					.categoryId(categoryId)
					.isDeleted(false)
					.build();
			documentCategoryCollection.insertObject(dce);
		}
		
		List<String> usernames = getAllUser();
		// granted permission for author
		String[] pp = {"1", "2", "3", "4"};
		PermissionDocumentEntity ppp = PermissionDocumentEntity.builder()
				.documentId(newDocument.getId())
				.username(author)
				.listPermission(Arrays.asList(pp))
				.build();
		permissionDocumentCollection.insertObject(ppp);
		
		PermissionDocumentEntity pppp = PermissionDocumentEntity.builder()
				.documentId(newDocument.getId())
				.username("administrator")
				.listPermission(Arrays.asList(pp))
				.build();
		permissionDocumentCollection.insertObject(pppp);
		
		// if document is public, create default permission (read) for all users
		// with author, remove from list user and grant all permissions
		if(type.equals("public")) {
			// granted permission for all other users
			usernames.remove(author);
			String[] p = {"1"};
			List<String> permissions = Arrays.asList(p);
			for(String username: usernames) {
				PermissionDocumentEntity pde = PermissionDocumentEntity.builder()
						.documentId(newDocument.getId())
						.username(username)
						.listPermission(permissions)
						.build();
				permissionDocumentCollection.insertObject(pde);
			}
		}
		return true;
	}

	private String getTimeNow() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

//	private String extractNameFromPath(String path) {
//		String[] tmp = path.split("/");
//		return tmp[tmp.length - 1].toString();
//	}

	// destPath: where file is stored
//	@Override
//	public void downloads(String docId, String destPath) {
//		DocumentEntity document = documentCollection.getDocumentById(docId);
//		String filename = document.getNameDocument();
//		String defaultUrl = "c:\\digital-office\\download";
//		// if user not enter destination path
//		if(destPath.equals("")) {
//			// create folder to save
//			boolean flag = fileStorageService.makeDir(defaultUrl);
//			System.out.println(flag);
//			destPath = defaultUrl + "//" + filename;
//		}
//		
//		File file = new File(destPath);
//		InputStream is = fileStorageService.loadFileAsInputStream(filename);
//		try {
//			fileStorageService.copyInputStreamToFile(is, file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public List<DocumentEntity> getAllDocument() {
		return documentCollection.getAllDocument();
	}

	@Override
	public DocumentEntity getDocumentById(String id) {
		DocumentEntity document = documentCollection.getDocumentById(id);
//		System.out.println("doc:" + document);
		return document;
	}

	@Override
	public List<DocumentEntity> getDocumentsApproved() {
		return documentCollection.getDocumentsApproved();
	}
	
	@Override
	public List<ResponseDocumentEntity> getPendingDocument() {
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		List<DocumentEntity> documents = documentCollection.getListPendingDocument();
		for(DocumentEntity document: documents) {
			ResponseDocumentEntity rde = convertToResponseDocument(document);
			rdes.add(rde);
		}
		return rdes;
	}
	
	@Override
	public void approveDocument(String docId) {
		documentCollection.approveDocument(docId);
		DocumentEntity document = documentCollection.getDocumentById(docId);
		String userId = document.getUserId();
		UserEntity user = userCollection.findById(userId);
		mailService.notifyApprove(user, document.getNameDocument());
	}
	
	@Override
	public void rejectDocument(String docId) {
		DocumentEntity document = documentCollection.getDocumentById(docId);
		String userId = document.getUserId();
		String publicFolderId = folderCollection.getPublicFolderId(userId);
		folderCollection.removeChild(publicFolderId, docId);
		documentCollection.rejectDocument(docId);
		documentCategoryCollection.delete(docId);
		UserEntity user = userCollection.findById(userId);
		mailService.notifyReject(user, document.getNameDocument());
		// remove version, note, permission :))
		// ...
	}
	
	@Override
	public List<ResponseDocumentEntity> getMyDocuments(String userId) {
		String username = userCollection.findById(userId).getUserName();
		List<DocumentEntity> documents = documentCollection.getDocumentsByUserCreated(username, userId);
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		for(DocumentEntity document: documents) {
			ResponseDocumentEntity rde = convertToResponseDocument(document);
			rdes.add(rde);
		}
		return rdes;
	}
	
	@Override
	public List<DocumentEntity> getPrivateDocument(String userId) {
		List<DocumentEntity> documents = documentCollection.getPrivateDocument(userId);
		return documents;
	}

	@Override
	public List<ResponseDocumentEntity> getTrash(String userId) {
		List<DocumentEntity> documents = documentCollection.getTrash(userId);
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		for(DocumentEntity document: documents) {
			ResponseDocumentEntity rde = convertToResponseDocument(document);
			rdes.add(rde);
		}
		return rdes;
	}
	
	@Override
	public List<DocumentEntity> getDocumentByCategoryId(String categoryId) {
		List<DocumentCategoryEntity> dces = documentCategoryCollection.getDocumentsByCategoryId(categoryId);
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		for(DocumentCategoryEntity dce: dces) {
			DocumentEntity document = documentCollection.getDocumentById(dce.getDocumentId());
			documents.add(document);
		}
		return documents;
	}
	
	@Override
	public List<DocumentEntity> searchInPeriodTime(String beginDate, String endDate) {
		return documentCollection.searchInPeriodTime(beginDate, endDate);
	}
	
	// need to improve: search by name, time and keyword together
	@Override
	public List<ResponseDocumentEntity> getDocumentsByKeyword(String keywords) {
		String[] kws = keywords.split(",");
		List<DocumentKeywordEntity> dkes = documentKeywordCollection.getDocumentsByKeywords(standardKeywords(Arrays.asList(kws)));
//		System.out.println(dkes);
		List<ResponseDocumentEntity> documents = new ArrayList<ResponseDocumentEntity>();
		for (DocumentKeywordEntity dke : dkes) {
			DocumentEntity document = documentCollection.getDocumentById(dke.getDocumentId());
			ResponseDocumentEntity rde = convertToResponseDocument(document);
			documents.add(rde);
		}
		return documents;
	}
	
	@Override
	public List<ResponseDocumentEntity> advancedSearch(String name, String beginDate, String endDate, String userId) {
		String username = userCollection.findById(userId).getUserName();
		List<DocumentEntity> documents = documentCollection.advancedSearch(name, beginDate, endDate, userId, username);
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		for(DocumentEntity document: documents) {
			ResponseDocumentEntity rde = convertToResponseDocument(document);
			rdes.add(rde);
		}
		return rdes;
	}
	
	@Override
	public List<ResponseDocumentEntity> advancedSearch2(String name, String beginDate, String endDate, String keywords, String userId) {
		String username = userCollection.findById(userId).getUserName();
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		List<DocumentEntity> documents = documentCollection.advancedSearch(name, beginDate, endDate, userId, username);
		String[] kws = keywords.split(",");
		for(DocumentEntity document: documents) {
			List<String> kwds = documentKeywordCollection.getListKeyword(document.getId());
//			System.out.println("kwds:" + kwds);
			if(kwds == null) continue;
			if(kwds.containsAll(standardKeywords(Arrays.asList(kws)))) {
				ResponseDocumentEntity rde = convertToResponseDocument(document);
				rdes.add(rde);
			}
		}
		return rdes;
	}
	
	@Override
	public ResponseDocumentEntity getResponseDocument(String docId) {
		DocumentEntity document = getDocumentById(docId);
		ResponseDocumentEntity rde = convertToResponseDocument(document);
//		System.out.println(rde);
		return rde;
	}
	
	@Override
	public List<String> getListKeyword(String docId) {
		return documentKeywordCollection.getListKeyword(docId);
	}

	@Override
	public List<String> getListSubscriber(String docId) {
		List<UserSubscribeEntity> uses = userSubscribeCollection.getListSubscribeDocument(docId);
//		List<UserEntity> users = new ArrayList<UserEntity>();
		List<String> usernames = new ArrayList<String>();
		for(UserSubscribeEntity use: uses) {
			String username = userCollection.findById(use.getUser_id()).getUserName();
			usernames.add(username);
		}
//		System.out.println(users);
		return usernames;
	}
	
	@Override
	public String download(String docId) throws IOException {
		DocumentEntity document = documentCollection.getDocumentById(docId);
		byte[] fileContent = FileUtils.readFileToByteArray(new File(document.getUrl()));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}

	// encode file to base64
//	@Override
//	public String getContent(String name) throws IOException {
//		System.out.println("name:" + name);
//		DocumentEntity document = documentCollection.getDocumentByName(name);
////		System.out.println("doc:" + document);
//		// check if document has format is doc, convert to pdf
//		String format = document.getFormat();
//		if(format.equals("doc") || format.equals("docx")) {
//			// not use for doc
//			return convertDocToPdf(document.getUrl());
//		}
////		System.out.println("url:" + document.getUrl());
//		byte[] fileContent = FileUtils.readFileToByteArray(new File(document.getUrl()));
//		String encodedString = Base64.getEncoder().encodeToString(fileContent);
//		return encodedString;
// 	}
	
	@Override
	public String getContent(String docId) throws IOException {
//		System.out.println("docId:" + docId);
		DocumentEntity document = documentCollection.getDocumentById(docId);
		System.out.println("doc:" + document);
		// check if document has format is doc, convert to pdf
		String format = document.getFormat();
		if(format.equals("doc") || format.equals("docx")) {
			// not use for doc
			String url = document.getUrl();
//			System.out.println("url:" + url);
			String encodedStr = convertDocToPdf(url);
			return encodedStr;
		}
//		System.out.println("url:" + document.getUrl());
		byte[] fileContent = FileUtils.readFileToByteArray(new File(document.getUrl()));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
 	}
	
	public String convertDocToPdf(String fileName) throws IOException {
		InputStream doc = new FileInputStream(new File(fileName));
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    XWPFDocument document = new XWPFDocument(doc);
	    PdfOptions options = PdfOptions.create();
	    PdfConverter.getInstance().convert(document, baos, options);
	    String base64_encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
	    doc.close();
	    return base64_encoded;
	}
	
	public String createPdfName(String name) {
		String tmp[] = name.split("\\.");
		StringBuilder res = new StringBuilder("");
		for(int i = 0; i < tmp.length - 1; i++) {
			res.append(tmp[i]);
		}
		res.append(".pdf");
		return res.toString();
	}

	// MultipartFile -> File
	public File convertToFile(MultipartFile multipartFile, String path) {
		File file = new File(path);
		try (OutputStream os = new FileOutputStream(file)) {
			os.write(multipartFile.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	// restore document from trash
	@Override
	public void restoreDocument(String docId) {
		try {
//			System.out.println(docId);
			String parentFolderId = documentCollection.getDocumentById(docId).getFolderId();
			documentCollection.restoreDocument(docId);
			documentCategoryCollection.restore(docId);
			// update child of folder
			folderCollection.addNewChild(parentFolderId, docId);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}

	// restore version of document
	@Override
	public void restores(String docId, String versionName, String userId) {
		try {
			DocumentEntity document = documentCollection.getDocumentById(docId);
			// set actual field of document to new version
			documentCollection.updateVersionDocument(docId, versionName);
			versionCollection.restoreVersion(docId, versionName);
			String fileName = document.getNameDocument();
			String type = document.getType();
			String username = userCollection.findById(userId).getUserName();
			if(type.equals("public")) {
				fileStorageService.restore(fileName, versionName);
			} else if(type.equals("private")) {
				fileStorageService.restorePrivateDocument(fileName, versionName, username);
			}
			
			// insert new note
			String author = userCollection.findById(userId).getUserName();
			NoteEntity note = NoteEntity.builder()
					.date(getTimeNow())
					.author(author)
					.text("Phiên bản " + versionName + " được khôi phục bởi " + author)
					.isDeleted(false)
					.documentId(docId)
					.build();
			noteCollection.insertObject(note);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}

	@Override
	public void subscribeDocument(String docId, String userId) {
		// insert new object to subscribe collection
		userSubscribeCollection.addNewSubscribe(docId, userId);
		// set is_subscribe filed to 1
		documentCollection.subscribe(docId);
	}

	@Override
	public void unsubscribeDocument(String docId, String userId) {
		// remove document record in collection
		userSubscribeCollection.unsubscribe(docId, userId);
		// check if nobody left is document subscriber, set is_subcribe to 0
		List<UserSubscribeEntity> use = userSubscribeCollection.getListSubscribeDocument(docId);
		if (use.isEmpty()) {
			documentCollection.unsubscribe(docId);
		}
	}

	@Override
	public void lock(String docId, String userId) throws LockException {
		String username = userCollection.findById(userId).getUserName();
		LockInfoEntity lie = lockInfoCollection.getLockInfo(docId);
		System.out.println("lie:" + lie);
		if(lie != null) {
			throw new LockException();
		} else {
			LockInfoEntity lock = LockInfoEntity.builder()
					.documentId(docId)
					.username(username)
					.timeCreated(getTimeNow())
					.build();
			lockInfoCollection.insertObject(lock);
			System.out.println("lockinfo: " + lock);
			// update document
			documentCollection.lock(docId);
		}
	}

	@Override
	public void unlock(String docId, String userId) throws Exception {
		// check user
		LockInfoEntity lock = getLockInfo(docId);
		String username = userCollection.findById(userId).getUserName();
//		System.out.println(lock);
		if (username.equals(lock.getUsername())) {
			documentCollection.unlock(docId);
			lockInfoCollection.remove(docId, username);
		} else {
			throw new Exception("This document is editing by another user in this time!");
		}
	}
	
	@Override
	public LockInfoEntity getLockInfo(String docId) {
//		String username = userCollection.findById(userId).getUserName();
		return lockInfoCollection.getLockInfo(docId);
	}

	@Override
	public void forceUnlock(String docId) {
		// unlock but need administrator permission
		LockInfoEntity lock = lockInfoCollection.getLockInfo(docId);
		if(lock != null) {
			documentCollection.unlock(docId);
			lockInfoCollection.remove(docId, lock.getUsername());
		}
	}
	
	@Override
	public void reportToAdmin(String userId, String docId) {
		try {
			LockInfoEntity lock = lockInfoCollection.getLockInfo(docId);
			UserEntity repoter = userCollection.findById(userId);
			UserEntity admin = userCollection.findByUsername("administrator");
			String nameDocument = documentCollection.getDocumentById(lock.getDocumentId()).getNameDocument();
			String actor = lock.getUsername();
			mailService.notifyAdmin(admin, repoter.getFullName(), lock.getTimeCreated(), nameDocument, actor);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<PermissionDocumentEntity> getPermissionDocument(String docId) {
		List<PermissionDocumentEntity> pdes = permissionDocumentCollection.getPermissionDocumentEntity(docId);
		System.out.println(pdes);
		return pdes;
	}
	
	@Override
	public List<PermissionDocumentEntity> getPermissionDocumentOfUser(String userId) {
		String username = userCollection.findById(userId).getUserName();
		List<PermissionDocumentEntity> pdes = permissionDocumentCollection.getPermissionOfUser(username);
		return pdes;
	}
	
	@Override
	public void grantPermissionDocument(String docId, UserPermissionDocumentDTO updd) {
		// if user already have permission, update it; else create new one
		PermissionDocumentEntity pde = permissionDocumentCollection.getPermissionDocumentOfUser(docId, updd.getUsername());
		if(pde != null) {
			permissionDocumentCollection.updatePermission(docId, updd.getUsername(), updd.getRoles());			
		} else {
			PermissionDocumentEntity pd = PermissionDocumentEntity.builder()
					.documentId(docId)
					.username(updd.getUsername())
					.listPermission(updd.getRoles())
					.build();
			permissionDocumentCollection.insertObject(pd);
		}
	}
	
	@Override
	public void updatePermissionDocument(String docId, String username, List<String> listPermission) {
		PermissionDocumentEntity pde = permissionDocumentCollection.getPermissionDocumentOfUser(docId, username);
		if(pde != null) {
			permissionDocumentCollection.updatePermission(docId, username, listPermission);			
		}
	}

	@Override
	public void addKeyword(List<String> keywords, String docId) {
		List<String> tmp = standardKeywords(keywords);
		// get list keyword of document
		List<String> kds = documentKeywordCollection.getListKeyword(docId);
		if (kds == null) {
			// add new
			try {
				DocumentKeywordEntity dke = DocumentKeywordEntity.builder()
						.documentId(docId)
						.keywords(tmp)
						.build();
				documentKeywordCollection.insertObject(dke);
			} catch (Exception e) {
				log.info(e.getMessage(), e);
			}
		} else {
			// only add a new keyword if it is not existed
			for (String word : tmp) {
				boolean flag = kds.contains(word);
				if (!flag) kds.add(word);
			}
			documentKeywordCollection.updateKeywords(docId, kds);
		}
	}

	private List<String> standardKeywords(List<String> strs) {
		List<String> ress = new ArrayList<>();
		for (String str : strs) {
			ress.add(str.replaceAll("\\s+", " ").trim());
		}
		return ress;
	}

//	@Override
//	public String checkoutDocument(String docId, OpenKMRequest request) {
//		String userId = openkmDocument.checkoutDocument(docId, request.getUsername(), request.getPassword());
//		return userId;
//	}
//
//	@Override
//	public void cancleCheckout(String docId, OpenKMRequest request) {
//		openkmDocument.cancleCheckout(docId, request.getUsername(), request.getPassword());
//	}
//
//	@Override
//	public VersionEntity updateDocument(String docId, OpenKMRequest request) {
//		try {
//			// get previous version id
//			// when update, actual version will be previous version
//			VersionEntity oldVersion = versionCollection.getActualVersion(docId);
//			String previousVersoinId = oldVersion.getId();
//			// set actual field of old version is false
//			versionCollection.updateVersion(docId, oldVersion.getName());
//			// update new version
//			Version version = openkmDocument.checkinDocument(docId, request.getDocPath(), request.getUsername(),
//					request.getPassword());
//			VersionEntity versionEntity = VersionEntity.builder().name(version.getName()).actual(true)
//					.author(version.getAuthor()).created(calendarToString(version.getCreated())).documentId(docId)
//					.previousVersionId(previousVersoinId).isDeleted(false).size(version.getSize()).build();
//
//			versionCollection.insertObject(versionEntity);
//
//			// set actual version of document to new version
//			documentCollection.updateVersionDocument(docId, versionEntity.getName());
//
//			// insert new note when new version is updated
//			NoteEntity note = NoteEntity.builder().author("system").date(calendarToString(version.getCreated()))
//					.text("New version " + version.getName() + " by author " + version.getAuthor()).documentId(docId)
//					.build();
//			noteCollection.insertObject(note);
//
//			System.out.println(version);
//
//			return versionEntity;
//		} catch (Exception e) {
//			log.info(e.getMessage(), e);
//		}
//		return null;
//	}

	// userId: who updated
	@Override
	public VersionEntity updates(MultipartFile file, String docId, String userId) {
		try {
			// get previous version id
			// when update, actual version will be previous version
			VersionEntity oldVersion = versionCollection.getActualVersion(docId);
			String previousVersoinId = oldVersion.getId();
			// set actual field of old version is false
			versionCollection.updateVersion(docId, oldVersion.getName());
			
			// update new version
			List<VersionEntity> versions = versionCollection.getListVersionDocument(docId);
			String nameOldVersion = versions.get(versions.size() - 1).getName();
			String nameNewVersion = Double.toString(Double.parseDouble(nameOldVersion) + 1.0);
			String author = userCollection.findById(userId).getUserName();
			VersionEntity versionEntity = VersionEntity.builder().name(nameNewVersion).actual(true).author(author)
					.created(getTimeNow()).documentId(docId).previousVersionId(previousVersoinId).isDeleted(false)
					.size(file.getSize()).build();

			versionCollection.insertObject(versionEntity);
//			System.out.println(versionEntity);
			DocumentEntity document = documentCollection.getDocumentById(docId);
			String type = document.getType();
			String uid = document.getUserId();
			String username = userCollection.findById(uid).getUserName();	// need to update in folder of author, not user update
			if(type.equals("public")) {
				fileStorageService.createFolderVersion(file, nameNewVersion);
			} else if(type.equals("private")) {
				fileStorageService.storePrivateVersion(file, nameNewVersion, username);
			}
			
			// set actual version of document to new version
			// and update last modified time
			documentCollection.updateVersionDocument(docId, versionEntity.getName());

			// insert new note when new version is updated
			NoteEntity note = NoteEntity.builder().author("system").date(getTimeNow())
					.text("Phiên bản mới " + versionEntity.getName() + " được cập nhật bởi " + versionEntity.getAuthor())
					.documentId(docId).build();
			noteCollection.insertObject(note);
			
			// notify to list user
			List<UserSubscribeEntity> uses = userSubscribeCollection.getListSubscribeDocument(docId);
			if(uses != null) {
				for(UserSubscribeEntity use: uses) {
					UserEntity userr = userCollection.findById(use.getUser_id());
					mailService.notifyUpdate(userr, document, username);
				}
			}

			System.out.println("update:" + versionEntity);

			return versionEntity;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean rename(String newName, String docId, String userId) throws AddressException, MessagingException {
		// update metadata
		DocumentEntity oldDocument = documentCollection.getDocumentById(docId);
		String uid = oldDocument.getUserId();
		String author = userCollection.findById(uid).getUserName();
		documentCollection.rename(newName, docId, author);		// author of document different with user who rename document 
		
		String type = oldDocument.getType();
		// insert new note
		String username = userCollection.findById(userId).getUserName();
		NoteEntity note = NoteEntity.builder()
				.author(username)
				.date(getTimeNow())
				.text("Tên tài liệu được cập nhật từ " + oldDocument.getNameDocument() + " thành " + newName + " bởi " + username)
				.documentId(docId)
				.build();
		noteCollection.insertObject(note);
		
		System.out.println("rename:" + oldDocument);
		// rename file
		boolean flag = false;
		if(type.equals("public")) {
			flag = fileStorageService.rename(oldDocument.getNameDocument(), newName);
		} else if(type.equals("private")) {
			flag = fileStorageService.renamePrivateDocument(oldDocument.getNameDocument(), newName, username);
		}
		
		// notify to list user
		List<UserSubscribeEntity> uses = userSubscribeCollection.getListSubscribeDocument(docId);
		if(uses != null) {
			for(UserSubscribeEntity use: uses) {
				UserEntity userr = userCollection.findById(use.getUser_id());
				mailService.notifyRename(userr, oldDocument, newName, username);
			}
		}
		return flag;
	}
	
	@Override
	public void privateToPublic(String docId) {
		documentCollection.privateToPublic(docId);
		List<String> usernames = getAllUser();
		// remove user that granted permission
		List<PermissionDocumentEntity> pdes = permissionDocumentCollection.getPermissionDocumentEntity(docId);
		if(pdes != null) {
			for(PermissionDocumentEntity pde: pdes) {
				if(usernames.contains(pde.getUsername())) usernames.remove(pde.getUsername());
			}
		}
		// grant permission for all user
		String[] p = {"1"};
		List<String> permissions = Arrays.asList(p);
		for(String username: usernames) {
			PermissionDocumentEntity pde = PermissionDocumentEntity.builder()
					.documentId(docId)
					.username(username)
					.listPermission(permissions)
					.build();
			permissionDocumentCollection.insertObject(pde);
		}
		
		// move file
		// ...
	}
	
	private ResponseDocumentEntity convertToResponseDocument(DocumentEntity document) {
		String username = userCollection.findById(document.getUserId()).getUserName();
		ResponseDocumentEntity rde = ResponseDocumentEntity.builder()
				.id(document.getId())
				.userId(document.getUserId())
				.owner(username)
				.nameDocument(document.getNameDocument())
				.timeCreated(document.getTimeCreated())
				.version(versionCollection.getActualVersion(document.getId()))
				.format(document.getFormat())
				.url(document.getUrl())
				.type(document.getType())
				.shared(document.getShared())
				.lastModified(document.getLastModified())
				.isLock(document.getIsLock())
				.isSubscribe(document.getIsSubscribe())
				.isApproved(document.isApproved())
				.folderId(document.getFolderId())
				.build();
		return rde;
	}
	
	// if have not create shared folder of user => error :))
	@Override
	public void shareDocument(String docId, List<String> usernames) {
		// update list shared of document
		documentCollection.shareDocument(docId, usernames);
		String[] p = {"1"};
		List<String> permissions = Arrays.asList(p);
		// add child to shared folder of list user
		// and grant read permission to user who is shared
		for(String username: usernames) {
			String userId = userCollection.findByUsername(username).getId();
			String folderId = folderCollection.getSharedFolderId(userId);
			folderCollection.addNewChild(folderId, docId);
			
			PermissionDocumentEntity pde = PermissionDocumentEntity.builder()
					.documentId(docId)
					.username(username)
					.listPermission(permissions)
					.build();
			permissionDocumentCollection.insertObject(pde);
		}
	}
	
	@Override
	public boolean purgeDelete(String docId) {
		return documentCollection.purgeDelete(docId);
	}
	
	// is_deleted => true
	@Override
	public void deletes(String docId){
		DocumentEntity document = documentCollection.getDocumentById(docId);
		String folderId = documentCollection.getDocumentById(docId).getFolderId();
		folderCollection.removeChild(folderId, docId);
		documentCategoryCollection.delete(docId);
		int isLock = document.getIsLock();
		System.out.println("is lock:" + isLock);
		if(isLock == 1) {
			forceUnlock(docId);
			documentCollection.unlock(docId);
		}
		List<String> userIds = documentCollection.deleteDocument(docId);
		if(userIds != null) {
			for(String userId: userIds) {
				UserEntity receiver = userCollection.findById(userId);
				mailService.notifyForReceiver(receiver, document);
			}
		}
	}
	
	@Override
	public List<String> getAllUser() {
		List<UserEntity> users = userCollection.findAll();
		List<String> usernames = new ArrayList<String>();
		for(UserEntity user: users) {
			if(!user.getUserName().equals("administrator")) {
				usernames.add(user.getUserName());
			}
		}
		return usernames.stream().sorted().collect(Collectors.toList());
	}

}
