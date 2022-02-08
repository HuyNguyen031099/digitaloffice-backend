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
@Document(collection = "user_subscribe")
public class UserSubscribeEntity {
	/*
	 * user có thể theo dõi nhiều tài liệu cùng lúc
	 */
	
	@Id
	private String id;
	
	@Field("user_id")
	private String user_id;
	
	@Field("document_id")
	private String document_id;
}
