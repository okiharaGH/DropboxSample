package com.example.demo.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.demo.service.DropboxService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ListController {
	
	@Autowired
	private DropboxService dropboxService; 
	
	@RequestMapping(value = "/list/**")
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav) {
		String token = "";
		
		HttpSession session = request.getSession(false);
		if(session != null) {
			token = session.getAttribute("token").toString();
		}
		
		Map<String, String> accountMap = new HashMap<>();
		List<Map<?, ?>> folderList = new ArrayList<Map<?,?>>();
		List<Map<?, ?>> fileList = new ArrayList<Map<?,?>>();
		
		try {
			
			String uri = request.getRequestURI();
			String path = URLDecoder.decode(uri.replaceAll("^/list", "").replaceAll("/$", ""), "UTF-8");
			System.out.println(path);
			mav.addObject("dbx_path" ,path);
			
			DbxClientV2 client = dropboxService.getClient(token);
			FullAccount account = client.users().getCurrentAccount();
			accountMap.put("name", account.getName().getDisplayName());
			accountMap.put("email", account.getEmail());
			
			ObjectMapper objectMapper = new ObjectMapper();
			ListFolderResult result = client.files().listFolder(path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					Map<?, ?> map = objectMapper.readValue(metadata.toString(), Map.class);
					if("folder".equals(map.get(".tag"))) {
						folderList.add(map);
					} else if("file".equals(map.get(".tag"))) {
						fileList.add(map);
					}
				}

				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch(Exception e) {
			// do nothing
		}
		
		mav.addObject("accout_map", accountMap);
		mav.addObject("folder_list", folderList);
		mav.addObject("file_list", fileList);
		mav.setViewName("/list");
		
		return mav;
	}
}
