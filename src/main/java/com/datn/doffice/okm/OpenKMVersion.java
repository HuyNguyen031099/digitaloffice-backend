package com.datn.doffice.okm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.VersionEntity;
import com.datn.doffice.utils.OKMUtils;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Version;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OpenKMVersion {
	
	public Version getActualVersionByDocId(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			Document doc = ws.getDocumentProperties(docId);
			return doc.getActualVersion();
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public void restoreVersion(String docId, String versionId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			ws.restoreVersion(docId, versionId);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	// get version history of document in okm system
	public List<VersionEntity> getVersionHistory(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			List<VersionEntity> vers = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			for(Version version: ws.getVersionHistory(docId)) {
				System.out.println(version);
				VersionEntity v = VersionEntity.builder()
						.name(version.getName())
						.size(version.getSize())
						.author(version.getAuthor())
						.created(formatter.format(version.getCreated().getTime()))
						.documentId(docId)
						.build()
						;
				vers.add(v);
			}
			return vers;
			
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
}
