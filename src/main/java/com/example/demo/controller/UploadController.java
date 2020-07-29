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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.example.demo.service.DropboxService;

@Controller
public class UploadController {
	
	@Autowired
	private DropboxService dropboxService; 
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav
			,@RequestParam(name = "upload_file") MultipartFile multipartFile
			,@RequestParam(name = "upload_path", required = false) String uploadPath) {
		
		InputStream in = null;
		try {
			String originalFileName = multipartFile.getOriginalFilename().replace("\\", "/");
			String fileName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
			String filePath = uploadPath + "/" + fileName;
			
			String token = "";
			
			HttpSession session = request.getSession(false);
			if(session != null) {
				token = session.getAttribute("token").toString();
			}
			
			DbxClientV2 client = dropboxService.getClient(token);
			in = multipartFile.getInputStream();
			FileMetadata metadata = client.files().uploadBuilder(filePath).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
			System.out.println(metadata.toString());
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
		if("".equals(uploadPath)) {
			viewName = "/list/";
		} else {
			viewName = "/list" + uploadPath + "/";
		}
		
		mav.setViewName("redirect:" + viewName);
		return mav;
	}
}
