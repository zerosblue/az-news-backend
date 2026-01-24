package com.azit.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 루트 경로 가져오기
        String projectPath = System.getProperty("user.dir");

        // ★ 핵심 수정: 맥/윈도우 호환성을 위해 "file:" + 경로 + "/" 형식으로 통일
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + projectPath + "/uploads/");
    }
}