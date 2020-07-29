package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

@Controller
public class IndexController {
	
	@RequestMapping(value = "/")
	public ModelAndView hello(HttpServletRequest request
			,HttpServletResponse response
			,ModelAndView mav
			,@RequestParam(name = "token" ,required = false) String token) throws DbxApiException, DbxException {
		
		HttpSession session = request.getSession(false);
		if(session == null) {
			session = request.getSession(true);
		}
		
		// アクセストークン取得フラグ
		boolean isExists = false;
		
		DbxClientV2 client = this.getDbxClient(token);
		if(client != null) {
			try {
				FullAccount account = client.users().getCurrentAccount();
				String accountName = account.getName().getDisplayName();
				if(!"".equals(accountName)) {
					isExists = true;
				}
			} catch(Exception e) {
				// 無効なアクセストークンの場合、例外
			}
		}
		
		if(isExists) {
			session.setAttribute("token", token);
			mav.setViewName("redirect:/list/");
		} else {
			mav.setViewName("index");
			
			// アクセストークンが見つからなかった場合、エラー表示
			if(token != null && !"".equals(token)) {
				mav.addObject("errmsg", "Access token not found.");
			}
		}
		
		return mav;
	}
	
	/**
	 * Dropbox APIの Access Tokenを取得する
	 * @param token
	 * @return
	 */
	public DbxClientV2 getDbxClient(String token) {
		DbxClientV2 client = null;
		if(token != null && !"".equals(token)) {
			DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
			client = new DbxClientV2(config, token);
		}
		return client;
	}
}
