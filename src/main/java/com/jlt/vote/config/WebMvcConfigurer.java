package com.jlt.vote.config;

import com.jlt.vote.interceptor.VoteInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(getVoteInterceptor()).addPathPatterns("/vote/**");
        super.addInterceptors(registry);
    }

    @Bean
    public VoteInterceptor getVoteInterceptor() {
        return new VoteInterceptor();
    }

}
