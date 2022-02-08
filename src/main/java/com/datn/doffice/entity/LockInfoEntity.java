package com.datn.doffice.entity;

import java.util.Date;

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
@Document(collection = "lock_info")
public class LockInfoEntity {
	
	@Id
	private String id;
	
	@Field("time_created")
	private String timeCreated;
	
	@Field("user_name")
	private String username;
	
	@Field("document_id")
	private String documentId;
}
