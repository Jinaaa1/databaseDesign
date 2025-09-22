package com.ank.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地图片目录到 /dbDesign/** 路径
        registry.addResourceHandler("/commodityPictures/**")
                .addResourceLocations("file:D:/dbDesign/commodityPictures/");
        // 本地图片实际存储路径
    }
}

