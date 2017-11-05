package com.jlt.vote.bis.wx;

/**
 * 微信token 类型
 * @Author gaoyan
 * @Date: 2017/8/14
 */    
public enum WxTokenTypeEnum {

	/**
	 * access_token
	 */
	ACCESS_TOKEN("ACCESS_TOKEN"),
	/**
	 * js_ticket
	 */
	JS_TICKET("JS_TICKET");

	private final String value;

	private WxTokenTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
