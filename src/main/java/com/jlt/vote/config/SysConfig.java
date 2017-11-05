package com.jlt.vote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 本地资源属性配置
 * @Author gaoyan
 * @Date: 2017/5/23
 */
@Configuration
@PropertySource({"classpath:sys-config.properties","classpath:sys-common-config.properties"})
public class SysConfig {

    /**
     * 环境信息
     */
    @Value("${projectProfile}")
    private String projectProfile;

    /**
     * 微信授权
     */
    @Value("${wxAuthUrl}")
    private String wxAuthUrl;

    /**
     * 微信appid
     */
    @Value("${wxAppId}")
    private String wxAppId;

    /**
     * 微信AppSecret
     */
    @Value("${wxAppSecret}")
    private String wxAppSecret;

    /**
     * 微信授权回调url
     */
    @Value("${wxCallbackUrl}")
    private String wxCallbackUrl;

    /**
     * 微信授权token的url
     */
    @Value("${wxAuthTokenUrl}")
    private String wxAuthTokenUrl;

    /**
     * 微信授权后重定向url
     */
    @Value("${wxRedirectUrl}")
    private String wxRedirectUrl;

    /**
     * 微信获取用户信息url
     */
    @Value("${wxUserInfoUrl}")
    private String wxUserInfoUrl;

    /**
     * 微信支付信息url
     */
    @Value("${wxPayUrl}")
    private String wxPayUrl;

    /**
     * 微信支付回调信息url
     */
    @Value("${wxPayCallbackUrl}")
    private String wxPayCallbackUrl;

    /**
     * 微信商户号
     */
    @Value("${wxMchId}")
    private String wxMchId;

    /**
     * 微信商户号
     */
    @Value("${wxMerchantKey}")
    private String wxMerchantKey;

    /**
     * redis的host配置
     */
    @Value("${redisHost}")
    private String redisHost;

    /**
     * redis的host配置
     */
    @Value("${redisPort}")
    private Integer redisPort;

    /**
     * redis的host配置
     */
    @Value("${voteCookieHost}")
    private String voteCookieHost;

    /**
     * 获取微信accessToken的url
     */
    @Value("${wxAccessTokenUrl}")
    private String wxAccessTokenUrl;

    /**
     * 获取微信jsSdkTicket的url
     */
    @Value("${jsSdkTicketUrl}")
    private String jsSdkTicketUrl;

    public String getWxAuthUrl() {
        return wxAuthUrl;
    }

    public void setWxAuthUrl(String wxAuthUrl) {
        this.wxAuthUrl = wxAuthUrl;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWxAppSecret() {
        return wxAppSecret;
    }

    public void setWxAppSecret(String wxAppSecret) {
        this.wxAppSecret = wxAppSecret;
    }

    public String getWxCallbackUrl() {
        return wxCallbackUrl;
    }

    public void setWxCallbackUrl(String wxCallbackUrl) {
        this.wxCallbackUrl = wxCallbackUrl;
    }

    public String getWxAuthTokenUrl() {
        return wxAuthTokenUrl;
    }

    public void setWxAuthTokenUrl(String wxAuthTokenUrl) {
        this.wxAuthTokenUrl = wxAuthTokenUrl;
    }

    public String getWxRedirectUrl() {
        return wxRedirectUrl;
    }

    public void setWxRedirectUrl(String wxRedirectUrl) {
        this.wxRedirectUrl = wxRedirectUrl;
    }

    public String getWxUserInfoUrl() {
        return wxUserInfoUrl;
    }

    public void setWxUserInfoUrl(String wxUserInfoUrl) {
        this.wxUserInfoUrl = wxUserInfoUrl;
    }

    public String getWxPayUrl() {
        return wxPayUrl;
    }

    public void setWxPayUrl(String wxPayUrl) {
        this.wxPayUrl = wxPayUrl;
    }

    public String getWxPayCallbackUrl() {
        return wxPayCallbackUrl;
    }

    public void setWxPayCallbackUrl(String wxPayCallbackUrl) {
        this.wxPayCallbackUrl = wxPayCallbackUrl;
    }

    public String getWxMchId() {
        return wxMchId;
    }

    public void setWxMchId(String wxMchId) {
        this.wxMchId = wxMchId;
    }

    public String getWxMerchantKey() {
        return wxMerchantKey;
    }

    public void setWxMerchantKey(String wxMerchantKey) {
        this.wxMerchantKey = wxMerchantKey;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public Integer getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(Integer redisPort) {
        this.redisPort = redisPort;
    }

    public String getProjectProfile() {
        return projectProfile;
    }

    public void setProjectProfile(String projectProfile) {
        this.projectProfile = projectProfile;
    }

    public String getVoteCookieHost() {
        return voteCookieHost;
    }

    public void setVoteCookieHost(String voteCookieHost) {
        this.voteCookieHost = voteCookieHost;
    }

    public String getWxAccessTokenUrl() {
        return wxAccessTokenUrl;
    }

    public void setWxAccessTokenUrl(String wxAccessTokenUrl) {
        this.wxAccessTokenUrl = wxAccessTokenUrl;
    }

    public String getJsSdkTicketUrl() {
        return jsSdkTicketUrl;
    }

    public void setJsSdkTicketUrl(String jsSdkTicketUrl) {
        this.jsSdkTicketUrl = jsSdkTicketUrl;
    }
}