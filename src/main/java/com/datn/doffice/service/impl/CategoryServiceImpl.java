package com.datn.doffice.service.impl;

import com.datn.doffice.dao.CategoryCollection;
import com.datn.doffice.dao.DocumentCategoryCollection;
import com.datn.doffice.dao.DocumentCollection;
import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.dao.VersionCollection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.doffice.entity.CategoryEntity;
import com.datn.doffice.entity.DocumentCategoryEntity;
import com.datn.doffice.entity.DocumentEntity;
import com.datn.doffice.entity.ResponseCategoryEntity;
import com.datn.doffice.entity.ResponseDocumentEntity;
import com.datn.doffice.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryCollection categoryCollection;
	
	@Autowired
	private DocumentCollection documentCollection;
	
	@Autowired
	private DocumentCategoryCollection documentCategoryCollection;
	
	@Autowired
	private UserCollection userCollection;
	
	@Autowired
	private VersionCollection versionCollection;
	
	@Override
	public void createCategory(String categoryName) {
		List<CategoryEntity> categories = getAll();
		int categoryCode = categories.get(categories.size() - 1).getCode() + 1;
		CategoryEntity category = CategoryEntity.builder()
				.nameCategory(categoryName)
				.code(categoryCode)
				.build();
		categoryCollection.insertObject(category);
	}
	
	@Override
	public List<CategoryEntity> getAll() {
		return categoryCollection.getAll();
	}
	
	@Override
	public List<ResponseCategoryEntity> getResponseCategories() {
		List<ResponseCategoryEntity> rces = new ArrayList<ResponseCategoryEntity>();
		try {
			List<CategoryEntity> categories = getAll();
			for(CategoryEntity category: categories) {
				List<DocumentEntity> documents = getDocumentByCategoryId(category.getId());
				List<ResponseDocumentEntity> rdes = convert(documents);
				
				ResponseCategoryEntity rce = ResponseCategoryEntity.builder()
						.id(category.getId())
						.categoryName(category.getNameCategory())
						.listDocument(rdes)
						.build();
				rces.add(rce);
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}
		return rces;
	}
	
	private List<ResponseDocumentEntity> convert(List<DocumentEntity> documents) {
		List<ResponseDocumentEntity> rdes = new ArrayList<ResponseDocumentEntity>();
		for(DocumentEntity document: documents) {
			if(document.isApproved()) {
				String username = userCollection.findById(document.getUserId()).getUserName();
				ResponseDocumentEntity rde = ResponseDocumentEntity.builder()
						.id(document.getId())
						.userId(document.getUserId())
						.owner(username)
						.format(document.getFormat())
						.nameDocument(document.getNameDocument())
						.type(document.getType())
						.shared(document.getShared())
						.version(versionCollection.getActualVersion(document.getId()))
						.url(document.getUrl())
						.timeCreated(document.getTimeCreated())
						.lastModified(document.getLastModified())
						.isLock(document.getIsLock())
						.isSubscribe(document.getIsSubscribe())
						.build();
				rdes.add(rde);
			}
		}
		return rdes;
	}
	
	// get list document by category id
	private List<DocumentEntity> getDocumentByCategoryId(String categoryId) {
		List<DocumentCategoryEntity> dces = documentCategoryCollection.getDocumentsByCategoryId(categoryId);
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		for(DocumentCategoryEntity dce: dces) {
			DocumentEntity document = documentCollection.getDocumentById(dce.getDocumentId());
			if(document.getType().equals("public")) {
				documents.add(document);
			}
		}
		return documents;
	}
}
