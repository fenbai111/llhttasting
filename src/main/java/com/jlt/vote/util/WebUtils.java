package com.jlt.vote.util;

import javax.servlet.http.HttpServletRequest;


/**
 * 提供一些系统中使用到的共用方法
 * 
 * 比如获得会员信息,获得后台站点信息
 * 
 * @author liufang
 * 
 */
public class WebUtils {
	/**
	 * 用户KEY
	 */
	public static final String VOTER_OPENID = "_voter_openId";
	/**
	 * 获得用户
	 * 
	 * @param request
	 * @return
	 */
	public static String getOpenId(HttpServletRequest request) {
		return (String) request.getAttribute(VOTER_OPENID);
	}

	/**
	 * 设置用户OpenId
	 * 
	 * @param request
	 * @param openId
	 */
	public static void setOpenId(HttpServletRequest request, String openId) {
		request.setAttribute(VOTER_OPENID, openId);
	}

	
}
