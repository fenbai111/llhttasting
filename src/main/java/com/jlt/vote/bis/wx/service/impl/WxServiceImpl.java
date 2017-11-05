package com.jlt.vote.bis.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.entity.UserVoteRecord;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.campaign.vo.CampaignGiftDetailVo;
import com.jlt.vote.bis.wx.PayStatusEnum;
import com.jlt.vote.bis.wx.WxTokenTypeEnum;
import com.jlt.vote.bis.wx.entity.AccessToken;
import com.jlt.vote.bis.wx.entity.UserGiftRecord;
import com.jlt.vote.bis.wx.entity.VotePayOrder;
import com.jlt.vote.bis.wx.sdk.common.decrypt.AesException;
import com.jlt.vote.bis.wx.sdk.common.decrypt.SHA1;
import com.jlt.vote.bis.wx.sdk.common.jsapi.JsAPISignature;
import com.jlt.vote.bis.wx.sdk.common.util.RandomStringGenerator;
import com.jlt.vote.bis.wx.sdk.common.util.XmlObjectMapper;
import com.jlt.vote.bis.wx.sdk.jsapis.JsAPIs;
import com.jlt.vote.bis.wx.sdk.pay.base.PaySetting;
import com.jlt.vote.bis.wx.sdk.pay.payment.Payments;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.PaymentNotification;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderRequest;
import com.jlt.vote.bis.wx.sdk.pay.payment.bean.UnifiedOrderResponse;
import com.jlt.vote.bis.wx.sdk.pay.util.SignatureUtil;
import com.jlt.vote.bis.wx.service.IWxPayService;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.GiftWxPrePayOrder;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.exception.VoteRuntimeException;
import com.jlt.vote.util.CacheConstants;
import com.jlt.vote.util.CommonConstants;
import com.jlt.vote.util.HTTPUtil;
import com.jlt.vote.util.RedisDaoSupport;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.common.util.EncryptUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 微信service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxServiceImpl implements IWxService {

    private static Logger logger = Logger.getLogger(WxServiceImpl.class);

    /**
     * 微信回调返回处理成功
     */
    public static final String RET_S = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    /**
     * 微信回调返回处理失败
     */
    public static final String RET_F = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";

    @Autowired
    private IWxPayService wxPayService;

    @Autowired
    private ICampaignService campaignService;

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport baseDaoSupport;

    @Autowired
    private RedisDaoSupport redisDaoSupport;



    @Override
    public String buildWxAuthRedirect(Long chainId, String redirectUrl) {
        StringBuilder wxAuthUrl = new StringBuilder(sysConfig.getWxAuthUrl());
        wxAuthUrl.append("?appid=").append(sysConfig.getWxAppId());
        wxAuthUrl.append("&redirect_uri=").append(URLEncoder.encode(sysConfig.getWxCallbackUrl()));
        wxAuthUrl.append("&response_type=code");
        wxAuthUrl.append("&scope=snsapi_userinfo");
        String state = URLEncoder.encode(redirectUrl);
        wxAuthUrl.append("&state=").append(state);
        wxAuthUrl.append("#wechat_redirect");
        return wxAuthUrl.toString();
    }

    @Override
    public String jsOnPay(GiftWxPrePayOrder giftWxPrePayOrder) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String noncestr = RandomStringGenerator.getRandomStringByLength(16);
        //订单编号
        String orderCode = giftWxPrePayOrder.getOrderCode();
        //支付订单流水号
        String payCode = wxPayService.getPayCode();
        String openId = giftWxPrePayOrder.getOpenId();
        UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
        unifiedOrderRequest.setBody(giftWxPrePayOrder.getTitle());
        unifiedOrderRequest.setTradeNumber(payCode);
        unifiedOrderRequest.setTotalFee(getWxPayMoney(giftWxPrePayOrder.getPayMoney()));
        unifiedOrderRequest.setBillCreatedIp("127.0.0.1");
        unifiedOrderRequest.setNotifyUrl(sysConfig.getWxPayCallbackUrl());
        unifiedOrderRequest.setTradeType("JSAPI");
        unifiedOrderRequest.setOpenId(openId);
        PaySetting paySetting = getPaySetting();
        UnifiedOrderResponse response = Payments.with(paySetting).unifiedOrder(unifiedOrderRequest);

        Map<String, Object> params2 = new TreeMap<>();
        params2.put("appId", sysConfig.getWxAppId());
        params2.put("timeStamp", timestamp);
        params2.put("nonceStr", noncestr);
        params2.put("package", "prepay_id=" + response.getPrepayId());
        params2.put("signType", "MD5");
        String paySign = SignatureUtil.sign(params2, sysConfig.getWxMerchantKey());
        params2.put("paySign", paySign);

        //save db
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Long chainId = giftWxPrePayOrder.getChainId();
        VotePayOrder pay = new VotePayOrder();
        pay.setBuyerId(openId);
        pay.setCreated(now);
        pay.setOrderCode(orderCode);
        pay.setPayCode(payCode);
        pay.setPayMoney(giftWxPrePayOrder.getPayMoney());
        pay.setPayStatus(PayStatusEnum.WAIT_BUYER_PAY.value());
        pay.setChainId(chainId);
        pay.setGiftId(giftWxPrePayOrder.getGiftId());
        pay.setGiftCount(giftWxPrePayOrder.getGiftCount());
        pay.setUserId(giftWxPrePayOrder.getUserId());
        pay.setTitle(giftWxPrePayOrder.getTitle());
        wxPayService.saveVotePayOrder(pay);

        //保存礼物记录 dataStatus= 0
        UserGiftRecord userGiftRecord = new UserGiftRecord();
        userGiftRecord.setChainId(chainId);
        userGiftRecord.setDataStatus(false);
        userGiftRecord.setGiftCount(giftWxPrePayOrder.getGiftCount());
        userGiftRecord.setGiftId(giftWxPrePayOrder.getGiftId());
        userGiftRecord.setGiftName(giftWxPrePayOrder.getGiftName());

        userGiftRecord.setOpenId(openId);
        userGiftRecord.setOrderId(pay.getId());
        userGiftRecord.setUserId(giftWxPrePayOrder.getUserId());
        userGiftRecord.setVoteTime(now);
        userGiftRecord.setVoteTimestamp(System.currentTimeMillis());
        userGiftRecord.setRemark(giftWxPrePayOrder.getRemark());
        campaignService.saveUserGiftRecord(userGiftRecord);
        return JSON.toJSONString(params2);
    }

    @Override
    public String optWxPayCallback(String xml) {
        try {
            PaymentNotification paymentNotification = XmlObjectMapper.nonEmptyMapper().fromXml(xml, PaymentNotification.class);
            if (paymentNotification == null || paymentNotification.getReturnCode() == null) {
                logger.error("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
                return RET_F;
            }

            if (paymentNotification.getReturnCode().equals("FAIL")) {
                //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
                logger.error("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法, return_msg={}", paymentNotification.getReturnMessage());
                return RET_F;
            }

            PaySetting paySetting = getPaySetting();
            if (!Payments.with(paySetting).checkSignature(paymentNotification)) {
                logger.error("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                return RET_F;
            }

            String payCode = paymentNotification.getTradeNumber();
            String tradeNo = paymentNotification.getTransactionId();
            String nonce = paymentNotification.getNonce();
            BigDecimal totalFee = BigDecimal.valueOf(paymentNotification.getTotalFee()).divide(BigDecimal.valueOf(100));
            BigDecimal cashFee = BigDecimal.valueOf(paymentNotification.getCashFee()).divide(BigDecimal.valueOf(100));
            String sellerId = paymentNotification.getMchId();
            String openId = paymentNotification.getOpenId();
            VotePayOrder votePayOrder = wxPayService.queryOrderByPayCode(payCode);
            if (votePayOrder == null) {
                //支付信息未找到
                return RET_F;
            }

            int result = wxPayService.updatePayForCallBack(payCode
                    , nonce
                    , tradeNo
                    , PayStatusEnum.TRADE_SUCCESS.value()
                    , openId
                    , votePayOrder.getPayMoney()
                    , totalFee
                    , cashFee
                    , paymentNotification.getTimeEndString()
                    , paymentNotification.getBankType()
                    , sellerId
                    , paymentNotification.getAppId()
                    , paymentNotification.getIsSubscribed());

            if (result > 0) {
                campaignService.updateUserGiftRecord(votePayOrder.getId(), openId);
                campaignService.updateUserGiftInfo(openId,votePayOrder.getChainId(), votePayOrder.getUserId(), votePayOrder.getGiftId(), votePayOrder.getGiftCount());
            }
            return RET_S;
        } catch (Exception e) {
            logger.error("WX-JSAPI-NOTIFY error.wx callback data: " + xml, e);
        }
        return RET_F;
    }

    @Override
    public JsAPISignature getShareConfigInfo(String currentSharePageUrl) throws Exception {
        if (StringUtils.isBlank(currentSharePageUrl)) return null;

        if (currentSharePageUrl.indexOf("#") > 0) {
            currentSharePageUrl = currentSharePageUrl.substring(0,currentSharePageUrl.indexOf("#"));
        }
        String appId = sysConfig.getWxAppId();
        AccessToken jsToken = queryWxJsAccessToken(appId);
        if (Objects.isNull(jsToken)) {
            return null;
        }
        String jsTicket = jsToken.getToken();
        long updated = jsToken.getUpdated().getTime();
        long expired = updated + Long.parseLong((jsToken.getExpired() * 1000) + "");
        long now = System.currentTimeMillis();

        if (expired <= now) {
            //已经过期了
            JsAPIs jsAPIs = JsAPIs.with(sysConfig.getWxAccessTokenUrl(),sysConfig.getJsSdkTicketUrl());
            Map accessTokenMap = jsAPIs.getWxAccessToken(appId, sysConfig.getWxAppSecret());
            if (MapUtils.isNotEmpty(accessTokenMap)) {
                String access_token = MapUtils.getString(accessTokenMap, CommonConstants.ACCESS_TOKEN);
                Integer expires_in = MapUtils.getInteger(accessTokenMap, CommonConstants.EXPIRES_IN);

                Map jsTicketMap = jsAPIs.getWxJsSdkTicket(access_token);
                if (MapUtils.isNotEmpty(jsTicketMap)) {
                    jsTicket = MapUtils.getString(jsTicketMap, "ticket");

                    AccessToken accessToken = queryAccessTokenByType(WxTokenTypeEnum.ACCESS_TOKEN, appId);
                    accessToken.setExpired(expires_in);
                    accessToken.setToken(access_token);
                    accessToken.setUpdated(new Timestamp(System.currentTimeMillis()));
                    updateAccessToken(accessToken);

                    jsToken.setExpired(expires_in);
                    jsToken.setToken(jsTicket);
                    jsToken.setUpdated(new Timestamp(System.currentTimeMillis()));
                    updateAccessToken(jsToken);
                    redisDaoSupport.set(CacheConstants.CAMPAIGN_JS_TOKEN+appId,jsToken);
                }
            }
        }
        return createJsAPISignature(appId,currentSharePageUrl, jsTicket, expired);
    }

    /**
     * 创建JsAPI签名
     * @param url
     * @return
     */
    private JsAPISignature createJsAPISignature(String appId,String url, String ticket,long expired){
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = RandomStringGenerator.getRandomStringByLength(16);
        try {
            String signature = SHA1.getSHA1("jsapi_ticket=" + ticket + "&noncestr=" + nonce + "&timestamp=" + timestamp + "&url=" + url);

            JsAPISignature jsAPISignature = new JsAPISignature();
            jsAPISignature.setAppId(appId);
            jsAPISignature.setNonce(nonce);
            jsAPISignature.setTimestamp(timestamp);
            jsAPISignature.setSignature(signature);
            jsAPISignature.setUrl(url);
            jsAPISignature.setExpired(expired);
            return jsAPISignature;
        } catch (AesException e) {
            logger.error("createJsAPISignature failed", e);
            throw new VoteRuntimeException(e.getMessage());
        }
    }

    @Override
    public AccessToken queryAccessTokenByType(WxTokenTypeEnum type, String appId) {
        Ssqb query = Ssqb.create("com.jlt.vote.wx.queryAccessTokenByType")
                .setParam("appId", appId)
                .setParam("type", type.value());
        return baseDaoSupport.findForObj(query, AccessToken.class);
    }

    @Override
    public AccessToken queryWxJsAccessToken(String appId) {
        AccessToken wxJsAccessToken = redisDaoSupport.get(CacheConstants.CAMPAIGN_JS_TOKEN+appId,AccessToken.class);
        if(Objects.isNull(wxJsAccessToken)){
            Ssqb query = Ssqb.create("com.jlt.vote.wx.queryAccessTokenByType")
                    .setParam("appId", appId)
                    .setParam("type", WxTokenTypeEnum.JS_TICKET.value());
            wxJsAccessToken =  baseDaoSupport.findForObj(query, AccessToken.class);
            if(Objects.nonNull(wxJsAccessToken)){
                redisDaoSupport.set(CacheConstants.CAMPAIGN_JS_TOKEN+appId,wxJsAccessToken);
            }
        }
       return wxJsAccessToken;
    }

    @Override
    public void updateAccessToken(AccessToken accessToken) {
        baseDaoSupport.update(accessToken);
    }

    private PaySetting getPaySetting() {
        PaySetting paySetting = new PaySetting();
        paySetting.setAppId(sysConfig.getWxAppId());
        paySetting.setKey(sysConfig.getWxMerchantKey());
        paySetting.setMchId(sysConfig.getWxMchId());
        paySetting.setWxEndpoint(sysConfig.getWxPayUrl());
        return paySetting;
    }

    /**
     * 获得微信支付金额 将系统BigDecimal（元） 转换为（分）
     *
     * @param payMoney
     * @return
     */
    public int getWxPayMoney(BigDecimal payMoney) {
        Assert.notNull(payMoney, "payMoney is required");
        BigDecimal orderPayMoney = payMoney.multiply(BigDecimal.valueOf(100L));
        return orderPayMoney.intValue();
    }

}
