package com.datn.doffice.controller;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.dao.VersionCollection;
import com.datn.doffice.dto.AddKeywordRequestDTO;
import com.datn.doffice.dto.GrantDocumentPermissionDTO;
import com.datn.doffice.dto.SearchRequestDTO;
import com.datn.doffice.dto.ShareDocumentRequestDTO;
import com.datn.doffice.dto.UpdateDocumentRequestDTO;
import com.datn.doffice.dto.UpdatePermissionDocumentRequestDTO;
import com.datn.doffice.dto.UploadDocumentDTO;
import com.datn.doffice.dto.UserDTO;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.dto.UserPermissionDocumentDTO;
import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.DocumentKeywordEntity;
import com.datn.doffice.entity.NoteEntity;
import com.datn.doffice.entity.PermissionDocumentEntity;
import com.datn.doffice.entity.ResponseDocumentEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.entity.UserSubscribeEntity;
import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.enums.ApiError;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.exceptions.LockException;
import com.datn.doffice.okm.OpenKMRequest;
import com.datn.doffice.service.DocumentService;
import com.datn.doffice.service.MailService;
import com.datn.doffice.service.MyMailService;
import com.datn.doffice.service.NoteService;
import com.datn.doffice.service.UserService;
import com.datn.doffice.service.VersionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(path = "/api")
public class DocumentController extends ApiController {
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private MyMailService myMailService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private NoteService noteService;
	
	@Autowired
	private VersionService versionService;
	
	@GetMapping("/document")
	public ResponseEntity<?> getAllDocument() {
		List<DocumentEntity> documents = documentService.getAllDocument(); 
		return ok(documents);
	}
	
	@GetMapping("/document/approved")
	public ResponseEntity<?> getDocumentsAppoved() {
		List<DocumentEntity> documents = documentService.getDocumentsApproved();
		return ok(documents);
	}
	
	@GetMapping("/document/pending")
	public ResponseEntity<?> getPendingDocuments() {
		List<ResponseDocumentEntity> documents = documentService.getPendingDocument();
		return ok(documents);
	}
	
	@GetMapping("/document/mydocument")
	public ResponseEntity<?> getMyDocuments(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		String userId = user.getUserId();
		List<ResponseDocumentEntity> documents = documentService.getMyDocuments(userId);
		return ok(documents);
	}
	
	@GetMapping("/document/by-id/{id}")
	public ResponseEntity<?> getDocumentById(@PathVariable("id") String docId) {
		DocumentEntity document = documentService.getDocumentById(docId);
		return ok(document);
	}
	
	@GetMapping("/document/trash")
	public ResponseEntity<?> getTrash(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		List<ResponseDocumentEntity> documents = documentService.getTrash(user.getUserId());
		return ok(documents);
	}
	
	@GetMapping("/document/by-category/{id}")
	public ResponseEntity<?> getDocumentsByCategoryId(@PathVariable("id") String categoryId) {
		List<DocumentEntity> documents = documentService.getDocumentByCategoryId(categoryId);
		return ok(documents);
	}
	
	@GetMapping("/document/response-document/{id}")
	public ResponseEntity<?> getResponseDocument(@PathVariable("id") String docId) {
		return ok(documentService.getResponseDocument(docId));
	}
	
	@GetMapping("/document/keywords/{id}")
	public ResponseEntity<?> getListKeyword(@PathVariable("id") String docId) {
		return ok(documentService.getListKeyword(docId));
	}
	
	@GetMapping("/document/subscribers/{id}")
	public ResponseEntity<?> getListSubscriber(@PathVariable("id") String docId) {
		return ok(documentService.getListSubscriber(docId));
	}
	
	// new
	@PostMapping("/document")
	public ResponseEntity<?> uploads(@ModelAttribute UploadDocumentDTO udd, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		boolean flag = documentService.uploads(udd.getFile(), udd.getCategoriesId(), udd.getFolderId(), udd.getType(), user.getUserId());
		if(flag) {
			return ok(ApiStatus.OK);
		} else {
			return error(ApiError.FILE_EXIST, request);
		}
	}
	
	@PostMapping("/document/subscribe/{id}")
	public ResponseEntity<?> subscribeDocument(@PathVariable("id") String docId, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		documentService.subscribeDocument(docId, user.getUserId());
		return ok(ApiStatus.OK);
	}
	
	@PutMapping("/document/unsubscribe/{id}")
	public ResponseEntity<?> unsubscribeDocument(@PathVariable("id") String docId, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		documentService.unsubscribeDocument(docId, user.getUserId());
		return ok(ApiStatus.OK);
	}
	
	@PostMapping("/document/lock/{id}")
	public ResponseEntity<?> locks(@PathVariable("id") String docId, HttpServletRequest request) {
		try {
			UserLoginDetailDTO user = getCurrentUser(request);
			documentService.lock(docId, user.getUserId());
			return ok(ApiStatus.OK);
		} catch(Exception e) {
			return error(ApiError.INTERNAL_SERVER_ERROR, request);
		}
	}
	
	@PutMapping("/document/unlock/{id}")
	public ResponseEntity<?> unlock(@PathVariable("id") String docId, HttpServletRequest request) throws Exception {
		UserLoginDetailDTO user = getCurrentUser(request);
		documentService.unlock(docId, user.getUserId());
//		System.out.println(docId);
		return ok(ApiStatus.OK);
	}
	
//	@GetMapping("/document/lock-info/{id}")
//	public ResponseEntity<?> getLockInfo(@PathVariable("id") String docId, HttpServletRequest request) {
//		UserLoginDetailDTO user = getCurrentUser(request);
//		return ok(documentService.getLockInfo(docId, user.getUserId()));
//	}
	
	@GetMapping("/document/lock-info/{id}")
	public ResponseEntity<?> getLockInfo(@PathVariable("id") String docId) {
		return ok(documentService.getLockInfo(docId));
	}
	
	
	@GetMapping("/document/private")
	public ResponseEntity<?> getPrivateDocument(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		List<DocumentEntity> documents = documentService.getPrivateDocument(user.getUserId());
		return ok(documents);
	}
	
	// get list user have permission with this document
	@GetMapping("/document/permission/{docId}")
	public ResponseEntity<?> getPermissionDocument(@PathVariable("docId") String docId) {
		List<PermissionDocumentEntity> pdes = documentService.getPermissionDocument(docId);
		return ok(pdes);
	}
	
	// get list document that user have permission with
	@GetMapping("/document/permission-document")
	public ResponseEntity<?> getPermissionDocumentOfUser(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		List<PermissionDocumentEntity> pdes = documentService.getPermissionDocumentOfUser(user.getUserId());
		return ok(pdes);
	}
	
	// grant permission for users
	@PostMapping("/document/grant-permission")
	public ResponseEntity<?> grantPermissionDocument(@ModelAttribute GrantDocumentPermissionDTO gdpd) {
//		System.out.println(gdpd);
		String tmp = gdpd.getListUsers();
		UserPermissionDocumentDTO[] updds;
		try {
			updds = convertString(tmp);
			for(int i = 0; i < updds.length; i++) {
				System.out.println(updds[i]);
				documentService.grantPermissionDocument(gdpd.getDocId(), updds[i]);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return ok(ApiStatus.OK);
	}
	
	@PostMapping("/document/share")
	public ResponseEntity<?> shareDocument(@ModelAttribute ShareDocumentRequestDTO sdr) {
		documentService.shareDocument(sdr.getDocId(), sdr.getListUserShare());
		return ok(ApiStatus.OK);
	}
	
	@PutMapping("/document/update-permission")
	public ResponseEntity<?> updatePermission(@ModelAttribute UpdatePermissionDocumentRequestDTO updrd) {
//		System.out.println(updrd);
		documentService.updatePermissionDocument(updrd.getDocId(), updrd.getUsername(), updrd.getListPermission());
		return ok(ApiStatus.OK);
	}
	
	@PutMapping("/document/rename/{id}/{name}")
	public ResponseEntity<?> rename(@PathVariable("id") String docId, @PathVariable("name") String newName, 
			HttpServletRequest request) throws AddressException, MessagingException {
		UserLoginDetailDTO user = getCurrentUser(request);
		boolean flag = documentService.rename(newName, docId, user.getUserId());
		return ok(flag);
	}
	
	@PutMapping("/document/restore-document")
	public ResponseEntity<?> restoreDocument(@RequestParam("docId") String docId) {
		documentService.restoreDocument(docId);
		return ok(ApiStatus.OK);
	}
	
	@PostMapping("/document/addkeyword")
	public ResponseEntity<?> addKeyword(@ModelAttribute AddKeywordRequestDTO ard) {
		documentService.addKeyword(ard.getKeywords(), ard.getDocId());
		return ok(ApiStatus.OK);
	}
	
	@GetMapping("/document/keyword-search/{keywords}")
	public ResponseEntity<?> getDocumentsByKeywords(@PathVariable("keywords") String keywords) {
		List<ResponseDocumentEntity> documents = documentService.getDocumentsByKeyword(keywords);
		return ok(documents);
	}
	
	@GetMapping("/document/advanced-search/name={name}/begin={begin}/end={end}")
	public ResponseEntity<?> advancedSearch(@PathVariable("name") String name, @PathVariable("begin") String beginDate,
			@PathVariable("end") String endDate, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		List<ResponseDocumentEntity> documents = documentService.advancedSearch(name, beginDate, endDate, user.getUserId());
//		System.out.println(name + "-" + beginDate + "-" + endDate);
		return ok(documents);
	}
	
	// need to edit frontend
	@GetMapping("/document/search/name={name}/begin={begin}/end={end}/keywords={keywords}")
	public ResponseEntity<?> advancedSearch2(@PathVariable("name") String name, @PathVariable("begin") String beginDate,
			@PathVariable("end") String endDate, @PathVariable("keywords") String keywords, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		List<ResponseDocumentEntity> documents = documentService.advancedSearch2(name, beginDate, endDate, keywords, user.getUserId());
		System.out.println(name + "-" + beginDate + "-" + endDate + "-" + keywords);
		return ok(documents);
	}
	
	// new
	// test return base64 endcoded string
	@GetMapping("/document/download/{id}")
	public ResponseEntity<?> download(@PathVariable("id") String docId) throws IOException {
		String endcodedString = documentService.download(docId);
		return ok(endcodedString);
	}
	
//	@GetMapping("/document/by-name/{name}")
//	public ResponseEntity<?> getContent(@PathVariable("name") String name) throws IOException {
//		String encodedString = documentService.getContent(name);
////		System.out.println(name);
//		return ok(encodedString);
//	}
	
	@GetMapping("/document/content/{id}")
	public ResponseEntity<?> getContent(@PathVariable("id") String docId) {
		String encodedString = "";
		try {
			encodedString = documentService.getContent(docId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ok(encodedString);
	}
	
	// new
	@PutMapping("/document")
	public ResponseEntity<?> updates(@ModelAttribute UpdateDocumentRequestDTO urd, HttpServletRequest request) {
		try {
			UserLoginDetailDTO user = getCurrentUser(request);
			VersionEntity version = documentService.updates(urd.getFile(), urd.getDocId(), user.getUserId());
			return ok(version);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}

	// new
	@PutMapping("/document/restore/{id}/{version}")
	public ResponseEntity<?> restores(@PathVariable("id") String docId, @PathVariable("version") String version, 
			HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		documentService.restores(docId, version, user.getUserId());
		return ok(ApiStatus.OK);
	}
	
	@GetMapping("/document/notes/{id}")
	public ResponseEntity<?> getListNote(@PathVariable("id") String docId) { 
		return ok(noteService.getNotesByDocumentId(docId));
	}
	
	@GetMapping("/document/actual-version/{id}")
	public ResponseEntity<?> getActualVersion(@PathVariable("id") String docId) {
		VersionEntity version = versionService.getActualVersion(docId);
		return ok(version);
	}
	
	@GetMapping("/document/versions/{id}")
	public ResponseEntity<?> getVersionsOfDocument(@PathVariable("id") String docId) {
		List<VersionEntity> versions = versionService.getVersionsOfDocument(docId);
		return ok(versions);
	}
	
	// need to test
	@PutMapping("/document/private-to-public/{id}")
	public ResponseEntity<?> privateToPublic(@PathVariable("id") String docId) {
		documentService.privateToPublic(docId);
		return ok(ApiStatus.OK);
	}
	
	@PostMapping("/document/report-to-admin/{id}")
	public ResponseEntity<?> reportToAdmin(@PathVariable("id") String docId, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		documentService.reportToAdmin(user.getUserId(), docId);
		return ok(ApiStatus.OK);
	}
	
	@PutMapping("/document/approve-document/{id}")
	public ResponseEntity<?> documentApprove(@PathVariable("id") String docId) {
		documentService.approveDocument(docId);
		return ok(ApiStatus.OK);
	}
	
	@DeleteMapping("/document/reject-document/{id}")
	public ResponseEntity<?> rejectDocument(@PathVariable("id") String docId) {
		documentService.rejectDocument(docId);
		return ok(ApiStatus.OK);
	}
	
	// new
	@DeleteMapping("/document/{id}")
	public ResponseEntity<?> deletes(@PathVariable("id") String docId) {
		documentService.deletes(docId);
		return ok(ApiStatus.OK);
	}
	
	@DeleteMapping("/document/purge-delete/{id}")
	public ResponseEntity<?> purgeDelete(@PathVariable("id") String docId) {
		return ok(documentService.purgeDelete(docId));
	}
	
	@GetMapping("/all-user")
	public ResponseEntity<?> getAllUser() {
		return ok(documentService.getAllUser());
	}
	
	private UserPermissionDocumentDTO[] convertString(String tmp) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        UserPermissionDocumentDTO[] updd = mapper.readValue(tmp, UserPermissionDocumentDTO[].class);
        return updd;
	}
}
