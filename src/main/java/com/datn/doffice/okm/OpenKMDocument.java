package com.datn.doffice.okm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.utils.OKMUtils;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Version;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OpenKMDocument {
	
	@Autowired
	private UserCollection userCollection;

	// docPath: path document in your device
	// filename: filename contain name + format (ex: taco.png,...)
	public Document createDocument(String docPath, String fldId, String filename, String username,
			String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			String currentFolderPath = ws.getFolderPath(fldId);
			InputStream is = new FileInputStream(docPath);
			Document doc = ws.createDocumentSimple(currentFolderPath + "/" + filename, is);
//			System.out.println(doc);
			return doc;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public Document getDocumentProperties(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			Document doc = ws.getDocumentProperties(docId);
//			System.out.println(doc);
			return doc;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	// checkout document to update this document and this action is only executed by one user 
	public String checkoutDocument(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			// get user who checked out
			String userId = userCollection.findByUsername(username).getId();
			Document doc = ws.getDocumentProperties(docId);
			ws.checkout(doc.getPath());
			return userId;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	// only user who checkout document can execute this action
	public void cancleCheckout(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			Document doc = ws.getDocumentProperties(docId);
			ws.cancelCheckout(doc.getPath());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	// 
	public boolean isCheckedout(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			String docPath = ws.getDocumentPath(docId);
			boolean flag = ws.isCheckedOut(docPath);
			return flag;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return false;
	}

	// docId: id document
	// destPath: path in your device where you want to save
	public boolean downloadDocument(String docId, String destPath, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			String docPath = ws.getDocumentPath(docId);
			OutputStream os = new FileOutputStream(destPath);
			InputStream is = ws.getContent(docPath);
			IOUtils.copy(is, os);
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
			return true;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return false;
	}
	
	// update document and return new versioj
	// docPath: path in your device
	public Version checkinDocument(String docId, String docPath, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			InputStream is = new FileInputStream(docPath);
			Version version = ws.checkin(docId, is, "");
			IOUtils.closeQuietly(is);
			return version;
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public void renameDocument(String docId, String newName, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			ws.renameDocument(docId, newName);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	// can use to move document to another folder or trash folder
	public void moveDocument(String docId, String destFolderId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			ws.moveDocument(docId, destFolderId);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	public void restoreVersion(String docId, String versionName, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			String docPath = ws.getDocumentPath(docId);
			ws.restoreVersion(docPath, versionName);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	// not really delete it
	public void deleteDocument(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			String docSystemPath = ws.getDocumentPath(docId);
			ws.deleteDocument(docSystemPath);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
}
