package com.leftoverlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5174", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Food images
        registry.addResourceHandler("/food/**")
                .addResourceLocations("file:uploads/food/", "classpath:/static/food/");

        // ✅ Profile images
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/profiles/");

        // ✅ Community post images (fix for your issue)
        registry.addResourceHandler("/community/**")
                .addResourceLocations("file:uploads/community/", "classpath:/static/community/");
    }
}
