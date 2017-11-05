package com.jlt.vote.action;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.entity.CampaignAward;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.util.RequestUtils;
import com.jlt.vote.util.ResponseUtils;
import com.jlt.vote.util.WebUtils;
import com.jlt.vote.validation.ValidateFiled;
import com.jlt.vote.validation.ValidateGroup;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class VoteController {

    private static Logger logger = Logger.getLogger(VoteController.class);

    @Autowired
    private ICampaignService campaignService;

    /**
     * 首页登陆
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/home",method = {RequestMethod.GET})
    public String v_home(@PathVariable Long chainId, HttpServletRequest request, HttpServletResponse response,ModelMap model){
        logger.info("VoteController.v_home,chainId:{}",chainId);
        return "index";
    }

    /**
     * 查询活动详情接口
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/detail",method = {RequestMethod.GET})
    public void queryCampaignInfo(@PathVariable Long chainId, HttpServletRequest request, HttpServletResponse response,ModelMap model){
        logger.info("VoteController.queryCampaignInfo,chainId:{}",chainId);
        ResponseUtils.createSuccessResponse(response,campaignService.queryCampaignDetail(chainId));
    }

    /**
     * 用户详情落地页
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/v_user/{userId}",method = {RequestMethod.GET})
    public String v_user(@PathVariable Long chainId, @PathVariable Long userId,HttpServletRequest request, HttpServletResponse response,ModelMap model){
        logger.info("VoteController.v_user({},{})",chainId,userId);
        //通过chainId userId查询用户详情,同时用户热度+1,活动热度+2
        Map<String,Object> userDetail = campaignService.queryUserDetail(chainId,userId);
        userDetail.put("chainId",chainId);
        model.addAttribute("userDetail", JSON.toJSONString(userDetail));
        return "user";
    }

    /**
     * 获取用户详情
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/user/{userId}",method = {RequestMethod.GET})
    public void queryUserBaseInfo(@PathVariable Long chainId, @PathVariable Long userId,
                         HttpServletRequest request, HttpServletResponse response,ModelMap model){
        logger.info("VoteController.queryUserBaseInfo({},{})",chainId,userId);
        //通过chainId userId查询用户详情
        Map<String,Object> userBaseDetail = campaignService.queryUserBaseDetail(chainId,userId);
        Map<String,Object> userDetailResult = new HashMap<>();
        userDetailResult.put("giftPoint",MapUtils.getObject(userBaseDetail,"giftPoint"));
        userDetailResult.put("viewCount",MapUtils.getObject(userBaseDetail,"viewCount"));
        userDetailResult.put("voteCount",MapUtils.getObject(userBaseDetail,"voteCount"));
        ResponseUtils.createSuccessResponse(response,userDetailResult);
    }

    /**
     * 活动奖品落地页
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
    @RequestMapping(value ="/vote/{chainId}/v_award",method = {RequestMethod.GET})
    public String v_award(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response,ModelMap model) {
        logger.info("VoteController.v_award({})",chainId);
        List<CampaignAward> campaignAwards = campaignService.queryCampaignAward(chainId);
        Map<String, Object> awardResultMap = new HashMap<>();
        awardResultMap.put("campaignAwards", campaignAwards);
        awardResultMap.put("chainId", chainId);
        model.addAttribute("campaignAwards", JSON.toJSONString(awardResultMap));
        model.addAttribute("campaignRule", campaignService.queryCampaignRule(chainId));
        return "award";
    }

//    /**
//     * 查询活动奖品信息
//     * @param request
//     * @param response
//     */
//    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
//    @RequestMapping(value ="/vote/{chainId}/award",method = {RequestMethod.GET})
//    public void queryCampaignAward(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response){
//        logger.info("VoteController.queryCampaignAward({})",chainId);
//        //通过chainId 查询 活动奖品信息
//        ResponseUtils.createSuccessResponse(response,campaignService.queryCampaignAward(chainId));
//    }

    /**
     * 查询活动礼物信息
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
    @RequestMapping(value ="/vote/{chainId}/gift",method = {RequestMethod.GET})
    public void queryCampaignGift(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.queryCampaignGift({})",chainId);
        //通过chainId 查询 活动礼物信息
        ResponseUtils.createSuccessResponse(response,campaignService.queryCampaignGiftList(chainId));
    }

    /**
     * 首页用户列表
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 2, notNull = true, desc = "页码"),
            @ValidateFiled(index = 3, notNull = true, desc = "页大小")})
    @RequestMapping(value ="/vote/{chainId}/users",method = {RequestMethod.GET})
    public void getVoteUserList(@PathVariable Long chainId,String queryKey, Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.getVoteUserList({},{},{},{})",chainId,queryKey,pageNo,pageSize);
        ResponseUtils.createSuccessResponse(response,campaignService.querySimpleUserList(chainId,queryKey, pageNo, pageSize));
    }

//    /**
//     * 查询用户详情
//     * @param request
//     * @param response
//     */
//    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
//            @ValidateFiled(index = 1, notNull = true, desc = "用户id")})
//    @RequestMapping(value ="/vote/{chainId}/user",method = {RequestMethod.GET})
//    public void getVoteUserDetail(@PathVariable Long chainId,Long userId, HttpServletRequest request, HttpServletResponse response){
//        logger.info("VoteController.getVoteUserDetail({},{})",chainId,userId);
//        ResponseUtils.createSuccessResponse(response,campaignService.queryUserDetail(chainId,userId));
//    }

    /**
     * 查询用户礼物列表
     * @param chainId
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param voteTimestamp
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 1, notNull = true, desc = "用户id"),
            @ValidateFiled(index = 2, notNull = true, desc = "页码"),
            @ValidateFiled(index = 3, notNull = true, desc = "页大小")})
    @RequestMapping(value ="/vote/{chainId}/gifts",method = {RequestMethod.GET})
    public void getUserGiftList(@PathVariable Long chainId,Long userId,Integer pageNo, Integer pageSize,Long voteTimestamp,
                                HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.getUserGiftList({},{},{},{},{})",chainId,userId,pageNo,pageSize,voteTimestamp);
        if(Objects.isNull(voteTimestamp)){
            voteTimestamp = DateFormatUtils.addDate(DateFormatUtils.getNow(),1).getTime();
        }
        ResponseUtils.createSuccessResponse(response,campaignService.queryUserGiftList(chainId,userId,pageNo,pageSize,voteTimestamp));
    }

    /**
     * 投票人 投票
     * @param request
     * @param response
     */
    @RequestMapping(value ="/vote/{chainId}/common_vote",method = {RequestMethod.POST})
    public void vote(@PathVariable Long chainId,Long userId,HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.vote({},{})",chainId,userId);
        String openId = WebUtils.getOpenId(request);
        int result = campaignService.vote(chainId,openId,userId, RequestUtils.getIpAddr(request));
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("result",result);
        ResponseUtils.createSuccessResponse(response,resultMap);
    }


    /**
     * 查询投票排行
     * @param chainId
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id"),
            @ValidateFiled(index = 1, notNull = true, desc = "页码"),
            @ValidateFiled(index = 2, notNull = true, desc = "页大小")})
    @RequestMapping(value ="/vote/{chainId}/rank",method = {RequestMethod.GET})
    public void voteRank(@PathVariable Long chainId, Integer pageNo, Integer pageSize, ModelAndView mav,HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.voteRank({})",chainId);
        ResponseUtils.createSuccessResponse(response,campaignService.getVoteRank(chainId,pageNo, pageSize));
    }


    /**
     * 活动奖品落地页
     * @param request
     * @param response
     */
    @ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, desc = "活动id")})
    @RequestMapping(value ="/vote/{chainId}/v_rank",method = {RequestMethod.GET})
    public String v_rank(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response,ModelMap model) {
        logger.info("VoteController.v_rank({})",chainId);
        List<CampaignAward> campaignAwards = campaignService.queryCampaignAward(chainId);
        Map<String, Object> awardResultMap = new HashMap<>();
        awardResultMap.put("campaignAwards", campaignAwards);
        awardResultMap.put("chainId", chainId);
        model.addAttribute("userDetail", JSON.toJSONString(awardResultMap));
        return "rank";
    }


    @RequestMapping(value ="/vote/{chainId}/test",method = {RequestMethod.GET})
    public void test(@PathVariable Long chainId,HttpServletRequest request, HttpServletResponse response,ModelMap model) {
        throw new NullPointerException();
    }


    @RequestMapping(value ="/redis/keys/delete",method = {RequestMethod.GET})
    public void deleteRedisKeys(String auth, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.deleteRedisKeys,deleteRedisKeys:{}",auth);
        if(auth.equals("YNuO1BZqdPOIWGO1loTMo21Y")){
            campaignService.deleteAllRedisKeys();
        }
        ResponseUtils.defaultSuccessResponse(response);
    }

    @RequestMapping(value ="/redis/keys/delete2",method = {RequestMethod.GET})
    public void deleteRedisKeys2(String auth, HttpServletRequest request, HttpServletResponse response){
        logger.info("VoteController.deleteRedisKeys,deleteRedisKeys:{}",auth);
        if(auth.equals("YNuO1BZqdPOIWGO1loTMo21Y")){
            campaignService.deleteAllRedisKeys2();
        }
        ResponseUtils.defaultSuccessResponse(response);
    }

    @RequestMapping(value ="/test",method = {RequestMethod.GET})
    public void test(HttpServletRequest request, HttpServletResponse response){
        campaignService.operateRedisKey();
        ResponseUtils.defaultSuccessResponse(response);
    }

    @RequestMapping(value = "/vote/v_end", method= RequestMethod.GET)
    public String v_end(HttpServletRequest request ,HttpServletResponse response){
        logger.info("--------------/vote/v_end({})--------------------");
        return "end";
    }

    @RequestMapping(value = "/vote/v_info", method= RequestMethod.GET)
    public String v_info(HttpServletRequest request ,HttpServletResponse response){
        logger.info("--------------/vote/v_info({})--------------------");
        return "info";
    }


}
