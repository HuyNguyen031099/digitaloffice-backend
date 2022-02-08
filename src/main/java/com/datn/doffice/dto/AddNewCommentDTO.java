package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddNewCommentDTO {
	
	private String parentId;
	
	private String versionId;
	
	private String text;
}
