package com.datn.doffice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.datn.doffice.dao.NotificationCollection;
import com.datn.doffice.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationCollection notificationCollection;
	
	@Override
	public void sendNotication(String userId) {
		// TODO
		// ...
	}
	
}
