package com.datn.doffice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.dto.AddNewCommentDTO;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.entity.CommentEntity;
import com.datn.doffice.entity.ResponseCommentEntity;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.service.CommentService;

@Api(path = "/api")
public class CommentController extends ApiController {
	
	@Autowired
	private CommentService commentService;
	
	@PostMapping("/comment")
	public ResponseEntity<?> addNewComment(@ModelAttribute AddNewCommentDTO ancd, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		ResponseCommentEntity rce = commentService.createComment(ancd.getText(), ancd.getVersionId(), ancd.getParentId(), user.getUserId());
		return ok(rce);
	}
	
	@GetMapping("/comment/versionid={id}")
	public ResponseEntity<?> getListCommentByVersion(@PathVariable("id") String versionId) {
		List<ResponseCommentEntity> comments = commentService.getListCommentByVersion(versionId);
		return ok(comments);
	}
	
	@PutMapping("/comment/update/{id}")
	public ResponseEntity<?> updateComment(@RequestBody AddNewCommentDTO ancd, @PathVariable("id") String commentId) {
//		System.out.println(ancd);
//		System.out.println(commentId);
		commentService.updateComment(commentId, ancd.getText());
		return ok(ApiStatus.OK);
	}
	
	@DeleteMapping("/comment/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable("id") String id) {
		commentService.deleteComment(id);
		return ok(ApiStatus.OK);
	}
	
	
}
