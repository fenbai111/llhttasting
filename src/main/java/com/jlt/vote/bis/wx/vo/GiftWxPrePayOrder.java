package com.jlt.vote.bis.wx.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付订单实体
 * @Author gaoyan
 * @Date: 2017/7/22
 */    
public class GiftWxPrePayOrder extends WxPrePayOrder implements Serializable {

	private static final long serialVersionUID = 1L;

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
	 * 用户id
	 */
	private Long userId;

	/**
	 * 用户留言
	 */
	private String remark;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
