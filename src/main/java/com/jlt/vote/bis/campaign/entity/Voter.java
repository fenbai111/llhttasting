package com.jlt.vote.bis.campaign.entity;

import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

import java.util.Date;

/**
 * 投票人用户信息
 * @Author gaoyan
 * @Date: 2017/7/20
 */    
@Table(tableName = "t_t_voter")
public class Voter {

  @PrimaryKeyField
  private Long id;

  /**
   * 投票人 微信openId
   */
  private String openid;

  /**
   * 昵称
   */
  private String nickname;

  /**
   * 头像
   */
  private String headimgurl;

  /**
   * 原始信息
   */
  private String rawdata;

  /**
   * 创建时间
   */
  private Date created;

  /**
   * 更新时间
   */
  private Date updated;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getHeadimgurl() {
    return headimgurl;
  }

  public void setHeadimgurl(String headimgurl) {
    this.headimgurl = headimgurl;
  }

  public String getRawdata() {
    return rawdata;
  }

  public void setRawdata(String rawdata) {
    this.rawdata = rawdata;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }
}
