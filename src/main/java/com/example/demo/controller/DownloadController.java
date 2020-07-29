package com.example.demo.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.example.demo.service.DropboxService;

@Controller
public class DownloadController {
	
	@Autowired
	private DropboxService dropboxService; 
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav
			,@RequestParam(name = "download_path", required = false) String downloadPath
			,@RequestParam(name = "file_name", required = false) String fileName) {
		
		InputStream in = null;
		try {
			String token = "";
			
			HttpSession session = request.getSession(false);
			if(session != null) {
				token = session.getAttribute("token").toString();
			}
			
			DbxClientV2 client = dropboxService.getClient(token);
			
			DbxDownloader<FileMetadata> downloader = client.files().download(downloadPath + "/" + fileName);
			
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8") ,"ISO-8859-1") + ";filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8") +"'");
			OutputStream out = response.getOutputStream();
			
			downloader.download(out);
			out.flush();
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
		if("".equals(downloadPath)) {
			viewName = "/list/";
		} else {
			viewName = "/list" + downloadPath + "/";
		}
		
		mav.setViewName("redirect:" + viewName);
		return mav;
	}
}
