package com.jlt.vote.bis.wx.service.impl;

import com.jlt.vote.bis.wx.PayStatusEnum;
import com.jlt.vote.bis.wx.entity.VotePayOrder;
import com.jlt.vote.bis.wx.service.IWxPayService;
import com.jlt.vote.bis.wx.vo.WxPrePayOrder;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 微信service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxPayServiceImpl implements IWxPayService {

    public static final String date_pattern = "yyyyMMdd";

    /**
     * 两次支付间隔，单位：毫秒
     */
    private static final int MAX_PAY_INTERVAL = 3;

    @Autowired
    private BaseDaoSupport baseDaoSupport;

    /**
     * 获取支付订单编号
     *
     * @return
     */
    @Override
    public String getPayCode() {
        SimpleDateFormat formatter = new SimpleDateFormat(date_pattern);
        String dateString = formatter.format(new Date());
        return new StringBuilder("v").append(dateString)
                .append(System.currentTimeMillis())
                .append(RandomStringUtils.randomNumeric(4))
                .toString();
    }

    @Override
    public void saveVotePayOrder(VotePayOrder votePayOrder) {
        baseDaoSupport.save(votePayOrder);
    }

    /**
     * 检查同一个订单两次提交支付时间间隔
     * 小于3秒提示错误
     *
     * @param orderCode
     * @return
     */
    @Override
    public boolean isPayIntervalTooShort(String orderCode) {
        Long compareTime = System.currentTimeMillis() - MAX_PAY_INTERVAL;
        QueryBuilder query = QueryBuilder.where(Restrictions.ge("created", compareTime))
                .and(Restrictions.eq("orderCode", orderCode));
        int tooShortCount = baseDaoSupport.queryForInt(query, WxPrePayOrder.class);
        return tooShortCount > 0;
    }

    @Override
    public VotePayOrder queryOrderByPayCode(String payCode) {
        QueryBuilder query = QueryBuilder.where(Restrictions.eq("payCode", payCode))
                .and(Restrictions.eq("dataStatus", 1));
        return baseDaoSupport.query(query, VotePayOrder.class);
    }

    @Override
    public int updatePayForCallBack(String payCode, String nonce, String tradeNo, String tradeStatus, String buyerId,
                                    BigDecimal payMoney, BigDecimal totalFee, BigDecimal cashFee, String payTime, String bankType, String sellerId, String appId, String isSubscribed) {

        if (payMoney.compareTo(totalFee)==0) {
            Ssqb updateQuery = Ssqb.create("com.jlt.vote.wx.updatePayFromCallBack")
                    .setParam("appId", appId)
                    .setParam("bankType", bankType)
                    .setParam("buyerId", buyerId)
                    .setParam("cashFee", cashFee)
                    .setParam("totalFee", totalFee)
                    .setParam("isSubscribe", isSubscribed)
                    .setParam("notifyId", nonce)
                    .setParam("payStatus", PayStatusEnum.TRADE_SUCCESS.value())
                    .setParam("payTime", payTime)
                    .setParam("sellerId", sellerId)
                    .setParam("tradeNo", tradeNo)
                    .setParam("tradeStatus", tradeStatus)
                    .setParam("payCode", payCode);
            return baseDaoSupport.updateByMybatis(updateQuery);
        }
        return 0;
    }
}
