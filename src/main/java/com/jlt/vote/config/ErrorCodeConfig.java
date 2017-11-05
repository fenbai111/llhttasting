package com.jlt.vote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付模块错误码配置
 * @Author gaoyan
 * @Date: 2017/5/18
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "vote")
@PropertySource("classpath:error_message/error_message.properties")
public class ErrorCodeConfig {

    public static Map<String, String> errorMsg = new HashMap<>();

    public Map<String, String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Map<String, String> errorMsg) {
        this.errorMsg = errorMsg;
    }
}