package com.jlt.vote.bis.wx.sdk.pay.payment.wrapper;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.RefundRequest;

/**
 * @borball on 1/13/2017.
 */
public class RefundRequestWrapper extends BaseSettings {

    @JsonUnwrapped
    private RefundRequest request;

    public RefundRequest getRequest() {
        return request;
    }

    public void setRequest(RefundRequest request) {
        this.request = request;
    }
}
