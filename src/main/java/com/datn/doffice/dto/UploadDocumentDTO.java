package com.datn.doffice.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDocumentDTO {
	@NotEmpty
	private MultipartFile file;
	
	@NotEmpty
	private List<String> categoriesId;
	
	private String type;
	
	private String folderId;
}
