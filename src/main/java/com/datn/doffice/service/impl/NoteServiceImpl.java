package com.datn.doffice.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.doffice.dao.NoteCollection;
import com.datn.doffice.entity.NoteEntity;
import com.datn.doffice.okm.OpenKMNote;
import com.datn.doffice.okm.OpenKMRequest;
import com.datn.doffice.service.NoteService;
import com.openkm.sdk4j.bean.Note;

@Service
public class NoteServiceImpl implements NoteService {
	
	@Autowired
	private NoteCollection noteCollection;
	
	@Autowired
	private OpenKMNote openkmNote;

	@Override
	public NoteEntity createNewNote(String docId, String content, OpenKMRequest request) {
		Note note = openkmNote.addNote(docId, content, request.getUsername(), request.getPassword());
		NoteEntity noteEntity = NoteEntity.builder()
				.author(note.getAuthor())
				.date(calendarToString(note.getDate()))
				.text(note.getText())
				.isDeleted(false)
				.documentId(docId)
				.build();
		noteCollection.insertObject(noteEntity);
		return noteEntity;
	}
	
	@Override
	public List<NoteEntity> getNotesByDocumentId(String docId) {
		return noteCollection.getNotesByDocumentId(docId);
	}
	
	private String calendarToString(Calendar calendar) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatter.format(calendar.getTime());
	}
	
}
