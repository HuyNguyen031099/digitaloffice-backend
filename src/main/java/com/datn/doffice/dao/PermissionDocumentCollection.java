package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.PermissionDocumentEntity;

@Repository
public class PermissionDocumentCollection {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	// return a list user that have permission with this document
	public List<PermissionDocumentEntity> getPermissionDocumentEntity(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<PermissionDocumentEntity> pdes = mongoTemplate.find(query, PermissionDocumentEntity.class);
		return pdes;
	}
	
	// get all document that user have granted permission
	public List<PermissionDocumentEntity> getPermissionOfUser(String username) {
		Query query = Query.query(Criteria.where("username").is(username));
		List<PermissionDocumentEntity> pdes = mongoTemplate.find(query, PermissionDocumentEntity.class);
		return pdes;
	}
	
	public PermissionDocumentEntity getPermissionDocumentOfUser(String docId, String username) {
		Query query = Query.query(Criteria.where("document_id").is(docId).and("username").is(username));
		PermissionDocumentEntity pde = mongoTemplate.findOne(query, PermissionDocumentEntity.class);
		return pde;
	}
	
	public void updatePermission(String docId, String username, List<String> permissions) {
		Query query = Query.query(Criteria.where("document_id").is(docId).and("username").is(username));
		Update update = new Update();
		update.set("permissions", permissions);
		mongoTemplate.updateFirst(query, update, PermissionDocumentEntity.class);
	}
}
