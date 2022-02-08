package com.datn.doffice.service;

import java.util.List;

import com.datn.doffice.entity.NoteEntity;
import com.datn.doffice.okm.OpenKMRequest;

public interface NoteService {
	
	NoteEntity createNewNote(String docId, String content, OpenKMRequest request);
	
	List<NoteEntity> getNotesByDocumentId(String docId);
}
