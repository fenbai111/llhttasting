package com.jlt.vote.quartz.service;

public interface IJobService {
	/**
	 * 定时查询作弊记录
	 */
	void wxCheckCheatRecord();
}
