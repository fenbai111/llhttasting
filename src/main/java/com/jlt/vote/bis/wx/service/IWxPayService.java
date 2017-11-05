package com.jlt.vote.bis.wx.service;


import com.jlt.vote.bis.wx.entity.VotePayOrder;

import java.math.BigDecimal;

/**
 * 微信支付订单服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface IWxPayService {

    /**
     * 生成支付code
     * @return
     */
    String getPayCode();

    /**
     * 保存微信支付订单
     */
    void saveVotePayOrder(VotePayOrder votePayOrder);

    /**
     * 微信支付订单时间间隔校验
     * @param orderCode
     * @return
     */
    boolean isPayIntervalTooShort(String orderCode);

    /**
     * 通过out_trade_no
     * @param payCode
     * @return
     */
    VotePayOrder queryOrderByPayCode(String payCode);

    /**
     * 更新微信支付回调
     * @param payCode
     * @param nonce
     * @param tradeNo
     * @param value
     * @param buyerId
     * @param payMoney
     *@param totalFee
     * @param cashFee
     * @param timeEndString
     * @param bankType
     * @param sellerId
     * @param appId
     * @param isSubscribed        @return
     */
    int updatePayForCallBack(String payCode, String nonce, String tradeNo, String value, String buyerId, BigDecimal payMoney, BigDecimal totalFee, BigDecimal cashFee, String timeEndString, String bankType, String sellerId, String appId, String isSubscribed);
}

