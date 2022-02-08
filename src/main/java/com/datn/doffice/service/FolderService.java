package com.datn.doffice.service;

import java.util.List;

import com.datn.doffice.entity.FolderEntity;
import com.datn.doffice.entity.ResponseFolderEntity;

public interface FolderService {
	
	void createFolder(String name, String parentId, String userId);
	
	void createAll();
	
	void createMyDocumentFolder(String userId);
	
	String getMyDocumentFolderId(String userId);
	
	String getPublicFolderId(String userId);
	
	String getSharedFolderId(String userId);
	
	void renameFolder(String folderId, String newName);
	
	ResponseFolderEntity getResponseFolderEntity(String foldeId, int depth);
	
	FolderEntity getFolderById(String folderId);
	
	List<String> getListChildrenId(String folderId);
	
	void delete(String folderId);
}
