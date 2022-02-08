package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.DocumentCategoryEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DocumentCategoryCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public List<DocumentCategoryEntity> getDocumentsByCategoryId(String categoryId) {
		Query query = Query.query(Criteria.where("category_id").is(categoryId).and("is_deleted").is(false));
		List<DocumentCategoryEntity> dces = mongoTemplate.find(query, DocumentCategoryEntity.class);
		return dces;
	}
	
	// set is_deleted => false with document is restored
	public boolean restore(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<DocumentCategoryEntity> dces = mongoTemplate.find(query, DocumentCategoryEntity.class);
		try {
			for (DocumentCategoryEntity dce : dces) {
				Query q = Query.query(Criteria.where("category_id").is(dce.getCategoryId())
						.and("document_id").is(docId));
				Update update = new Update();
				update.set("is_deleted", false);
				mongoTemplate.updateFirst(q, update, DocumentCategoryEntity.class);
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return true;
	}
	
	// set is_deleted => true with document is deleted
	public boolean delete(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<DocumentCategoryEntity> dces = mongoTemplate.find(query, DocumentCategoryEntity.class);
		for(DocumentCategoryEntity dce: dces) {
			Query q = Query.query(Criteria.where("category_id").is(dce.getCategoryId())
					.and("document_id").is(docId));
			Update update = new Update();
			update.set("is_deleted", true);
			mongoTemplate.updateFirst(q, update, DocumentCategoryEntity.class);
		}
		return true;
	}
}
