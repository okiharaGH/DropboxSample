package com.example.demo.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.example.demo.service.DropboxService;

@Controller
public class CreateFolderController {
	
	@Autowired
	private DropboxService dropboxService; 
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav
			,@RequestParam(name = "folder_name", required = false) String folderName
			,@RequestParam(name = "create_path", required = false) String createPath) {
		
		InputStream in = null;
		try {
			String token = "";
			HttpSession session = request.getSession(false);
			if(session != null) {
				token = session.getAttribute("token").toString();
			}
			
			DbxClientV2 client = dropboxService.getClient(token);
			CreateFolderResult result = client.files().createFolderV2(createPath + "/" + folderName);
			System.out.println(result.toString());
		} catch(Exception e) {
			// do nothing
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e){
					// Ignore
				}
			}
		}
		
		String viewName = "";
		if("".equals(createPath)) {
			viewName = "/list/";
		} else {
			viewName = "/list" + createPath + "/";
		}
		
		mav.setViewName("redirect:" + viewName);
		return mav;
	}
}
