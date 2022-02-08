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
@Document(collection = "note")
public class NoteEntity {
	
	@Id
	private String id;
	
	@Field("date")
	private String date;
	
	@Field("author")
	private String author;
	
	@Field("text")
	private String text;
	
	@Field("document_id")
	private String documentId;
	
	@Field("is_deleted")
	private boolean isDeleted;
}
