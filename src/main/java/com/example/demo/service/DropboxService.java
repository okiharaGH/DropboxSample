package com.example.demo.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

@Service
public class DropboxService {
	
	public DbxClientV2 getClient() {
		String ACCESS_TOKEN = "-- ACCESS TOKEN --";
		return this.getClient(ACCESS_TOKEN);
	}
	
	public DbxClientV2 getClient(HttpSession session) {
		if (session == null) return null;
		
		String token = (String)session.getAttribute("token");
		if (token == null || token.isEmpty()) return null;
		
		return this.getClient(token);
	}
	
	public DbxClientV2 getClient(String token) {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
		DbxClientV2 client = new DbxClientV2(config, token);
		
		return client;
	}
	

}