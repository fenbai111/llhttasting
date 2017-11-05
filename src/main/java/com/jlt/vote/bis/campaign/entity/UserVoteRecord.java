package com.jlt.vote.bis.campaign.entity;

import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

import java.util.Date;

@Table(tableName = "t_t_user_vote_record")
public class UserVoteRecord {

  /**
   * 主办方id
   */
  @PrimaryKeyField
  private Long id;

  /**
   * 主办方id
   */
  private Long chainId;

  /**
   * 参与用户id
   */
  private Long userId;

  /**
   * 公众号openId
   */
  private String openId;

  /**
   * 1:普通投票 2:礼物投票
   */
  private Integer voteType;

  /**
   * 当前投票次数
   */
  private Integer voteCount;

  /**
   * 投票人ip地址
   */
  private String ipAddress;

  /**
   * 投票时间
   */
  private Date voteTime;

  /**
   * 状态 1:正常 0:失效
   */
  private String dataStatus;

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

  public Integer getVoteType() {
    return voteType;
  }

  public void setVoteType(Integer voteType) {
    this.voteType = voteType;
  }

  public Integer getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(Integer voteCount) {
    this.voteCount = voteCount;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public Date getVoteTime() {
    return voteTime;
  }

  public void setVoteTime(Date voteTime) {
    this.voteTime = voteTime;
  }

  public String getDataStatus() {
    return dataStatus;
  }

  public void setDataStatus(String dataStatus) {
    this.dataStatus = dataStatus;
  }
}
