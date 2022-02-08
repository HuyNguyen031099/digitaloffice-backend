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
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "document_category")
public class DocumentCategoryEntity {
	@Id
	private String id;
	
	@Field("document_id")
	private String documentId;
	
	@Field("category_id")
	private String categoryId;
	
	@Field("is_deleted")
	private boolean isDeleted;
}
