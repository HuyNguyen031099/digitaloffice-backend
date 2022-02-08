package com.datn.doffice.utils;

import com.openkm.api.OKMAuth;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OKMUtils {
	public OKMWebservices getOKMWebServices(String username, String password) {
		String url = "http://localhost:8080/OpenKM";
		OKMWebservices ws = OKMWebservicesFactory.newInstance(url, username, password);
		return ws;
	}
}
