package com.datn.doffice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.entity.CategoryEntity;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.service.CategoryService;

@Api(path = "/api")
public class CategoryController extends ApiController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/category")
	public ResponseEntity<?> getAll() {
		return ok(categoryService.getAll());
	}
	
	@GetMapping("/category/response-category")
	public ResponseEntity<?> getResponseCategories() {
		return ok(categoryService.getResponseCategories());
	}
	
	@PostMapping("/category/{name}")
	public ResponseEntity<?> createCategory(@PathVariable("name") String categoryName) {
		categoryService.createCategory(categoryName);
		return ok(ApiStatus.OK);
	}
	
}
