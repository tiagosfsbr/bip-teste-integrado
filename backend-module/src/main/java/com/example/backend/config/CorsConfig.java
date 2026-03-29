package com.example.backend.config;

/**
 * Configuração de CORS - comentada para usar API mock em Node.js
 * Para usar com Spring Boot real, descomente e implemente WebMvcConfigurer
 */
/*
 * import org.springframework.context.annotation.Configuration;
 * import org.springframework.web.servlet.config.annotation.CorsRegistry;
 * import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 * 
 * @Configuration
 * public class CorsConfig implements WebMvcConfigurer {
 * 
 * @Override
 * public void addCorsMappings(CorsRegistry registry) {
 * registry.addMapping("/**")
 * .allowedOrigins("*")
 * .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
 * .allowedHeaders("*")
 * .maxAge(3600)
 * .allowCredentials(false);
 * }
 * }
 */
