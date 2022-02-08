package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.NoteEntity;

@Repository
public class NoteCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public NoteEntity getNoteById(String noteId) {
		Query query = Query.query(Criteria.where("id").is(noteId));
		return mongoTemplate.findOne(query, NoteEntity.class);
	}
	
	public List<NoteEntity> getNotesByDocumentId(String docId) {
		Query query = Query.query(Criteria.where("document_id").is(docId));
		List<NoteEntity> notes = mongoTemplate.find(query, NoteEntity.class);
		return notes;
	}
	
	public void updateNote(NoteEntity newNote, String noteId) {
		Query query = Query.query(Criteria.where("id").is(noteId));
		Update update = new Update();
		update.set("author", newNote.getAuthor());
		update.set("text", newNote.getText());

		mongoTemplate.updateFirst(query, update, NoteEntity.class);
	}
	
	public void deleteNote(String noteId) {
		Query query = Query.query(Criteria.where("id").is(noteId));
		Update update = new Update();
		update.set("is_deleted", true);
		mongoTemplate.updateFirst(query, update, NoteEntity.class);
	}
}
