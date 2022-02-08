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
public class ResponseDocumentEntity {

	private String id;
	
	private String userId;
	
	private String owner;
	
	private String nameDocument;
	
	private String timeCreated;
	
	private String lastModified;
	
	private String type;
	
	private int isLock;
	
	private int isSubscribe;
	
	private boolean isApproved;
	
	private VersionEntity version;
	
	private String format;
	
	private String label;
	
	private String folderId;
	
	private String url;
	
	private List<String> shared;
}
