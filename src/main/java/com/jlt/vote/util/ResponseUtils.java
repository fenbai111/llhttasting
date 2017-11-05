package com.jlt.vote.util;

import com.alibaba.fastjson.JSON;
import com.xcrm.common.http.ResultParseException;
import com.xcrm.common.util.ListUtil;
import com.xcrm.log.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口响应结果封装工具类
 * @Author gaoyan
 * @Date: 2017/6/8
 */
public class ResponseUtils {

    private static Logger logger = Logger.getLogger(ResponseUtils.class);

    public static void createBadResponse(HttpServletResponse response, String msg) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);
        responseMap.put("msg", msg);
        render(response,responseMap, HttpServletResponse.SC_OK);
    }

    public static void createBadResponse(HttpServletResponse response, String msg, String errorCode) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);
        responseMap.put("errorCode", errorCode);
        responseMap.put("msg", msg);
        render(response,responseMap, HttpServletResponse.SC_OK);
    }

    public static void createSuccessResponse(HttpServletResponse response, Object data) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 1);
        responseMap.put("msg", "success");
        responseMap.put("data", data);
        render(response,responseMap, HttpServletResponse.SC_OK);
    }

    public static void createValidFailResponse(HttpServletResponse response, BindingResult result){
        String msg = getBindingResult(result);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status",0);
        responseMap.put("msg",msg);
        render(response,responseMap, HttpServletResponse.SC_OK);
    }

    public static void defaultSuccessResponse(HttpServletResponse response) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 1);
        responseMap.put("msg", "success");
        render(response,responseMap, HttpServletResponse.SC_OK);
    }

    public static void createForbiddenResponse(HttpServletResponse response,String msg) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);
        responseMap.put("msg", msg);
        render(response,responseMap, HttpServletResponse.SC_FORBIDDEN);
    }

    public static void createUnauthorizedResponse(HttpServletResponse response,String msg) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);
        responseMap.put("msg", msg);
        render(response,responseMap, HttpServletResponse.SC_UNAUTHORIZED);
    }


    /**
     * 发送内容,默认使用UTF-8编码。
     * @param response
     * @param resultMap
     * @param httpStatus
     */
    public static void render(HttpServletResponse response, Map<String,Object> resultMap, int httpStatus) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setStatus(httpStatus);
        String resultJson = JSON.toJSONString(resultMap);
        logger.info("response data :"+resultJson);
        try {
            response.setContentLength(resultJson.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e1) {
            logger.error("render exception：",e1);
        }
        try {
            response.getWriter().write(resultJson);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getBindingResult(BindingResult result){
        List<String> fieldErrorMsgList = new ArrayList<String>();
        List<String> globalErrorMsgList = new ArrayList<String>();
        HashMap<String,List<String>> resultMap = new HashMap<String,List<String>>();
        if(result.getFieldErrorCount()>0){
            List<FieldError> fieldErrorList = result.getFieldErrors();
            if(ListUtil.isNotEmpty(fieldErrorList)){
                for(FieldError fieldError:fieldErrorList){
                    fieldErrorMsgList.add(fieldError.getDefaultMessage());
                }
            }
            if(ListUtil.isNotEmpty(fieldErrorMsgList)){
                resultMap.put("fieldError", fieldErrorMsgList);
            }
        }

        if(result.getGlobalErrorCount()>0){
            List<ObjectError> globalErrorList = result.getGlobalErrors();
            if(ListUtil.isNotEmpty(globalErrorList)){
                for(ObjectError objectError:globalErrorList){
                    globalErrorMsgList.add(objectError.getDefaultMessage());
                }
            }
            if(ListUtil.isNotEmpty(globalErrorMsgList)){
                resultMap.put("globalError", globalErrorMsgList);
            }
        }

        return JSON.toJSONString(resultMap);
    }

}
