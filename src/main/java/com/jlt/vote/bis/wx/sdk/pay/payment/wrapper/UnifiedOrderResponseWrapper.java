package com.jlt.vote.bis.wx.sdk.pay.payment.wrapper;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderResponse;

/**
 * @borball on 1/13/2017.
 */
public class UnifiedOrderResponseWrapper extends BaseSettings {
    @JsonUnwrapped
    private UnifiedOrderResponse response;

    public UnifiedOrderResponse getResponse() {
        return response;
    }

    public void setResponse(UnifiedOrderResponse response) {
        this.response = response;
    }
}