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
import com.dropbox.core.v2.files.DeleteResult;
import com.example.demo.service.DropboxService;

@Controller
public class DeleteController {
	
	@Autowired
	private DropboxService dropboxService; 
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav
			,@RequestParam(name = "delete_path", required = false) String deletePath
			,@RequestParam(name = "file_name", required = false) String fileName) {
		
		InputStream in = null;
		try {
			String token = "";
			
			HttpSession session = request.getSession(false);
			if(session != null) {
				token = session.getAttribute("token").toString();
			}
			
			DbxClientV2 client = dropboxService.getClient(token);
			DeleteResult result = client.files().deleteV2(deletePath + "/" + fileName);
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
		if("".equals(deletePath)) {
			viewName = "/list/";
		} else {
			viewName = "/list" + deletePath + "/";
		}
		
		mav.setViewName("redirect:" + viewName);
		return mav;
	}
}
