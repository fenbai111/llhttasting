package com.jlt.vote.bis.campaign.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jlt.vote.bis.campaign.entity.Campaign;
import com.jlt.vote.bis.campaign.entity.CampaignAward;
import com.jlt.vote.bis.campaign.entity.UserVoteRecord;
import com.jlt.vote.bis.campaign.service.ICampaignService;
import com.jlt.vote.bis.campaign.vo.*;
import com.jlt.vote.bis.wx.entity.UserGiftRecord;
import com.jlt.vote.exception.VoteRuntimeException;
import com.jlt.vote.util.CacheConstants;
import com.jlt.vote.util.RedisDaoSupport;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.BaseQuery;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 投票service
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class CampaignServiceImpl implements ICampaignService {

	private static Logger logger = Logger.getLogger(CampaignServiceImpl.class);

	@Autowired
	private BaseDaoSupport baseDaoSupport;

	@Autowired
	private RedisDaoSupport redisDaoSupport;

	@Autowired
	private TaskExecutor taskExecutor;


	@Override
	public Map<String ,Object> queryCampaignDetail(Long chainId) {//redisDaoSupport.del(CacheConstants.CAMPAIGN_BASE+chainId);
		Map<String ,Object> campaignMap = redisDaoSupport.hgetAll(CacheConstants.CAMPAIGN_BASE+chainId);
		if(MapUtils.isEmpty(campaignMap)){
			if(campaignMap == null){
				campaignMap = new HashMap<>();
			}
			Ssqb queryDetailSqb = Ssqb.create("com.jlt.vote.queryCampaignDetail").setParam("chainId",chainId);
			CampaignDetailVo campaignDetail = baseDaoSupport.findForObj(queryDetailSqb,CampaignDetailVo.class);
            campaignMap.put("campaignName",campaignDetail.getCampaignName());
            campaignMap.put("campaignScroll",campaignDetail.getCampaignScroll());
			campaignMap.put("sponsorPic",campaignDetail.getSponsorPic());
			campaignMap.put("signCount",campaignDetail.getSignCount());
			campaignMap.put("voteCount",campaignDetail.getVoteCount());
			campaignMap.put("startTime",campaignDetail.getStartTime());
			campaignMap.put("sponsorIntro",campaignDetail.getSponsorIntro());
			campaignMap.put("endTime",campaignDetail.getEndTime());
			campaignMap.put("sponsorPicUrls",campaignDetail.getSponsorPicUrls());
			campaignMap.put("viewCount",campaignDetail.getViewCount() + 1);
			redisDaoSupport.hmset(CacheConstants.CAMPAIGN_BASE+chainId,campaignMap);
		}else{
			//缓存累加1
			redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"viewCount",1);
			//浏览量 加载首页累计加1
			campaignMap.put("viewCount",MapUtils.getIntValue(campaignMap,"viewCount") + 1);
		}
		//数据库中浏览量异步加1
		updateViewCount(chainId,1,null,null);
		return campaignMap;
	}

    @Override
    public Map<String, Object> queryCampaignInfo(Long chainId) {
        Map<String ,Object> campaignMap = redisDaoSupport.hgetAll(CacheConstants.CAMPAIGN_BASE+chainId);
        if(MapUtils.isEmpty(campaignMap)) {
            if (campaignMap == null) {
                campaignMap = new HashMap<>();
            }
            Ssqb queryDetailSqb = Ssqb.create("com.jlt.vote.queryCampaignDetail").setParam("chainId",chainId);
            CampaignDetailVo campaignDetail = baseDaoSupport.findForObj(queryDetailSqb,CampaignDetailVo.class);
            campaignMap.put("campaignName",campaignDetail.getCampaignName());
            campaignMap.put("campaignScroll",campaignDetail.getCampaignScroll());
            campaignMap.put("sponsorPic",campaignDetail.getSponsorPic());
            campaignMap.put("signCount",campaignDetail.getSignCount());
            campaignMap.put("voteCount",campaignDetail.getVoteCount());
            campaignMap.put("startTime",campaignDetail.getStartTime());
            campaignMap.put("sponsorIntro",campaignDetail.getSponsorIntro());
            campaignMap.put("endTime",campaignDetail.getEndTime());
            campaignMap.put("sponsorPicUrls",campaignDetail.getSponsorPicUrls());
            campaignMap.put("viewCount",campaignDetail.getViewCount());
            redisDaoSupport.hmset(CacheConstants.CAMPAIGN_BASE+chainId,campaignMap);
        }
        return campaignMap;
    }

    @Override
    public boolean checkCampaignFinish(Long chainId) {
        Integer campaignFinishFlag = redisDaoSupport.getInt(CacheConstants.CAMPAIGN_FINISH+chainId);
        if(Objects.isNull(campaignFinishFlag)){
            Map<String,Object> campaignMap = queryCampaignInfo(chainId);
            Date endTime = (Date) MapUtils.getObject(campaignMap,"endTime");
            if(!endTime.after(DateFormatUtils.getNow())){
                redisDaoSupport.setInt(CacheConstants.CAMPAIGN_FINISH+chainId,1);
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    @Override
	public String queryCampaignRule(Long chainId) {
		String campaignRule = redisDaoSupport.get(CacheConstants.CAMPAIGN_RULE+chainId,String.class);
		if(StringUtils.isEmpty(campaignRule)){
			Ssqb queryCampaignRuleSqb = Ssqb.create("com.jlt.vote.queryCampaignRule")
					.setParam("chainId",chainId);
			campaignRule = baseDaoSupport.findForObj(queryCampaignRuleSqb,String.class);
			redisDaoSupport.set(CacheConstants.CAMPAIGN_RULE+chainId,campaignRule);
		}
		return campaignRule;
	}

	@Override
	public boolean checkCampaignExist(Long chainId) {
		Map<String ,Object> campaignMap = redisDaoSupport.hgetAll(CacheConstants.CAMPAIGN_BASE+chainId);
		if(MapUtils.isEmpty(campaignMap)){
			QueryBuilder checkQb = QueryBuilder.where(Restrictions.eq("chainId",chainId))
					.and(Restrictions.eq("dataStatus",1));
			int count = baseDaoSupport.queryForInt(checkQb,Campaign.class);
			return count >0;
		}else{
			return true;
		}
	}

	@Override
	public void saveVoter(Map<String, Object> wxUserMap) {
		String openid = MapUtils.getString(wxUserMap,"openid");
		String nickname = MapUtils.getString(wxUserMap,"nickname","");
		String headimgurl = MapUtils.getString(wxUserMap,"headimgurl","");
		VoterVo voterDetail = redisDaoSupport.get(CacheConstants.VOTE_VOTER_DETAIL+openid,VoterVo.class);
		if((voterDetail == null)
				||(!Objects.equals(voterDetail.getNickname(),nickname))
				||(!Objects.equals(voterDetail.getHeadimgurl(),headimgurl))){
			VoterVo voterVo = new VoterVo();
			voterVo.setNickname(nickname);
			voterVo.setHeadimgurl(headimgurl);
			redisDaoSupport.set(CacheConstants.VOTE_VOTER_DETAIL+openid,voterVo);
			//异步保存db
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateVoter")
							.setParam("openid",openid)
							.setParam("nickname",nickname)
							.setParam("headimgurl",headimgurl)
							.setParam("rawData",JSON.toJSONString(wxUserMap));
					baseDaoSupport.updateByMybatis(updateSqb);
				}
			});
		}
	}

	@Override
	public VoterVo queryVoter(String openId) {
		VoterVo voterDetail = redisDaoSupport.get(CacheConstants.VOTE_VOTER_DETAIL+openId,VoterVo.class);
		if(Objects.isNull(voterDetail)){
			Ssqb querySqb = Ssqb.create("com.jlt.vote.queryVoter")
					.setParam("openid",openId);
			voterDetail = baseDaoSupport.findForObj(querySqb,VoterVo.class);
			if(Objects.nonNull(voterDetail)){
				redisDaoSupport.set(CacheConstants.VOTE_VOTER_DETAIL+openId,voterDetail);
			}
		}
		return voterDetail;
	}

	/**
	 * 活动浏览量增加1
	 * @param chainId
	 */
	private void updateViewCount(Long chainId,Integer incrCampaignViewCount,Long userId,Integer incrUserViewCount){
		taskExecutor.execute(new Runnable() {
                @Override
		public void run() {
			Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateCampaignViewCount")
					.setParam("chainId",chainId)
					.setParam("incrCampaignViewCount",incrCampaignViewCount)
					.setParam("userId",userId)
					.setParam("incrUserViewCount",incrUserViewCount);
			baseDaoSupport.updateByMybatis(updateSqb);
		}
	});
	}

	@Override
	public Pagination querySimpleUserList(Long chainId, String queryKey, Integer pageNo, Integer pageSize) {
		Ssqb queryUserQb = Ssqb.create("com.jlt.vote.querySimpleUserList")
				.setParam("chainId", chainId)
				.setParam("queryKey",queryKey)
                .setParam("start",(pageNo - 1)*pageSize)
                .setParam("pageSize", pageSize);
		List<Map<String, Object>> userList = baseDaoSupport.findForMapList(queryUserQb);

		Ssqb queryUserCountQb = Ssqb.create("com.jlt.vote.querySimpleUserList_count")
				.setParam("chainId", chainId)
				.setParam("queryKey", queryKey);
		Integer userCount = baseDaoSupport.findForInt(queryUserCountQb);

		return new Pagination(pageNo, pageSize, userCount, userList);
	}

	@Override
	public Map<String,Object> queryUserDetail(Long chainId, Long userId) {
		Map<String,Object> result = new HashMap<>();
		Map<String,Object> userDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
		List userPicList = redisDaoSupport.getList(CacheConstants.VOTE_USER_PICS+userId, UserPicVo.class);
		if((MapUtils.isEmpty(userDetailMap))||(ListUtil.isEmpty(userPicList))){
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryUserDetail")
					.setParam("chainId",chainId)
					.setParam("userId",userId);
			UserDetailVo userDetail = baseDaoSupport.findForObj(queryUsersSqb,UserDetailVo.class);
			result.put("name",userDetail.getName());
			result.put("number",userDetail.getNumber());
			result.put("userId",userDetail.getUserId());
			result.put("giftPoint",userDetail.getGiftPoint());
			result.put("viewCount",userDetail.getViewCount() + 1);
			result.put("voteCount",userDetail.getVoteCount());
			result.put("headPic",userDetail.getHeadPic());
			result.put("declaration",userDetail.getDeclaration());
			redisDaoSupport.set(CacheConstants.VOTE_USER_PICS+userId,userDetail.getUserPicVos());
			redisDaoSupport.hmset(CacheConstants.VOTE_USER_DETAIL+userId,result);
			result.put("userPicVos",userDetail.getUserPicVos());
		}else{
			//用户浏览量加1
			redisDaoSupport.hinc(CacheConstants.VOTE_USER_DETAIL+userId,"viewCount",1);
			result.put("userPicVos",userPicList);
			result.putAll(userDetailMap);
			result.put("viewCount",MapUtils.getInteger(result,"viewCount") + 1);
		}
		//活动浏览量缓存累加2
		redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"viewCount",2);
		//数据库中活动浏览量异步加2,用户浏览量加1
		updateViewCount(chainId,2,userId,1);
		return result;
	}

	@Override
	public Map<String, Object> queryUserBaseDetail(Long chainId, Long userId) {
		Map<String,Object> result = new HashMap<>();
		Map<String,Object> userDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
		List userPicList = redisDaoSupport.getList(CacheConstants.VOTE_USER_PICS+userId, UserPicVo.class);
		if((MapUtils.isEmpty(userDetailMap))||(ListUtil.isEmpty(userPicList))){
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryUserDetail")
					.setParam("chainId",chainId)
					.setParam("userId",userId);
			UserDetailVo userDetail = baseDaoSupport.findForObj(queryUsersSqb,UserDetailVo.class);
			result.put("name",userDetail.getName());
			result.put("number",userDetail.getNumber());
			result.put("userId",userDetail.getUserId());
			result.put("giftPoint",userDetail.getGiftPoint());
			result.put("viewCount",userDetail.getViewCount());
			result.put("voteCount",userDetail.getVoteCount());
			result.put("headPic",userDetail.getHeadPic());
            result.put("declaration",userDetail.getDeclaration());
			redisDaoSupport.set(CacheConstants.VOTE_USER_PICS+userId,userDetail.getUserPicVos());
			redisDaoSupport.hmset(CacheConstants.VOTE_USER_DETAIL+userId,result);
			result.put("userPicVos",userDetail.getUserPicVos());
		}else{
			result.put("userPicVos",userPicList);
			result.putAll(userDetailMap);
		}
		return result;
	}

	@Override
	public boolean checkUserExist(Long chainId, Long userId) {
		Map<String,Object> userDetailMap = redisDaoSupport.hgetAll(CacheConstants.VOTE_USER_DETAIL+userId);
		if(MapUtils.isEmpty(userDetailMap)){
			Ssqb queryUserCountSqb = Ssqb.create("com.jlt.vote.queryUserCount")
					.setParam("chainId",chainId)
					.setParam("userId",userId);
			int userCount = baseDaoSupport.findForInt(queryUserCountSqb);
			return userCount >0;
		}
		return true;
	}

	@Override
	public Pagination queryUserGiftList(Long chainId, Long userId, Integer pageNo, Integer pageSize, Long voteTimestamp) {
		Ssqb queryUserQb = Ssqb.create("com.jlt.vote.queryUserGiftList")
				.setParam("chainId", chainId)
				.setParam("userId", userId)
				.setParam("voteTimestamp", voteTimestamp)
                .setParam("start",(pageNo - 1)*pageSize)
                .setParam("pageSize", pageSize);
		List<Map<String, Object>> userGiftList = baseDaoSupport.findForMapList(queryUserQb);

		Ssqb queryUserGiftCountQb = Ssqb.create("com.jlt.vote.queryUserGiftList_count")
				.setParam("chainId", chainId)
				.setParam("userId", userId);
		Integer userGiftCount = baseDaoSupport.findForInt(queryUserGiftCountQb);

		return new Pagination(pageNo, pageSize, userGiftCount, userGiftList);
	}

	@Override
	public List<CampaignAward> queryCampaignAward(Long chainId) {//redisDaoSupport.del(CacheConstants.CAMPAIGN_AWARD+chainId);
		String campaignAwardStr = redisDaoSupport.get(CacheConstants.CAMPAIGN_AWARD+chainId,String.class);
        List<CampaignAward> campaignAwardList = JSONObject.parseArray(campaignAwardStr,CampaignAward.class);

		if(ListUtil.isEmpty(campaignAwardList)){
			QueryBuilder queryCamAwardQb = QueryBuilder.where(Restrictions.eq("chainId",chainId))
					.and(Restrictions.eq("dataStatus",1))
					.setOrderBys("priority", BaseQuery.OrderByType.asc);
			campaignAwardList = baseDaoSupport.queryList(queryCamAwardQb,CampaignAward.class);

			//保存redis
			redisDaoSupport.set(CacheConstants.CAMPAIGN_AWARD+chainId,JSON.toJSONString(campaignAwardList));
		}
		return campaignAwardList;
	}

	@Override
	public List<CampaignGiftVo> queryCampaignGiftList(Long chainId) {
		List<CampaignGiftVo> campaignGiftList = redisDaoSupport.getList(CacheConstants.CAMPAIGN_GIFT+chainId,CampaignGiftVo.class);
		if(ListUtil.isEmpty(campaignGiftList)){
			if(campaignGiftList == null){
				campaignGiftList = new ArrayList<>();
			}
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryCampaignGift")
					.setParam("chainId",chainId);
			List<CampaignGiftDetailVo> campaignGiftDetailList = baseDaoSupport.findForList(queryUsersSqb,CampaignGiftDetailVo.class);
			for (CampaignGiftDetailVo detailVo : campaignGiftDetailList) {
				if((detailVo.getGiftId() > 0)
						&&(detailVo.getGiftPoint() > 0)
						&&(detailVo.getVoteCount() > 0)
						&&(detailVo.getGiftFee().compareTo(BigDecimal.ZERO) > 0)){
					redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT_DETAIL+detailVo.getGiftId(),detailVo);
					CampaignGiftVo campaignGiftVo = new CampaignGiftVo();
					BeanUtils.copyProperties(detailVo,campaignGiftVo);
					campaignGiftList.add(campaignGiftVo);
				}
			}
			redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT+chainId,campaignGiftList);
		}
		return campaignGiftList;
	}

	@Override
	public CampaignGiftDetailVo queryCampaignGiftDetail(Long chainId,Long giftId) {
		CampaignGiftDetailVo detailVo = redisDaoSupport.get(CacheConstants.CAMPAIGN_GIFT_DETAIL+giftId,CampaignGiftDetailVo.class);
		if(detailVo == null){
			Ssqb queryUsersSqb = Ssqb.create("com.jlt.vote.queryCampaignGift")
					.setParam("chainId",chainId)
					.setParam("giftId",giftId);
			detailVo = baseDaoSupport.findForObj(queryUsersSqb,CampaignGiftDetailVo.class);
			if((detailVo != null)
					&&(detailVo.getGiftId() > 0)
					&&(detailVo.getGiftPoint() > 0)
					&&(detailVo.getVoteCount() > 0)
					&&(detailVo.getGiftFee() != null)
					&&(detailVo.getGiftFee().compareTo(BigDecimal.ZERO) > 0)){
				redisDaoSupport.set(CacheConstants.CAMPAIGN_GIFT_DETAIL+giftId,detailVo);
			}
		}
		return detailVo;
	}

	@Override
	public void saveUserGiftRecord(UserGiftRecord userGiftRecord) {
		String openId = userGiftRecord.getOpenId();
		if(StringUtils.isNotEmpty(openId)){
			VoterVo voterVo = queryVoter(openId);
			userGiftRecord.setHeadImgUrl(voterVo.getHeadimgurl());
			userGiftRecord.setNickName(voterVo.getNickname());
		}
		baseDaoSupport.save(userGiftRecord);
	}

	@Override
	public void updateUserGiftRecord(Long orderId,String openId) {
		Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateGiftRecord")
				.setParam("orderId",orderId)
				.setParam("openId",openId);
		baseDaoSupport.updateByMybatis(updateSqb);
	}

	@Override
	public void updateUserGiftInfo(String openId, Long chainId, Long userId, Long giftId, Integer giftCount) {
		CampaignGiftDetailVo giftDetailVo = redisDaoSupport.get(CacheConstants.CAMPAIGN_GIFT_DETAIL+giftId,CampaignGiftDetailVo.class);
		if(Objects.isNull(giftDetailVo)){
			giftDetailVo = queryCampaignGiftDetail(chainId,giftId);
		}
		if(Objects.nonNull(giftDetailVo)){
			Integer voteCount = giftDetailVo.getVoteCount()*giftCount;
			Integer giftPoint = giftDetailVo.getGiftPoint()*giftCount;
			int result = updateUserVoteInfo(userId,voteCount,giftPoint);
			if(result > 0 ){
				if(voteCount > 0){
					redisDaoSupport.hinc(CacheConstants.VOTE_USER_DETAIL+userId,"voteCount",voteCount);
					redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"voteCount",voteCount);
				}

				if(giftCount > 0){
					redisDaoSupport.hinc(CacheConstants.VOTE_USER_DETAIL+userId,"giftPoint",giftPoint);
				}
			}

            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    //增加投票人 普通投票记录
                    UserVoteRecord userVoteRecord = new UserVoteRecord();
                    userVoteRecord.setChainId(chainId);
                    userVoteRecord.setOpenId(openId);
                    userVoteRecord.setVoteType(2);
                    userVoteRecord.setUserId(userId);
                    userVoteRecord.setVoteCount(voteCount);
                    userVoteRecord.setVoteTime(DateFormatUtils.getNow());
                    baseDaoSupport.save(userVoteRecord);
                }
            });
		}
	}

	@Override
	public int updateUserVoteInfo(Long userId,Integer voteCount,Integer giftPoint) {
		Ssqb updateSqb = Ssqb.create("com.jlt.vote.updateUserVoteInfo")
				.setParam("userId",userId)
				.setParam("voteCount",voteCount)
				.setParam("giftPoint",giftPoint);
		return baseDaoSupport.updateByMybatis(updateSqb);
	}

	@Override
	public int vote(Long chainId, String openId,Long userId,String ipAddress) {
        if(Objects.isNull(chainId)
                ||Objects.isNull(openId)
                ||Objects.isNull(userId)){
            logger.error("common_vote error.chainId:{},userId:{},ipAddress:{}",chainId,userId,ipAddress);
            throw new VoteRuntimeException("10000");
        }
		//查看 openId 是否存在
		VoterVo voterVo = queryVoter(openId);
		if(Objects.isNull(voterVo)){
			logger.error("common_vote error.chainId:{},openid:{},userId:{},ipAddress:{}",chainId,openId,userId,ipAddress);
			throw new VoteRuntimeException("10000");
		}

        //每人每天最多一票
        Integer dayUserVoteCount = redisDaoSupport.getInt(CacheConstants.CAMPAIGN_VOTER_USER_COUNT+chainId+"_"+openId+"_"+userId);
        if((Objects.nonNull(dayUserVoteCount))&&(dayUserVoteCount >= 1)){
//			throw new VoteRuntimeException("10003");
            return 1;
        }

		//每人每天最多一票
		Integer dayVoteCount = redisDaoSupport.getInt(CacheConstants.CAMPAIGN_VOTER_COUNT+chainId+"_"+openId);
		if((Objects.nonNull(dayVoteCount))&&(dayVoteCount >= 1)){
//			throw new VoteRuntimeException("10001");
			return 0;
		}


		Date now = DateFormatUtils.getNow();
		redisDaoSupport.incr(CacheConstants.CAMPAIGN_VOTER_COUNT+chainId+"_"+openId,1);
		redisDaoSupport.expire(CacheConstants.CAMPAIGN_VOTER_COUNT+chainId+"_"+openId,
				DateFormatUtils.getLastTimeOfDay(new Date()).getTime() - now.getTime());

		redisDaoSupport.incr(CacheConstants.CAMPAIGN_VOTER_USER_COUNT+chainId+"_"+openId+"_"+userId,1);
		redisDaoSupport.expire(CacheConstants.CAMPAIGN_VOTER_USER_COUNT+chainId+"_"+openId+"_"+userId,
				DateFormatUtils.getLastTimeOfDay(new Date()).getTime() - now.getTime());
		redisDaoSupport.hinc(CacheConstants.VOTE_USER_DETAIL+userId,"voteCount",1);
		redisDaoSupport.hinc(CacheConstants.CAMPAIGN_BASE+chainId,"voteCount",1);
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				updateUserVoteInfo(userId,1,0);
				//增加投票人 普通投票记录
				UserVoteRecord userVoteRecord = new UserVoteRecord();
				userVoteRecord.setChainId(chainId);
				userVoteRecord.setIpAddress(ipAddress);
				userVoteRecord.setOpenId(openId);
				userVoteRecord.setUserId(userId);
				userVoteRecord.setVoteCount(1);
				userVoteRecord.setVoteTime(now);
				baseDaoSupport.save(userVoteRecord);
			}
		});
		return 2;
	}

	@Override
	public Pagination getVoteRank(Long chainId, Integer pageNo, Integer pageSize) {
		Ssqb queryRankSqb = Ssqb.create("com.jlt.vote.getVoteRank")
				.setParam("chainId",chainId)
				.setParam("start",(pageNo - 1)*pageSize)
				.setParam("pageSize", pageSize);
		List<Map<String, Object>>  rankList = baseDaoSupport.findForMapList(queryRankSqb);

		Ssqb queryRankCountQb = Ssqb.create("com.jlt.vote.getVoteRank_count")
				.setParam("chainId", chainId);
		Integer rankCount = baseDaoSupport.findForInt(queryRankCountQb);
		return new Pagination(pageNo, pageSize, rankCount, rankList);


	}

	@Override
	public void deleteAllRedisKeys() {
		Set<String> keys = redisDaoSupport.keys("*");
		if(keys != null && keys.size() > 0){
			keys.forEach(a->{
			    if(Objects.isNull(a)
                        ||((!a.contains(CacheConstants.CAMPAIGN_VOTER_COUNT))
						&&(!a.contains(CacheConstants.CAMPAIGN_VOTER_USER_COUNT)))){
                    redisDaoSupport.del(a);
                }
			});
		}
	}

	@Override
	public void deleteAllRedisKeys2() {
		Set<String> keys = redisDaoSupport.keys("*");
		if(keys != null && keys.size() > 0){
			keys.forEach(a->{
				redisDaoSupport.del(a);
			});
		}
	}

	@Override
	public void operateRedisKey() {
//		redisDaoSupport.del(CacheConstants.VOTE_USER_DETAIL+"4590475890010112");
//		redisDaoSupport.del(CacheConstants.VOTE_USER_PICS+"4601802847528960");
//

		redisDaoSupport.hset(CacheConstants.CAMPAIGN_BASE+"9150344668","signCount","117");
	}

}
