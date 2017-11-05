package com.jlt.vote.config;

import com.xcrm.cloud.database.db.init.InitAnnotationEntity;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 内置服务server的相关配置
 *
 * @Author gaoyan
 * @Date: 2017/5/18
 */
@Configuration
@ComponentScan({"com.jlt.vote"})
public class WebServerConfig {
    @Autowired
    private SysConfig sysConfig;

    /**
     * 内置tomcat服务的配置
     * 线程池 ,连接数及超时时间配置
     *
     * @return
     */
    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            //设置最大连接数
            protocol.setMaxConnections(1000);
            //设置最大线程数
            protocol.setMaxThreads(800);
            protocol.setConnectionTimeout(2000);
        });
        return tomcatFactory;
    }

    @Bean(name = "initAnnotationEntity", initMethod = "init")
    public InitAnnotationEntity getInitAnnotationEntity() {
        return new InitAnnotationEntity("com.jlt.vote");
    }

    /**
     * 内置tomcat服务的配置
     * 线程池 ,连接数及超时时间配置
     *
     * @return
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor createThreadPoolExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }


}