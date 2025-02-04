package com.care.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImgConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/project/review/image/**")
                .addResourceLocations("file:C:/Users/USER/Desktop/job/spring-workspace/movieProjectBack/target/movieProject/resources/img/poster/");
    }
}