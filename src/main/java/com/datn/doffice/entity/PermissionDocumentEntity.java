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
@Document(collection = "permission_document")
public class PermissionDocumentEntity {

	@Id
	private String id;
	
	@Field("username")
	private String username;
	
	@Field("document_id")
	private String documentId;
	
	@Field("permissions")
	private List<String> listPermission;
	// 1: read, 2: edit, 3: delete
}
