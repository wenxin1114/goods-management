package com.wenxin.sm.config;

import com.wenxin.sm.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 实现WebMvcConfigurer配置拦截器
     */
    public void addInterceptors(InterceptorRegistry registry){
        // 不需要拦截的路径
        List<String> strings = new ArrayList<>();
        strings.add("/user/login");
        strings.add("/goods/list");
        strings.add("/goods/search");
        strings.add("/category/list");
        strings.add("/goods/detail/*");
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns(strings);
    }
}
