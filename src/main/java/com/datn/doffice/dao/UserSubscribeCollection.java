package com.datn.doffice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.UserSubscribeEntity;

@Repository
public class UserSubscribeCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void addNewSubscribe(String documentId, String userId) {
		// insert newObject
		UserSubscribeEntity userSubscribe = UserSubscribeEntity.builder()
				.user_id(userId)
				.document_id(documentId)
				.build();
		mongoTemplate.insert(userSubscribe);
	}
	
	public void unsubscribe(String documentId, String userId) {
		Query query = Query.query(Criteria.where("user_id").is(userId).and("document_id").is(documentId));
		UserSubscribeEntity userSubscribe = mongoTemplate.findOne(query, UserSubscribeEntity.class);
		if(userSubscribe != null) {
			mongoTemplate.remove(userSubscribe);
		}
	}
	
	public List<UserSubscribeEntity> getListSubscribeDocument(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<UserSubscribeEntity> uids = mongoTemplate.find(query, UserSubscribeEntity.class);
		return uids;
	}
	
}
