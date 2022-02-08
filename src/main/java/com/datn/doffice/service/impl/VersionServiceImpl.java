package com.datn.doffice.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.doffice.dao.VersionCollection;
import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.okm.OpenKMRequest;
import com.datn.doffice.okm.OpenKMVersion;
import com.datn.doffice.service.VersionService;
import com.openkm.sdk4j.bean.Version;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VersionServiceImpl implements VersionService {

	@Autowired
	private VersionCollection versionCollection;
	
	@Autowired
	private OpenKMVersion openkmVersion;
	
	@Override
	public VersionEntity createNewVersion(String docId, OpenKMRequest request) {
		try {
			Version ver = openkmVersion.getActualVersionByDocId(docId, request.getUsername(), request.getPassword());
			VersionEntity version = VersionEntity.builder()
					.name(ver.getName())
					.size(ver.getSize())
					.author(ver.getAuthor())
					.documentId(docId)
					.actual(true) 	// when first upload, this field default is true
					.created(calendarToString(ver.getCreated()))
					.isDeleted(false)
					.build();
			versionCollection.insertObject(version);
			return version;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	private String calendarToString(Calendar calendar) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatter.format(calendar.getTime());
	}

	@Override
	public boolean restoreVersion(String docId, String nameVersion) {
		try {
			versionCollection.restoreVersion(docId, nameVersion);
			return true;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public VersionEntity getActualVersion(String docId) {
		return versionCollection.getActualVersion(docId);
	}
	
	@Override
	public List<VersionEntity> getVersionsOfDocument(String docId) {
		return versionCollection.getListVersionDocument(docId);
	}
}
