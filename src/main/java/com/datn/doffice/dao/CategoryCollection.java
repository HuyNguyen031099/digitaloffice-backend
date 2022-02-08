package com.datn.doffice.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.datn.doffice.entity.CategoryEntity;

@Repository
public class CategoryCollection {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void insertObject(Object object) {
		mongoTemplate.insert(object);
	}
	
	public List<CategoryEntity> getAll() {
		List<CategoryEntity> categories = mongoTemplate.findAll(CategoryEntity.class);
		return categories;
	}
	
	public CategoryEntity findByCode(int code) {
		Query query = Query.query(Criteria.where("code").is(code));
		CategoryEntity category = mongoTemplate.findOne(query, CategoryEntity.class);
		return category;
	}
}
