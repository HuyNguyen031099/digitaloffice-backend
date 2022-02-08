package com.datn.doffice.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.web.multipart.MultipartFile;

import com.datn.doffice.dto.UserPermissionDocumentDTO;
import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.DocumentKeywordEntity;
import com.datn.doffice.entity.KeywordEntity;
import com.datn.doffice.entity.LockInfoEntity;
import com.datn.doffice.entity.PermissionDocumentEntity;
import com.datn.doffice.entity.ResponseDocumentEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.okm.OpenKMRequest;

public interface DocumentService {
	
	List<DocumentEntity> getAllDocument();
	
	DocumentEntity getDocumentById(String docId);
	
	List<DocumentEntity> getDocumentsApproved();
	
	List<ResponseDocumentEntity> getPendingDocument();
	
	List<ResponseDocumentEntity> getMyDocuments(String userId);
	
	List<ResponseDocumentEntity> getTrash(String userId);
	
	// subscribe
	void subscribeDocument(String docId, String userId);
	
	void unsubscribeDocument(String docId, String userId);
	
	// search by keyword
	List<ResponseDocumentEntity> getDocumentsByKeyword(String keywords);
	
	List<DocumentEntity> getDocumentByCategoryId(String categoryId);
	
	// add list keyword to document
	void addKeyword(List<String> keywords, String docId);
	
	// new
	boolean uploads(MultipartFile file, List<String> categoriesId, String folderId, String type, String userId);
	
	void restores(String docId, String versionName, String userId);
	
	VersionEntity updates(MultipartFile file, String docId, String userId);
	
	boolean rename(String newName, String docId, String userId) throws AddressException, MessagingException;
	
//	void downloads(String docId, String destPath);
	
	String download(String docId) throws IOException;
	
	// lock: when document is locked, no one can edit this document
	// except the user who locked
	void lock(String docId, String userId)  throws Exception;
	
	void unlock(String docId, String userId) throws Exception;
	
	void forceUnlock(String docId);
	
	void reportToAdmin(String userId, String docId);
	
	void deletes(String docId);
	
	// restore from trash
	void restoreDocument(String docId);
	
	// edited
//	String getContent(String name) throws IOException;
	
	String getContent(String docId) throws IOException;
	
	List<DocumentEntity> searchInPeriodTime(String beginDate, String endDate);
	
	// search by name + time created
	List<ResponseDocumentEntity> advancedSearch(String name, String beginDate, String endDate, String userId);
	
	// search by name + time created + list keyword
	List<ResponseDocumentEntity> advancedSearch2(String name, String beginDate, String endDate, String keywords, String userId);
	
	ResponseDocumentEntity getResponseDocument(String docId);
	
	List<String> getListKeyword(String docId);
	
	List<String> getListSubscriber(String docId);
	
	// test
	LockInfoEntity getLockInfo(String docId);
	
	void shareDocument(String docId, List<String> usernames);
	
	List<PermissionDocumentEntity> getPermissionDocument(String docId);
	
	void grantPermissionDocument(String docId, UserPermissionDocumentDTO updd);
	
	void updatePermissionDocument(String docId, String username, List<String> listPermission);
	
	List<PermissionDocumentEntity> getPermissionDocumentOfUser(String userId);
	
	void privateToPublic(String docId);
	
	List<DocumentEntity> getPrivateDocument(String userId);
	
	void approveDocument(String docId);
	
	void rejectDocument(String docId);
	
	boolean purgeDelete(String docId);
	
	List<String> getAllUser();
}
