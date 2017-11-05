package com.jlt.vote.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 阿里Druid数据库线程池配置
 * @Author gaoyan
 * @Date: 2017/5/18
 */
@Configuration
public class DruidDataSourceConfig {

	@Bean
    @ConfigurationProperties(prefix = "spring.datasource")
	public DataSource druidDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		return druidDataSource;
	}

}