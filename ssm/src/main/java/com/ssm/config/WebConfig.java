package com.ssm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//拦截器配置
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**")
                .excludePathPatterns("/login", "/uploads/**", "/file/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件的访问路径
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
