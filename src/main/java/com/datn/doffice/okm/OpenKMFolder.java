package com.datn.doffice.okm;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.datn.doffice.utils.OKMUtils;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.bean.Folder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OpenKMFolder {

	public List<Folder> getFolderChidren(String fldId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			List<Folder> folders = ws.getFolderChildren(fldId);
			return folders;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public void createFolder(String sysPath, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			Folder fld = new Folder();
			fld.setPath(sysPath);
			ws.createFolder(fld);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
}
