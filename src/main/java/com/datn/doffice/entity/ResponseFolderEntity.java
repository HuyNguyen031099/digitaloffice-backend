package com.datn.doffice.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFolderEntity {
	
	private String id;
	
	private String name;
	
	private String created;
	
	private String userCreated;
	
	private String label;
	
	private String parentId;
	
	private List<?> children;	// can has children folder, documents 
}
