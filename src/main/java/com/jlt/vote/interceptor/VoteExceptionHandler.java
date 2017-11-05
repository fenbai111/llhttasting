package com.jlt.vote.interceptor;

import com.jlt.vote.config.SysConfig;
import com.jlt.vote.email.SendMailUtil;
import com.jlt.vote.exception.VoteRuntimeException;
import com.jlt.vote.util.*;
import com.xcrm.cloud.database.db.util.StringUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 核心业务错误，模块业务异常基类
 * 继承RuntimeException 抛出时 事务回滚
 * @Author gaoyan
 * @Date: 2017/7/30
 */
@RestControllerAdvice
public class VoteExceptionHandler {

    private static Logger logger = Logger.getLogger(VoteExceptionHandler.class);

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private SysConfig sysConfig;

    @ExceptionHandler(value = { Exception.class })
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        response.setCharacterEncoding(CommonConstants.UTF8);

        String errorMessage = "系统错误";
        //读取错误码信息
        if(ex instanceof VoteRuntimeException){
            VoteRuntimeException voteRuntimeException = (VoteRuntimeException) ex;
            if(voteRuntimeException.getErrorCode() != null){
                errorMessage = ErrorCodeMessageUtil.getErrorCodeMessage(voteRuntimeException.getErrorCode(),
                        voteRuntimeException.getErrorContents());
            }else if(voteRuntimeException.getMessage() != null){
                errorMessage = voteRuntimeException.getMessage();
            }

        }else{
            logger.error("###############unknown exception ########",ex);
            //线上环境 发送异常邮件处理
            if (sysConfig.getProjectProfile().equals(SystemProfileEnum.PRODUCT.value())) {
                StringBuilder exceptionData = new StringBuilder();
                exceptionData.append("【Resource】=").append(RequestUtils.getLocation(request)).append("\n");
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String text = exceptionData + " | " + StringUtil.getStackTrace(ex);
                        SendMailUtil sendEmail = new SendMailUtil(
                                "15604090129@163.com", "jlt2016YUIOYHN", "15604090129@163.com",
                                "线上环境发生异常，请及时关注", text, "ecs-vote", "", "");
                        try {
                            sendEmail.send();
                        } catch (Exception ex) {
                            logger.error("SendMailUtil.send() error ",ex);
                        }
                    }
                });

            }
        }

        //如果是ajax请求
        if(RequestUtils.isAjaxRequest(request)){
            //如果错误消息非空 直接返还，否则返回默认系统错误
            ResponseUtils.createBadResponse(response, errorMessage);
        }else{
            ModelAndView mv = new ModelAndView();
            mv.addObject("errorMessage", errorMessage);
            //跳转错误页
            mv.setViewName("/error");
            return mv;
        }
        return null;
    }

}