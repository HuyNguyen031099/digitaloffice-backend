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
@Document(collection = "category")
public class CategoryEntity {
	@Id
	private String id;
	
	@Field("code")
	private int code;
	
	@Field("name_category")
	private String nameCategory;
}
