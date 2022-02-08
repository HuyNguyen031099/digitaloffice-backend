package com.datn.doffice.okm;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.datn.doffice.utils.OKMUtils;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.bean.Note;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OpenKMNote {
	public Note addNote(String docId, String content, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			return ws.addNote(docId, content);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public List<Note> getListNote(String docId, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			return ws.listNotes(docId);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}
	
	public void setNote(String noteId, String content, String username, String password) {
		OKMWebservices ws = new OKMUtils().getOKMWebServices(username, password);
		try {
			ws.setNote(noteId, content);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
	}
	
	
}
