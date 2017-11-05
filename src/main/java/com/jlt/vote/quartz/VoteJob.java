package com.jlt.vote.quartz;

import com.jlt.vote.config.SysConfig;
import com.jlt.vote.quartz.service.IJobService;
import com.jlt.vote.util.SystemProfileEnum;
import com.xcrm.common.util.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 定时刷新公众号授权的token
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Component
public class VoteJob {

	private static final Logger logger = LoggerFactory.getLogger(VoteJob.class);

	@Autowired
	private SysConfig sysConfig;

	@Autowired
	private IJobService jobService;
	
	@Scheduled(cron="0 0 * * * ?")
	public void wxCheckCheatJob() {
		//每10分钟扫描时效
		if(!Objects.equals(sysConfig.getProjectProfile(), SystemProfileEnum.DEVELOP.value())){
			logger.debug("#############wxCheckCheatJob############");
			logger.debug("----------------wxCheckCheatJob JOB begin "+ DateFormatUtils.formatDate(new Date(),null)+"-------------------");
			jobService.wxCheckCheatRecord();
			logger.debug("----------------wxCheckCheatJob JOB end "+ DateFormatUtils.formatDate(new Date(),null)+"-------------------");
		}
	}
}