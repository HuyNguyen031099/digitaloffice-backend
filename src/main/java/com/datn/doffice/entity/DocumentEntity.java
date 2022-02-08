package com.datn.doffice.entity;

import java.util.List;

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
@Document(collection = "document")
public class DocumentEntity {
	@Id
	private String id;
	
	@Field("name_document")
	private String nameDocument;
	
	@Field("label")
	private String label;
	
	@Field("user_id")
	private String userId; // who created
	
//	@Field("category_id")
//	private String categoryId; // reject
	
	@Field("time_created")
	private String timeCreated;
	
	@Field("last_modified")
	private String lastModified; // time
	
	@Field("expired_date")
	private String expiredDate; // really need?
	
	@Field("actual_version")
	private String actualVersion;
	
	@Field("is_lock")
	private int isLock;
	
	@Field("is_subscribe")
	private int isSubscribe;
	
	@Field("folder_id")
	private String folderId;
	
	@Field("is_approved")
	private boolean isApproved;

//	@Field("admin_id")
//	private String adminId; 		// who approved document. default is null.
	
	@Field("format")
	private String format;
	
	@Field("type")
	private String type;			// public or private
	
	@Field("shared")
	private List<String> shared;	// list user name who author shared with
	
	@Field("is_deleted")
	private boolean isDeleted;
	
	@Field("url")
	private String url;
}
