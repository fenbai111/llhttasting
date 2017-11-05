package com.jlt.vote.bis.campaign.entity;

import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

/**
 * 活动礼物列表
 * @Author gaoyan
 * @Date: 2017/7/20
 */    
@Table(tableName = "t_t_campaign_award")
public class CampaignAward {

  @PrimaryKeyField
  private Long id;

  /**
   * 主办方id
   */
  private Long chainId;

  /**
   * 奖品简介
   */
  private String summary;

  /**
   * 奖品说明
   */
  private String detail;

  /**
   * 奖品图片
   */
  private String awardpic;

  /**
   * 奖品图片
   */
  private Integer priority;

  private Boolean datastatus;

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

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getAwardpic() {
    return awardpic;
  }

  public void setAwardpic(String awardpic) {
    this.awardpic = awardpic;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Boolean getDatastatus() {
    return datastatus;
  }

  public void setDatastatus(Boolean datastatus) {
    this.datastatus = datastatus;
  }
}
