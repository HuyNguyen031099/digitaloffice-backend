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
public class ResponseCategoryEntity {
	
	private String id;
	
	private String categoryName;
	
	private List<ResponseDocumentEntity> listDocument;
}
