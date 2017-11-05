package com.jlt.vote.bis.wx.sdk.pay.payment.wrapper;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.RefundResponse;

/**
 * @borball on 1/13/2017.
 */
public class RefundResponseWrapper extends BaseSettings {

    @JsonUnwrapped
    private RefundResponse response;

    public RefundResponse getResponse() {
        return response;
    }

    public void setResponse(RefundResponse response) {
        this.response = response;
    }
}
