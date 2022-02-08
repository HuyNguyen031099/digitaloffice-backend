package com.datn.doffice.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.doffice.dao.CommentCollection;
import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.entity.CommentEntity;
import com.datn.doffice.entity.ResponseCommentEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.service.CommentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
	
	@Autowired
	private CommentCollection commentCollection;
	
	@Autowired
	private UserCollection userCollection;
	
	@Override
	public ResponseCommentEntity createComment(String text, String versionId, String parentId, String userId) {
		CommentEntity comment = CommentEntity.builder()
				.userId(userId)
				.parentId(parentId)
				.versionId(versionId)
				.text(text)
				.createdAt(new Date())
				.isDeleted(false)
				.build();
		commentCollection.insertObject(comment);
		System.out.println(convert(comment));
		return convert(comment);
	}
	
	@Override
	public ResponseCommentEntity getCommentById(String commentId) {
		CommentEntity comment = commentCollection.getCommentById(commentId);
		return convert(comment);
	}
	
	@Override
	public List<ResponseCommentEntity> getListCommentByVersion(String versionId) {
		List<CommentEntity> comments = commentCollection.getListCommentByVersion(versionId);
		List<ResponseCommentEntity> rces = new ArrayList<ResponseCommentEntity>();
		try {
			for(CommentEntity comment: comments) {
				ResponseCommentEntity rce = convert(comment);
				rces.add(rce);
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
//		System.out.println("rces: " + rces);
		return rces;
	}
	
	@Override
	public void updateComment(String commentId, String text) {
		commentCollection.updateComment(text, commentId);
	}
	
	@Override
	public void deleteComment(String commentId) {
		commentCollection.deleteComment(commentId);
	}
	
	private ResponseCommentEntity convert(CommentEntity comment) {
		ResponseCommentEntity rce = ResponseCommentEntity.builder()
				.id(comment.getId())
				.created(comment.getCreatedAt())
				.parentId(comment.getParentId())
				.text(comment.getText())
				.versionId(comment.getVersionId())
				.userId(comment.getUserId())
				.username(userCollection.findById(comment.getUserId()).getUserName())
				.build();
		return rce;
	}
}
