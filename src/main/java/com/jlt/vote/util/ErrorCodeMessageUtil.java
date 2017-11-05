package com.jlt.vote.util;


import com.jlt.vote.config.ErrorCodeConfig;

import java.text.MessageFormat;

/**
 * 错误消息的读取工具类
 * 读取
 * @author gaoyan
 * @version 1.0
 * @created 2016年1月4日 下午4:37:33
 */

public class ErrorCodeMessageUtil {

	public static String getErrorCodeMessage(String key,Object ... arguments){
		String errorMessageTemplate = ErrorCodeConfig.errorMsg.get(key);
		if((arguments == null) || (arguments.length<=0)){
			return errorMessageTemplate;
		}else{
			return MessageFormat.format(errorMessageTemplate, arguments);
		}

	}
}
