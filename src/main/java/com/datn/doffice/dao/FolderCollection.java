package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.FolderEntity;

@Repository
public class FolderCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public FolderEntity getFolderById(String folderId) {
		Query query = Query.query(Criteria.where("id").is(folderId));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		return folder;
	}
	
	public String getMyDocumentFolderId(String userId) {
		Query query = Query.query(Criteria.where("name").is("My Documents").and("user_id").is(userId).and("is_deleted").is(false));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		if(folder == null) return null;
		String folderId = folder.getId();
		return folderId;
	}
	
	public String getPublicFolderId(String userId) {
		Query query = Query.query(Criteria.where("name").is("Công khai").and("user_id").is(userId).and("is_deleted").is(false));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		if(folder == null) return null;
		String folderId = folder.getId();
		return folderId;
	}
	
	public String getPrivateFolderId(String userId) {
		Query query = Query.query(Criteria.where("name").is("Riêng tư").and("user_id").is(userId).and("is_deleted").is(false));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		if(folder == null) return null;
		String folderId = folder.getId();
		return folderId;
	}
	
	public String getSharedFolderId(String userId) {
		Query query = Query.query(Criteria.where("name").is("Được chia sẻ").and("user_id").is(userId));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		if(folder == null) return null;
		String folderId = folder.getId();
		return folderId;
	}
	
	public void renameFolder(String folderId, String newName) {
		Query query = Query.query(Criteria.where("id").is(folderId));
		Update update = new Update();
		update.set("name", newName);
		mongoTemplate.updateFirst(query, update, FolderEntity.class);
	}
	
	public List<String> getListChildrenId(String folderId) {
		Query query = Query.query(Criteria.where("id").is(folderId).and("is_deleted").is(false));
		List<String> childrenId = mongoTemplate.findOne(query, FolderEntity.class).getChildren();
		return childrenId;
	}
	
	public List<String> addNewChild(String parentFolderId, String childFolderId) {
		Query query = Query.query(Criteria.where("id").is(parentFolderId).and("is_deleted").is(false));
		List<String> childrenId = getListChildrenId(parentFolderId);
		childrenId.add(childFolderId);
		Update update = new Update();
		update.set("children", childrenId);
		mongoTemplate.updateFirst(query, update, FolderEntity.class);
		return childrenId;
	}
	
	public void removeChild(String parentFolderId, String childId) {
		Query query = Query.query(Criteria.where("id").is(parentFolderId).and("is_deleted").is(false));
		FolderEntity folder = mongoTemplate.findOne(query, FolderEntity.class);
		List<String> childrenId = folder.getChildren();
		childrenId.remove(childId);
		Update update = new Update();
		update.set("children", childrenId);
		mongoTemplate.updateFirst(query, update, FolderEntity.class);
	}
	
	// update children when remove document
	public void deleteFolder(String folderId) {
		Query query = Query.query(Criteria.where("id").is(folderId));
		Update update = new Update();
		update.set("is_deleted", true);
		mongoTemplate.updateFirst(query, update, FolderEntity.class);
	}
}
