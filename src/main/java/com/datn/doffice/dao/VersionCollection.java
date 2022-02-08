package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.VersionEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class VersionCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public List<VersionEntity> getListVersionDocument(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<VersionEntity> versions = mongoTemplate.find(query, VersionEntity.class);
		return versions;
	}
	
	public VersionEntity getVersionById(String versionId) {
		Query query = Query.query(Criteria.where("id").is(versionId));
		return mongoTemplate.findOne(query, VersionEntity.class);
	}
	
	// set actual field of old version to false
	public void updateVersion(String docId, String oldVersion) {
		Query query = Query.query(Criteria.where("document_id").is(docId).and("name").is(oldVersion));
		Update update = new Update();
		update.set("actual", false);
		mongoTemplate.updateFirst(query, update, VersionEntity.class);
	}
	
	public VersionEntity getActualVersion(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId).and("actual").is(true));
		VersionEntity version = mongoTemplate.findOne(query, VersionEntity.class);
		return version;
	}
	
	public void restoreVersion(String docId, String nameVersion) {
		// find actual version and set to false
		Query query = new Query();
		query.addCriteria(
					new Criteria().andOperator(
							Criteria.where("document_id").is(docId),
							Criteria.where("actual").is(true)
							)
					);
		Update update = new Update();
		update.set("actual", false);
		mongoTemplate.updateFirst(query, update, VersionEntity.class);

		// find version need to restore and set to true
		Query query2 = new Query();
		query2.addCriteria(
					new Criteria().andOperator(
						Criteria.where("document_id").is(docId),
						Criteria.where("name").is(nameVersion)
						)
					);
		Update update2 = new Update();
		update2.set("actual", true);
		mongoTemplate.updateFirst(query2, update2, VersionEntity.class);

	}

}
