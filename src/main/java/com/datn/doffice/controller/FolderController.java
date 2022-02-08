package com.datn.doffice.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.dto.CreateFolderRequestDTO;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.entity.FolderEntity;
import com.datn.doffice.entity.ResponseFolderEntity;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.service.FolderService;

@Api(path = "/api")
public class FolderController extends ApiController {

	@Autowired
	private FolderService folderService;
	
	@PostMapping("/folder")
	public ResponseEntity<?> createFolder(@ModelAttribute CreateFolderRequestDTO cfrd, HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		folderService.createFolder(cfrd.getName(), cfrd.getParentId(), user.getUserId());
		return ok(ApiStatus.OK);
	}
	
	@PostMapping("/folder/create-all")
	public ResponseEntity<?> createAll() {
		try {
			folderService.createAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(ApiStatus.OK);
	}
	
	// create my document folder is called when user is created
	
	@GetMapping("/folder/{id}")
	public ResponseEntity<?> getFolderById(@PathVariable("id") String folderId) {
		FolderEntity folder = folderService.getFolderById(folderId);
		return ok(folder);
	}
	
	@GetMapping("/folder/mydocument-id")
	public ResponseEntity<?> getMyDocumentFolderId(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		String folderId = folderService.getMyDocumentFolderId(user.getUserId());
		return ok(folderId);
	}
	
	@GetMapping("/folder/public-folder-id")
	public ResponseEntity<?> getPublicFolderId(HttpServletRequest request) {
		UserLoginDetailDTO user = getCurrentUser(request);
		String folderId = folderService.getPublicFolderId(user.getUserId());
		return ok(folderId);
	}
	
	@GetMapping("/folder/response-folder/{id}")
	public ResponseEntity<?> getResponseFolder(@PathVariable("id") String folderId) {
		ResponseFolderEntity rfe = folderService.getResponseFolderEntity(folderId, 0);
		return ok(rfe);
	}
	
	@PutMapping("/folder/{id}/{name}")
	public ResponseEntity<?> renameFolder(@PathVariable("id") String folderId, @PathVariable("name") String newName) {
		folderService.renameFolder(folderId, newName);
		return ok(ApiStatus.OK);
	}
	
	@DeleteMapping("/folder/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") String folderId) {
		folderService.delete(folderId);
		return ok(ApiStatus.OK);
	}
	
}
