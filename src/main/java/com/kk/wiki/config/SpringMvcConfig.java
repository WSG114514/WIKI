package com.kk.wiki.config;

import com.kk.wiki.interceptor.LogIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LogIntercepter logIntercepter;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logIntercepter).addPathPatterns("/**").excludePathPatterns("/login");
    }
}
