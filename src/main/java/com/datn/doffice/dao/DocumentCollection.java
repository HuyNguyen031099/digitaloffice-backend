package com.datn.doffice.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.UserSubscribeEntity;
import com.datn.doffice.utils.FileStorageProperties;

@Repository
public class DocumentCollection {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}

	public List<DocumentEntity> getAllDocument() {
		Query query = Query.query(Criteria.where("is_deleted").is(false));
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	public List<DocumentEntity> getDocumentsApproved() {
		Query query = Query.query(Criteria.where("is_approved").is(1).and("is_deleted").is(false));
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	public List<DocumentEntity> getDocumentsByUserCreated(String username, String userId) {
		List<String> tmp = new ArrayList<String>();
		tmp.add(username);
		Criteria c1 = new Criteria().orOperator(
					Criteria.where("user_id").is(userId),
					Criteria.where("shared").in(tmp)
				);
		Criteria c2 = Criteria.where("is_deleted").is(false);
		Criteria c = new Criteria().andOperator(c1, c2);
		Query query = new Query(c);
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	public DocumentEntity getDocumentById(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		return document;
	}
	
	public List<DocumentEntity> getPrivateDocument(String userId) {
		Query query = Query.query(Criteria.where("type").is("private")
				.and("user_id").is(userId)
				.and("is_deleted").is(false));
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	public DocumentEntity getDocumentByName(String name) {
		Query query = Query.query(Criteria.where("name_document").is(name).and("is_deleted").is(false));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		return document;
	}

	public List<DocumentEntity> getTrash(String userId) {
		Criteria c1 = Criteria.where("type").is("public").and("is_deleted").is(true);
		Criteria c2 = Criteria.where("userId").is(userId).and("is_deleted").is(true);
		Criteria c = new Criteria().orOperator(c1, c2);
		Query query = Query.query(c);
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	// search
	public List<DocumentEntity> searchByName(String input, String userId, String username) {
		List<String> tmp = new ArrayList<String>();
		tmp.add(username);
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		Criteria c1 = Criteria.where("name_document").regex(input);
		Criteria c2 = Criteria.where("user_id").is(userId).and("type").is("private");
		Criteria c3 = Criteria.where("type").is("public");
		Criteria c4 = Criteria.where("is_deleted").is(false);
		Criteria c5 = Criteria.where("shared").in(tmp);
		Criteria cc1 = new Criteria().andOperator(c1, c2, c4);
		Criteria cc2 = new Criteria().andOperator(c1, c3, c4);
		Criteria cc3 = new Criteria().andOperator(c1, c4, c5);
		// get document that is public and is private of user and is shared with user
		Criteria c = new Criteria().orOperator(cc1, cc2, cc3);
		Query query = new Query(c);
		documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}
	
	public List<DocumentEntity> searchInPeriodTime(String beginDate, String endDate) {
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		Criteria c1 = Criteria.where("time_created").gte(beginDate);
		Criteria c2 = Criteria.where("time_created").lte(endDate);
		Criteria c = new Criteria().andOperator(c1, c2);
		Query query = Query.query(c);
		documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}
	
	private String reformatBeginDate(String date) {
		String[] tmp = date.split("-");
		String res = tmp[2] + "/" + tmp[1] + "/" + tmp[0] + " 00:00:00";
		return res;
	}
	
	private String reformatEndDate(String date) {
		String[] tmp = date.split("-");
		String res = tmp[2] + "/" + tmp[1] + "/" + tmp[0] + " 23:59:59";
		return res;
	}
	
	public List<DocumentEntity> advancedSearch(String name, String beginDate, String endDate, String userId, String username) {
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		List<String> tmp = new ArrayList<String>();
		tmp.add(username);
		// 2021-11-30 -> 30/11/2021 00:00:00
		if(beginDate.equals("") || endDate.equals("")) {
			return searchByName(name, userId, username);
		} else {
			Criteria c1 = Criteria.where("time_created").gte(reformatBeginDate(beginDate));
			Criteria c2 = Criteria.where("time_created").lte(reformatEndDate(endDate));
			Criteria c3 = Criteria.where("name_document").regex(name);
			Criteria c4 = Criteria.where("user_id").is(userId).and("type").is("private");
			Criteria c5 = Criteria.where("type").is("public");
			Criteria c6 = Criteria.where("is_deleted").is(false);
			Criteria c7 = Criteria.where("shared").in(tmp);
			Criteria cc1 = new Criteria().andOperator(c1, c2, c3, c4, c6);
			Criteria cc2 = new Criteria().andOperator(c1, c2, c3, c5, c6);
			Criteria cc3 = new Criteria().andOperator(c1, c2, c3, c6, c7);
			Criteria c = new Criteria().orOperator(cc1, cc2, cc3);
			Query query = Query.query(c);
			documents = mongoTemplate.find(query, DocumentEntity.class);
			return documents;
		}
	}

	public void updateVersionDocument(String docId, String newVersion) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("actual_version", newVersion);
		update.set("last_modified", getTimeNow());
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}

	private String getTimeNow() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

	public void rename(String newName, String docId, String username) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity oldDocument = mongoTemplate.findOne(query, DocumentEntity.class);

		String uploadDir = fileStorageProperties.getUploadDir();
		String privateDir = fileStorageProperties.getPrivateDir();
		String type = oldDocument.getType();
		String url = "";
		if(type.equals("public")) {
			url = uploadDir + "/" + newName;
		} else if(type.equals("private")) {
			url = privateDir + "/" + username + "/documents/" + newName;
		}

		Update update = new Update();
		update.set("name_document", newName);
		update.set("last_modified", getTimeNow());
		update.set("url", url);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}
	
	public void restoreDocument(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId).and("is_deleted").is(true));
		Update update = new Update();
		update.set("is_deleted", false);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}

	// check expired date and notify to user
	public boolean checkExpired(String documentId) {
		DocumentEntity document = getDocumentById(documentId);

		// get all documents that will be expired within 1 week
		String now = getDateNow();
		String expiredDocument = document.getExpiredDate();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			Date date1 = formatter.parse(now);
			Date date2 = formatter.parse(expiredDocument);
			long diff = date2.getTime() - date1.getTime();

			TimeUnit time = TimeUnit.DAYS;
			long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);

			if (diffrence <= 7) { // notify before one week
				return true;
			}

		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	private String getDateNow() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

	public List<DocumentEntity> getListPendingDocument() {
		Query query = Query.query(Criteria.where("is_approved").is(false)
				.and("type").is("public")
				.and("is_deleted").is(false));
		List<DocumentEntity> documents = mongoTemplate.find(query, DocumentEntity.class);
		return documents;
	}

	public void approveDocument(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("is_approved", true);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}
	
	public void rejectDocument(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		if(document != null) {
			mongoTemplate.remove(document);
		}
	}

	public void subscribe(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		int flag = document.getIsSubscribe();
		if (flag == 1)
			return; // document is subscribed, no need action
		else {
			Update update = new Update();
			update.set("is_subscribe", 1);
			mongoTemplate.updateFirst(query, update, DocumentEntity.class);
		}
	}

	public void unsubscribe(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("is_subscribe", 0);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}

	public boolean isSubscribed(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		int flag = document.getIsSubscribe();
		if (flag == 1)
			return true;
		return false;
	}

	public void lock(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("is_lock", 1);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}

	public void unlock(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId).and("is_lock").is(1));
		Update update = new Update();
		update.set("is_lock", 0);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}

	public boolean isLock(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		int flag = mongoTemplate.findOne(query, DocumentEntity.class).getIsLock();
		if (flag == 1)
			return true;
		return false;
	}
	
	public void shareDocument(String docId, List<String> usernames) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("shared", usernames);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}
	
	public void privateToPublic(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		Update update = new Update();
		update.set("type", "public");
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);
	}
	
	public boolean purgeDelete(String docId) {
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity document = mongoTemplate.findOne(query, DocumentEntity.class);
		if(document != null) {
			mongoTemplate.remove(document);
		}
		return true;
	}

	public List<String> deleteDocument(String docId) {
		// set is_deleted field to true
		Query query = Query.query(Criteria.where("id").is(docId));
		DocumentEntity documentEntity = mongoTemplate.findOne(query, DocumentEntity.class);
		List<String> usersId = new ArrayList<String>();
		
		int flag = documentEntity.getIsSubscribe();
		Update update = new Update();
		update.set("is_deleted", true);
		update.set("is_subscribe", 0);
		mongoTemplate.updateFirst(query, update, DocumentEntity.class);

		if (flag == 1) {
			// get list user was subscribed this document and send mail notify to them
			Query query2 = Query.query(Criteria.where("document_id").is(docId));
			List<UserSubscribeEntity> usersSubscribe = mongoTemplate.find(query2, UserSubscribeEntity.class);
//			System.out.println("uS:" + usersSubscribe);
			for (UserSubscribeEntity userSub : usersSubscribe) {
				usersId.add(userSub.getUser_id());
			}
		}
		return usersId;
	}
}
