package com.datn.doffice.service;

import java.util.List;

import com.datn.doffice.entity.ResponseCommentEntity;

public interface CommentService {
	
	ResponseCommentEntity getCommentById(String commentId);
	
	ResponseCommentEntity createComment(String text, String versionId, String parentId, String userId);
	
	List<ResponseCommentEntity> getListCommentByVersion(String versionId);
	
	void updateComment(String commentId, String text);
	
	void deleteComment(String commentId);
}
