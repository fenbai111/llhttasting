package com.jlt.vote.util;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @Description: OrderCodeCreater
 * @author Brian
 * @date Nov 27, 2013 6:14:09 PM
 * @version
 */
public class OrderCodeCreater {
	
	/**
	 * 7位随机数+订单创建时间
	 * @param service
	 * @return
	 */
	public static String createTradeNO() {
		return RandomStringUtils.randomNumeric(7) + (System.currentTimeMillis());
	}
}
