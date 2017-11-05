package com.jlt.vote.bis.campaign.service;

import com.jlt.vote.bis.campaign.entity.CampaignAward;
import com.jlt.vote.bis.campaign.vo.CampaignGiftDetailVo;
import com.jlt.vote.bis.campaign.vo.CampaignGiftVo;
import com.jlt.vote.bis.campaign.vo.VoterVo;
import com.jlt.vote.bis.wx.entity.UserGiftRecord;
import com.xcrm.common.page.Pagination;

import java.util.List;
import java.util.Map;

/**
 * 主办方服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface ICampaignService {

    /**
     * 查询活动详情
     * @param chainId 店铺标识
     */
    Map<String ,Object> queryCampaignDetail(Long chainId);

    /**
     * 查询活动信息
     * @param chainId
     * @return
     */
    Map<String,Object> queryCampaignInfo(Long chainId);

    /**
     * 校验活动是否结束
     * @param chainId
     * @return
     */
    boolean checkCampaignFinish(Long chainId);

    /**
     * 查询活动规则
     * @param chainId 店铺标识
     */
    String queryCampaignRule(Long chainId);

    /**
     * 校验活动是否存在
     * @param chainId
     * @return
     */
    boolean checkCampaignExist(Long chainId);

    /**
     * 保存微信用户信息
     * @param wxUserMap
     */
    void saveVoter(Map<String, Object> wxUserMap);

    /**
     * 查询微信用户信息
     * @param openId
     */
    VoterVo queryVoter(String openId);

    /**
     * 查询用户列表信息
     * @param chainId 店铺标识
     * @param pageNo
     * @param pageSize
     * @return
     */
    Pagination querySimpleUserList(Long chainId,String queryKey, Integer pageNo, Integer pageSize);

    /**
     * 查询用户详情
     * 活动 用户热度增加加
     * @param chainId 店铺标识
     * @param userId 用户标识
     * @return
     */
    Map<String,Object> queryUserDetail(Long chainId, Long userId);

    /**
     * 查询用户基本详情
     * 活动 用户热度 不变
     * @param chainId 店铺标识
     * @param userId 用户标识
     * @return
     */
    Map<String,Object> queryUserBaseDetail(Long chainId, Long userId);

    /**
     * 校验活动用户是否存在
     * @param chainId 店铺标识
     * @param userId 用户标识
     * @return
     */
    boolean checkUserExist(Long chainId, Long userId);

    /**
     * 查询用户礼物列表
     * @param chainId 店铺标识
     * @param userId 用户id
     * @param pageNo
     * @param pageSize
     * @param voteTimestamp
     * @return
     */
    Pagination queryUserGiftList(Long chainId, Long userId, Integer pageNo, Integer pageSize, Long voteTimestamp);

    /**
     * 查询活动奖品信息
     * @param chainId
     * @return
     */
    List<CampaignAward> queryCampaignAward(Long chainId);

    /**
     * 查询活动礼物信息
     * @param chainId
     * @return
     */
    List<CampaignGiftVo> queryCampaignGiftList(Long chainId);

    /**
     * 查询活动礼物信息
     * @param chainId
     * @return
     */
    CampaignGiftDetailVo queryCampaignGiftDetail(Long chainId,Long giftId);

    /**
     * 保存礼物记录
     * @param userGiftRecord
     * @return
     */
    void saveUserGiftRecord(UserGiftRecord userGiftRecord);

    /**
     * 更新礼物记录
     * @param orderId
     * @return
     */
    void updateUserGiftRecord(Long orderId,String openId);

    /**
     * 更新用户礼物信息
     * @param openId
     * @param userId
     * @param giftId
     * @param giftCount
     */
    void updateUserGiftInfo(String openId, Long chainId, Long userId, Long giftId, Integer giftCount);

    /**
     * 更新用户投票信息
     * @param userId
     * @param voteCount
     * @param giftPoint
     */
    int updateUserVoteInfo(Long userId,Integer voteCount,Integer giftPoint);

    /**
     * 投票人投票
     * @param chainId
     * @param openId
     * @param userId
     * @return 投票结果
     */
    int vote(Long chainId, String openId,Long userId,String ipAddress);


    /**
     * 投票排行
     * @param chainId
     * @param pageNo
     *@param pageSize @return
     */
    Pagination getVoteRank(Long chainId, Integer pageNo, Integer pageSize);

    /**
     * 删除所有redis的key
     */
    void deleteAllRedisKeys();

    /**
     * 删除所有redis的key
     */
    void deleteAllRedisKeys2();


    void operateRedisKey();
}

