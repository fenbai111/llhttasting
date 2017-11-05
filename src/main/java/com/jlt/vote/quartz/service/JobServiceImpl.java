package com.jlt.vote.quartz.service;

import com.alibaba.fastjson.JSON;
import com.jlt.vote.bis.campaign.vo.UserDetailVo;
import com.jlt.vote.email.SendMailUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.util.StringUtil;
import com.xcrm.common.util.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JobServiceImpl implements IJobService {
	
	protected Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	private BaseDaoSupport baseDaoSupport;

	@Override
	public void wxCheckCheatRecord() {
		Ssqb queryCheatRecordSqb = Ssqb.create("com.jlt.vote.job.queryWxCheatRecord");
		List<Map<String,Object>> cheatRecordMap = baseDaoSupport.findForMapList(queryCheatRecordSqb);
		if(ListUtil.isNotEmpty(cheatRecordMap)){
			String text = JSON.toJSONString(cheatRecordMap);
			SendMailUtil sendEmail = new SendMailUtil(
					"15604090129@163.com", "jlt2016YUIOYHN", "15604090129@163.com",
					"线上环境发生作弊数据，请及时关注", text, "ecs-vote", "", "");
			try {
				sendEmail.send();
			} catch (Exception ex) {
				log.error("SendMailUtil.send() error ",ex);
			}
		}
	}
}
