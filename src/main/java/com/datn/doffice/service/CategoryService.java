package com.datn.doffice.service;

import java.util.List;

import com.datn.doffice.entity.CategoryEntity;
import com.datn.doffice.entity.ResponseCategoryEntity;

public interface CategoryService {
	
	void createCategory(String categoryName);
	
	List<CategoryEntity> getAll();
	
	List<ResponseCategoryEntity> getResponseCategories();
	
}
