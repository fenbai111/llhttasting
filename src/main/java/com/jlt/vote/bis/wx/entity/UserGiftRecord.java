package com.jlt.vote.bis.wx.entity;

import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

import java.util.Date;

@Table(tableName = "t_t_user_gift_record")
public class UserGiftRecord {

    @PrimaryKeyField
    private Long id;

    /**
     * 活动id
     */
    private Long chainId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 公众号openId
     */
    private String openId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 投票用户头像
     */
    private String headImgUrl;

    /**
     * 投票用户名称
     */
    private String nickName;

    /**
     * 投票时间
     */
    private Date voteTime;

    /**
     * 投票时间戳
     */
    private Long voteTimestamp;

    /**
     * 投票礼物Id
     */
    private Long giftId;

    /**
     * 礼物名称
     */
    private String giftName;

    /**
     * 礼物点数
     */
    private Integer giftCount;

    /**
     * 礼物备注
     */
    private String remark;

    /**
     * 状态 1:正常 0:失效
     */
    private Boolean dataStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Date voteTime) {
        this.voteTime = voteTime;
    }

    public Long getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(Long voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Integer getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(Integer giftCount) {
        this.giftCount = giftCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(Boolean dataStatus) {
        this.dataStatus = dataStatus;
    }
}
