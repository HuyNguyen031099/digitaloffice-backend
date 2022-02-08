package com.datn.doffice.entity;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommentEntity {
	
	private String id;
	
	private String parentId;
	
	private String userId;
	
	private String username;
	
	private String text;
	
	private Date created;
	
	private String versionId;
	
}
