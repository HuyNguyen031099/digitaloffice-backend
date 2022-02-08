package com.datn.doffice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.LockInfoEntity;

@Repository
public class LockInfoCollection {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
//	public LockInfoEntity getLockInfo(String docId, String username) {
//		Query query = Query.query(Criteria.where("document_id").is(docId).and("user_name").is(username));
//		LockInfoEntity lock = mongoTemplate.findOne(query, LockInfoEntity.class);
//		return lock;
//	}
	
	public LockInfoEntity getLockInfo(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		LockInfoEntity lock = mongoTemplate.findOne(query, LockInfoEntity.class);
		return lock;
	}
	
	public void remove(String docId, String username) {
		Query query = Query.query(Criteria.where("document_id").is(docId).and("user_name").is(username));
		LockInfoEntity lock = mongoTemplate.findOne(query, LockInfoEntity.class);
		System.out.println("unlock: " + lock);
		if(lock != null) {
			mongoTemplate.remove(lock);
		}
	}

}
