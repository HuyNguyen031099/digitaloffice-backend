package com.datn.doffice.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.doffice.dao.DocumentCollection;
import com.datn.doffice.dao.FolderCollection;
import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.dao.VersionCollection;
import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.FolderEntity;
import com.datn.doffice.entity.NodeEntity;
import com.datn.doffice.entity.ResponseDocumentEntity;
import com.datn.doffice.entity.ResponseFolderEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.service.FolderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FolderServiceImpl implements FolderService {
	
	@Autowired
	private FolderCollection folderCollection;
	
	@Autowired
	private DocumentCollection documentCollection;
	
	@Autowired
	private VersionCollection versionCollection;
	
	@Autowired
	private UserCollection userCollection;

	@Override
	public void createFolder(String name, String parentId, String userId) {
		List<String> children = new ArrayList<String>();
		try {
			FolderEntity folder = FolderEntity.builder()
					.name(name)
					.created(getTimeNow())
					.parentId(parentId)
					.userId(userId)
					.children(children)
					.label("folder")
					.isDeleted(false)
					.build();
			folderCollection.insertObject(folder);
			
			// update children for parent folder
			if(parentId != null) {
				folderCollection.addNewChild(parentId, folder.getId());
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}
	
	@Override
	public String getMyDocumentFolderId(String userId) {
		String folderId = folderCollection.getMyDocumentFolderId(userId);
		return folderId;
	}
	
	@Override
	public String getPublicFolderId(String userId) {
		String folderId = folderCollection.getPublicFolderId(userId);
		return folderId;
	}
	
	@Override
	public String getSharedFolderId(String userId) {
		String folderId = folderCollection.getSharedFolderId(userId);
		return folderId;
	}
	
	@Override
	public void createMyDocumentFolder(String userId) {
		createFolder("My Documents", "", userId);
	}
	
	@Override
	public void createAll() {
		List<UserEntity> users = userCollection.findAll();
		for(UserEntity user: users) {
			String sharedfolderid = folderCollection.getSharedFolderId(user.getId());
			if(sharedfolderid == null) {
//				System.out.println("shared folder id:" + sharedfolderid);
				String parentId = getMyDocumentFolderId(user.getId());
				createFolder("Được chia sẻ", parentId, user.getId());
			}
		}
	}

	@Override
	public void renameFolder(String folderId, String newName) {
		folderCollection.renameFolder(folderId, newName);
	}

	@Override
	public FolderEntity getFolderById(String folderId) {
		FolderEntity folder = folderCollection.getFolderById(folderId);
		return folder;
	}

	@Override
	public List<String> getListChildrenId(String folderId) {
		List<String> childrenId = folderCollection.getListChildrenId(folderId);
		return childrenId;
	}

	@Override
	public ResponseFolderEntity getResponseFolderEntity(String folderId, int depth) {
		try {
			FolderEntity folder = getFolderById(folderId);
			String username = userCollection.findById(folder.getUserId()).getUserName();
//			getListChildren(folder.getChildren(), depth);
			List<Object> children = getListChildren(folder.getChildren(), depth);
//			getListChildren(folder.getChildren(), depth);
			ResponseFolderEntity rfe = ResponseFolderEntity.builder()
					.id(folderId)
					.name(folder.getName())
					.userCreated(username)
					.label(folder.getLabel())
					.parentId(folder.getParentId())
					.children(children)
					.build();
			return rfe;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Object> getListChildren(List<String> childrenIds, int depth) {
		List<Object> children = new ArrayList<Object>();
		for(String childId: childrenIds) {
			FolderEntity folder = folderCollection.getFolderById(childId);
			if(folder != null) {
				if(!folder.isDeleted()) {
					List<String> cids = folder.getChildren();
					String username = userCollection.findById(folder.getUserId()).getUserName();
					ResponseFolderEntity rfe = ResponseFolderEntity.builder()
							.id(folder.getId())
							.name(folder.getName())
							.userCreated(username)
							.created(folder.getCreated())
							.parentId(folder.getParentId())
							.label(folder.getLabel())
							.children(getListChildren(cids, depth + 1))
							.build();
//					NodeEntity node = NodeEntity.builder()
//							.object(folder)
//							.label("folder")
//							.children(getListChildren(cids, depth + 1))
//							.build();
					children.add(rfe);
				}
			} else {
//				System.out.println(childId);
				DocumentEntity document = documentCollection.getDocumentById(childId);
				if(document.isApproved()) {
					ResponseDocumentEntity rde = convertToResponseDocument(document);
//					NodeEntity node = NodeEntity.builder()
//							.object(rde)
//							.label("document")
//							.children(null)
//							.build();
					children.add(rde);
				}
			}
		}
		return children;
	}

	@Override
	public void delete(String folderId) {
		folderCollection.deleteFolder(folderId);
	}
	
	private String getTimeNow() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}
	
	private ResponseDocumentEntity convertToResponseDocument(DocumentEntity document) {
//		System.out.println("document: " + document);
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
				.isApproved(document.isApproved())
				.isSubscribe(document.getIsSubscribe())
				.label("document")
				.folderId(document.getFolderId())
				.build();
		return rde;
	}
}
