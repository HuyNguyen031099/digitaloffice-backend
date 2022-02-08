package com.datn.doffice.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String versionDir;
    private String privateDir;

    public String getVersionDir() {
		return versionDir;
	}

	public void setVersionDir(String versionDir) {
		this.versionDir = versionDir;
	}

	public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

	public String getPrivateDir() {
		return privateDir;
	}

	public void setPrivateDir(String privateDir) {
		this.privateDir = privateDir;
	}
}