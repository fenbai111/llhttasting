package com.jlt.vote.bis.wx.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付订单实体
 * @Author gaoyan
 * @Date: 2017/7/22
 */    
public class WxPrePayOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal payMoney;
	
	private String title;
	
	private String orderCode;
	
	private Long chainId;

	private String openId;

	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}


	public Long getChainId() {
		return chainId;
	}

	public void setChainId(Long chainId) {
		this.chainId = chainId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
}
