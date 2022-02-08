package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.CommentEntity;

@Repository
public class CommentCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public CommentEntity getCommentById(String commentId) {
		Query query = Query.query(Criteria.where("id").is(commentId));
		CommentEntity comment = mongoTemplate.findOne(query, CommentEntity.class);
		return comment;
	}
	
	public List<CommentEntity> getListCommentByVersion(String versionId) {
		Query query = Query.query(Criteria.where("version_id").is(versionId)
				.and("is_deleted").is(false));
		query.with(Sort.by(Sort.Order.desc("created_at")));
		List<CommentEntity> comments = mongoTemplate.find(query, CommentEntity.class);
		return comments;
	}
	
	public void updateComment(String text, String commentId) {
		Query query = Query.query(Criteria.where("id").is(commentId));
		Update update = new Update();
		update.set("text", text);
		mongoTemplate.updateFirst(query, update, CommentEntity.class);
	}
	
	public void deleteComment(String commentId) {
		Query query = Query.query(Criteria.where("id").is(commentId));
		Update update = new Update();
		update.set("is_deleted", true);
		mongoTemplate.updateFirst(query, update, CommentEntity.class);
	}
	
} 
