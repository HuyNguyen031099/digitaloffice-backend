package com.datn.doffice.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDocumentRequestDTO {
	
	private String docId;
	
	private MultipartFile file;
}
