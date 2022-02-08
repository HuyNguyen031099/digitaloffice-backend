package com.datn.doffice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KeywordCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
}
