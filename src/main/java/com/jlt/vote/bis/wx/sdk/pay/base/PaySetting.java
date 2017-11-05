package com.jlt.vote.bis.wx.sdk.pay.base;

/**
 * 商户信息
 *
 * Created by exizhai on 11/22/2015.
 */
public class PaySetting {

    /**
     * 商户ID
     */
    private String mchId;

    /**
     * 商户的appId
     */
    private String appId;

    /**
     * 秘钥
     */
    private String key;

    /**
     * 支付路径
     */
    private String wxEndpoint;

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWxEndpoint() {
        return wxEndpoint;
    }

    public void setWxEndpoint(String wxEndpoint) {
        this.wxEndpoint = wxEndpoint;
    }
}
