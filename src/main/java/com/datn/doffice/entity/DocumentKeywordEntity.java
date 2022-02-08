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
@Document(collection = "document_keyword")
public class DocumentKeywordEntity {

	@Id
	private String id;
	
	@Field("document_id")
	private String documentId;
	
	@Field("keyword")
	private List<String> keywords;
}
