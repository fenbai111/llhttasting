package com.jlt.vote.bis.wx.service;


import com.jlt.vote.bis.wx.WxTokenTypeEnum;
import com.jlt.vote.bis.wx.entity.AccessToken;
import com.jlt.vote.bis.wx.sdk.common.jsapi.JsAPISignature;
import com.jlt.vote.bis.wx.vo.GiftWxPrePayOrder;

/**
 * 微信服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface IWxService {

    /**
     * 生成微信授权跳转url
     * @param chainId
     * @param redirectUrl
     */
    String buildWxAuthRedirect(Long chainId,String redirectUrl);

    /**
     * 生成跳转至第三方支付平台的html和脚本
     * @param giftWxPrePayOrder 可支付的对象
     * @return 跳转到第三方支付平台的html和脚本
     */
    String jsOnPay(GiftWxPrePayOrder giftWxPrePayOrder) throws Exception;

    /**
     * 处理微信支付回调
     * @param xml
     */
    String optWxPayCallback(String xml);

    /**
     * 获取微信js-sdk 分享配置信息
     * @param currentSharePageUrl  当前页面url
     * @return
     */
    JsAPISignature getShareConfigInfo(String currentSharePageUrl) throws Exception;

    /**
     * 获取微信公众号的AccessToken
     * @param type
     * @param appId
     * @return
     */
    AccessToken queryAccessTokenByType(WxTokenTypeEnum type, String appId);

    /**
     * 获取微信的js的token
     * @param appId
     * @return
     */
    AccessToken queryWxJsAccessToken(String appId);

    /**
     * 更新微信的accessToken
     * @param accessToken
     */
    void updateAccessToken(AccessToken accessToken);
}

