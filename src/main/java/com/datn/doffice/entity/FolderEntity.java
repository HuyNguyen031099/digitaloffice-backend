package com.datn.doffice.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "folder")
public class FolderEntity {
	@Id
	private String id;
	
	@Field("name")
	private String name;
	
	@Field("created")
	private String created;
	
	@Field("label")
	private String label;
	
	@Field("parent_id")
	private String parentId;
	
	@Field("user_id")
	private String userId;
	
	@Field("children")
	private List<String> children;		// save list id of children: folder, file
	
	@Field("is_deleted")
	private boolean isDeleted;
}
