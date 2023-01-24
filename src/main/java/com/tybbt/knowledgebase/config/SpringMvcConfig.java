package com.tybbt.knowledgebase.config;

import com.tybbt.knowledgebase.interceptor.LogInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Resource
    LogInterceptor logInterceptor;  // 拦截器注入

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns("/login"); // '/**'表示针对所有接口做校验 若只
        // excludePathPatterns 表示不拦截某些接口
    }
}
