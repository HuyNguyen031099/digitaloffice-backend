package com.datn.doffice.service;

import java.util.List;

import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.okm.OpenKMRequest;

public interface VersionService {
	
	VersionEntity createNewVersion(String docId, OpenKMRequest request);
	
	VersionEntity getActualVersion(String docId);
	
	boolean restoreVersion(String docId, String nameVersion);
	
	List<VersionEntity> getVersionsOfDocument(String docId);
	
}
