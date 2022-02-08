package com.datn.doffice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "version")
public class VersionEntity {
	@Id
	private String id;
	
	@Field("name")
	private String name;
	
	@Field("size")
	private long size;
	
	@Field("author")
	private String author;
	
	@Field("actual")
	private boolean actual;
	
	@Field("created")
	private String created;
	
	@Field("previous_version_id")
	private String previousVersionId;
	
	@Field("document_id")
	private String documentId;
	
	@Field("is_deleted")
	private boolean isDeleted;
}
