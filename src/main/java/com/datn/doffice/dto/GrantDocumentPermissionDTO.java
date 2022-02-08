package com.datn.doffice.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrantDocumentPermissionDTO {
	
	private String docId;
	
	private String listUsers;
}
