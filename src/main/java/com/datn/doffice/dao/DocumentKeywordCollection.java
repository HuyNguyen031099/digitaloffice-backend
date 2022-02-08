package com.datn.doffice.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.DocumentKeywordEntity;
import com.datn.doffice.entity.KeywordEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DocumentKeywordCollection {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	// get list document by list keyword
	public List<DocumentKeywordEntity> getDocumentsByKeywords(List<String> keywords) {
		// get all object
		List<DocumentKeywordEntity> dkes = mongoTemplate.findAll(DocumentKeywordEntity.class);
		List<DocumentKeywordEntity> res = new ArrayList<DocumentKeywordEntity>();
		
		for(DocumentKeywordEntity dke: dkes) {
			List<String> kwds = dke.getKeywords();
			boolean flag = kwds.containsAll(keywords);
			if(flag) res.add(dke);
		}
		
		return res;
	}
	
	public void updateKeywords(String docId, List<String> keywords) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		Update update = new Update();
		update.set("keywords", keywords);
		mongoTemplate.updateFirst(query, update, DocumentKeywordEntity.class);
	}
	
	public void remove(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		DocumentKeywordEntity dke = mongoTemplate.findOne(query, DocumentKeywordEntity.class);
		if(dke != null) {
			mongoTemplate.remove(dke);
		}
	}
	
	public List<String> getListKeyword(String docId) {
		try {
			Query query = Query.query(Criteria.where("document_id").is(docId));
			DocumentKeywordEntity dke = mongoTemplate.findOne(query, DocumentKeywordEntity.class);
			if(dke == null) return null;
			return dke.getKeywords();
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
}
