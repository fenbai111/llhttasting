package com.jlt.vote.bis.wx.sdk.pay;


import com.jlt.vote.bis.wx.sdk.common.WxSslClient;
import com.jlt.vote.bis.wx.sdk.pay.base.PaySetting;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by exizhai on 11/12/2015.
 */
public class PayWxClientFactory {

    private static PayWxClientFactory instance = null;
    private static ConcurrentHashMap<String, WxSslClient> wxClients = new ConcurrentHashMap<>();

    public static PayWxClientFactory getInstance() {
        if (instance == null) {
            instance = new PayWxClientFactory();
        }
        return instance;
    }

    public WxSslClient with(PaySetting paySetting) {
        if (!wxClients.containsKey(key(paySetting))) {
            WxSslClient wxClient = new WxSslClient();
            wxClients.putIfAbsent(key(paySetting), wxClient);
        }

        return wxClients.get(key(paySetting));
    }


    private String key(PaySetting paySetting) {
        return paySetting.getAppId() + ":" + paySetting.getMchId();
    }
}

