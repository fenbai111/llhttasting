package com.jlt.vote.action;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.campaign.vo.CampaignGiftDetailVo;
import com.jlt.vote.bis.wx.service.IWxService;
import com.jlt.vote.bis.wx.vo.GiftWxPrePayOrder;
import com.jlt.vote.bis.wx.vo.VotePrepayRequest;
import com.jlt.vote.config.SysConfig;
import com.jlt.vote.exception.VoteRuntimeException;
import com.jlt.vote.util.*;
import com.jlt.vote.validation.ValidateFiled;
import com.jlt.vote.validation.ValidateGroup;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxController {

    private static Logger logger = Logger.getLogger(WxController.class);

    @Autowired
    private ICampaignService campaignService;

    @Autowired
    private IWxService wxService;

    @Autowired
    private SysConfig sysConfig;

    @RequestMapping(value = "/vote/v_nowx", method= RequestMethod.GET)
    public String v_s_nowx(HttpServletRequest request ,HttpServletResponse response){
        logger.info("--------------/vote/v_nowx({})--------------------");
        return "nowx";
    }

    /**
     * 微信授权 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/index",method = {RequestMethod.GET})
    public void index(@PathVariable Long chainId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("VoteController.index({})",chainId);
        if(StringUtils.isNotEmpty(WebUtils.getOpenId(request))){
            logger.info("VoteController login with open auth.openid :{}",WebUtils.getOpenId(request));
            response.sendRedirect(response.encodeRedirectURL(MessageFormat.format(sysConfig.getWxRedirectUrl(), String.valueOf(chainId))));
        }else{
            String wxAuthUrl = wxService.buildWxAuthRedirect(chainId,MessageFormat.format(sysConfig.getWxRedirectUrl(), String.valueOf(chainId)));
            logger.info("VoteController login wxAuthUrl :{}",wxAuthUrl);
            response.sendRedirect(wxAuthUrl);
        }
    }

    /**
     * 微信授权回调
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/auth/callback",method = {RequestMethod.GET})
    public void wxRedirect(String code, String state,HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("VoteController.wxRedirect({},{})",code,state);
        if(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state)) {
            String redirectUrl = String.valueOf(state);
            //通过回调获取的code,获取授权的accessToken和openId
            Map<String,Object> outhTokenParaMap = new HashMap<>();
            outhTokenParaMap.put("appid",sysConfig.getWxAppId());
            outhTokenParaMap.put("code",code);
            outhTokenParaMap.put("grant_type","authorization_code");
            outhTokenParaMap.put("secret",sysConfig.getWxAppSecret());
            Map<String,Object> resultMap = HTTPUtil.sendGet(sysConfig.getWxAuthTokenUrl(),outhTokenParaMap);

            if(MapUtils.isNotEmpty(resultMap)){
                logger.info("WxAuthController.wxRedirect resultMap:" + resultMap);
                if(StringUtils.isNotEmpty(MapUtils.getString(resultMap,"errmsg"))){
                    String errmsg = MapUtils.getString(resultMap,"errmsg","获取用户信息失败");
                    logger.error("VoteController.wxRedirect get token error :" + errmsg);
                    ResponseUtils.createBadResponse(response,errmsg);
                }else{
                    String accessToken = MapUtils.getString(resultMap,"access_token");
                    String openId = MapUtils.getString(resultMap,"openid");

                    if(StringUtils.isNotBlank(openId)) {
                        logger.info("get openId from wx and save it to cookie, openId is ={}" ,openId);
                        CookieUtils.addCookie(request, response, CommonConstants.WX_OPEN_ID_COOKIE
                                , openId, null, sysConfig.getVoteCookieHost());
                    }

                    //获取用户信息
                    Map<String,Object> userInfoParaMap = new HashMap<>();
                    userInfoParaMap.put("access_token",accessToken);
                    userInfoParaMap.put("openid",openId);
                    userInfoParaMap.put("lang","zh_CN");
                    Map<String, Object> wxUserMap = HTTPUtil.sendGet(sysConfig.getWxUserInfoUrl(),userInfoParaMap);

                    if(MapUtils.isNotEmpty(wxUserMap)){
                        if(wxUserMap.containsKey("errmsg")){
                            String errmsg = MapUtils.getString(wxUserMap,"errmsg","获取用户信息失败");
                            logger.error("WxAuthController.queryWxUser occurs error.redirectUrl:{},openId:{},userInfoParaMap:{},errmsg:{}",
                                    redirectUrl,openId,userInfoParaMap,errmsg);
                            ResponseUtils.createBadResponse(response,errmsg);
                            return;
                        }
                        logger.info("WxAuthController.queryWxUser user:" + wxUserMap);
                        //保存用户信息到redis db
                        campaignService.saveVoter(wxUserMap);
                        logger.info("WxAuthController reirect url:" + redirectUrl);
                        response.sendRedirect(response.encodeRedirectURL(redirectUrl));

                    }else{
                        logger.error("WxAuthController.queryWxUser occurs error.redirectUrl:{},openId:{},userInfoParaMap:{}",
                                redirectUrl,openId,userInfoParaMap);
                        ResponseUtils.createBadResponse(response,"获取用户信息失败");
                    }
                }

            }
        }
    }

    @RequestMapping(value = "/vote/pay/v_pay", method = RequestMethod.GET)
    public String v_pay(Long chainId,Long userId,HttpServletRequest request,HttpServletResponse response,ModelMap model) {
        logger.info("--------------/vote/v_pay({},{})--------------------",chainId,userId);
        String openId = WebUtils.getOpenId(request);
        model.put("openId", openId);
        model.put("chainId", chainId);
        //通过chainId userId查询用户详情,同时用户热度+1,活动热度+2
        Map<String,Object> userDetail = campaignService.queryUserDetail(chainId,userId);
        model.addAttribute("userDetail", JSON.toJSONString(userDetail));
        return "pay";
    }

    /**
     * 投票活动预支付
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/pay/prepay",method = {RequestMethod.POST})
    public void votePrepay(@PathVariable Long chainId,@RequestBody @Valid VotePrepayRequest votePrepayRequest,
                           BindingResult bindingResult,
                           HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.votePrepay({},{})",chainId,votePrepayRequest);
        if (bindingResult.hasErrors()) {
            ResponseUtils.createValidFailResponse(response, bindingResult);
            return;
        }
        Long giftId = votePrepayRequest.getGiftId();
        Integer giftCount = votePrepayRequest.getGiftCount();
        //查询礼物是否存在
        CampaignGiftDetailVo giftDetailVo = campaignService.queryCampaignGiftDetail(chainId,giftId);
        if(giftDetailVo == null){
            ResponseUtils.createBadResponse(response,"礼物不存在");
            return;
        }
        BigDecimal giftFee = giftDetailVo.getGiftFee();
        if((giftFee == null)||(giftFee.compareTo(BigDecimal.ZERO)<=0)){
            ResponseUtils.createBadResponse(response,"礼物金额错误");
            return;
        }

        if((giftCount == null)||(giftCount <= 0)){
            giftCount = 1;
        }

        Long userId = votePrepayRequest.getUserId();
        boolean userExist = campaignService.checkUserExist(chainId,userId);
        if(!userExist){
            ResponseUtils.createBadResponse(response,"用户不存在");
            return;
        }

        GiftWxPrePayOrder wxPrePayOrder = new GiftWxPrePayOrder();
        wxPrePayOrder.setChainId(chainId);
        wxPrePayOrder.setOrderCode(OrderCodeCreater.createTradeNO());
        wxPrePayOrder.setOpenId(WebUtils.getOpenId(request));
        wxPrePayOrder.setTitle(giftDetailVo.getGiftBaseName()+"支付");
        wxPrePayOrder.setPayMoney(giftFee.multiply(BigDecimal.valueOf(giftCount)));
        wxPrePayOrder.setGiftCount(giftCount);
        wxPrePayOrder.setGiftId(giftId);
        wxPrePayOrder.setGiftName(giftDetailVo.getGiftBaseName());
        wxPrePayOrder.setUserId(userId);
        wxPrePayOrder.setRemark(votePrepayRequest.getRemark());
        HashMap<String,Object> resultMap = new HashMap<String,Object>();
        try {

            String payResult = wxService.jsOnPay(wxPrePayOrder);
            resultMap.put("payResult", payResult);
        } catch (Exception e) {
            logger.error("VoteController.votePrepay error",e);
            throw new VoteRuntimeException("10002");
        }
        ResponseUtils.createSuccessResponse(response,resultMap);
    }

    /**
     * 微信支付回调
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/pay/callback",method = {RequestMethod.POST})
    public void wxPayCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            String xml = InputStreamUtils.InputStreamTOString(request.getInputStream(), "UTF-8");
            logger.info("~~~~~~~~~~~~~~~~~~callback_xml:" + xml);
            wxService.optWxPayCallback(xml);
        } catch (Exception e) {
            logger.error("wxPayCallback occurs exception ",e);
        }
    }

    /**
     * 获取微信jssdk配置信息
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "当前URL")})
    @RequestMapping(value ="/vote/{chainId}/jssdk_config",method = {RequestMethod.GET})
    public void queryJsSdkConfig(String currentUrl,HttpServletRequest request, HttpServletResponse response) {
        logger.info("VoteController.queryJsSdkConfig({})",currentUrl);
        try {
            ResponseUtils.createSuccessResponse(response,wxService.getShareConfigInfo(currentUrl));
        } catch (Exception e) {
            logger.error("queryJsSdkConfig occurs exception ",e);
            ResponseUtils.createBadResponse(response,"获取配置信息错误");
        }
    }

}
