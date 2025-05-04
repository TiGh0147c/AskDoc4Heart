package com.example.demo.RegisterAndLogin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 明确列出所有允许的端口，包括3000-4000范围
                .allowedOrigins(
                    "http://localhost:3000", 
                    "http://localhost:3001", 
                    "http://localhost:3002",
                    "http://localhost:3003",
                    "http://localhost:3004",
                    "http://localhost:3005",
                    "http://localhost:3006",
                    "http://localhost:3007",
                    "http://localhost:3008",
                    "http://localhost:3009",
                    "http://localhost:3010",
                    "http://localhost:4000",
                    "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
}