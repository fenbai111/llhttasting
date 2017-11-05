package com.jlt.vote.bis.wx.sdk.pay.payment.wrapper;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderRequest;

/**
 * @borball on 1/13/2017.
 */
@JacksonXmlRootElement(localName = "xml")
public class UnifiedOrderRequestWrapper extends BaseSettings {

    @JsonUnwrapped
    private UnifiedOrderRequest request;

    public void setRequest(UnifiedOrderRequest request) {
        this.request = request;
    }

    public UnifiedOrderRequest getRequest() {
        return request;
    }
}