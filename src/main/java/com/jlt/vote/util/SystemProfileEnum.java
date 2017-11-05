package com.jlt.vote.util;

/**
 * 系统环境
 * @Author gaoyan
 * @Date: 2017/7/29
 */    
public enum SystemProfileEnum {

	/**
	 * 开发环境
	 */
	DEVELOP("develop"),
	/**
	 * 生产环境
	 */
	PRODUCT("product");

	private final String value;

	private SystemProfileEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
