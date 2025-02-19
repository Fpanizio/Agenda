package com.panizio.agenda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**") // Define o padrão de URL para permitir CORS
        .allowedOrigins("http://localhost:4200") // Permite requisições do frontend
        .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos permitidos
        .allowedHeaders("*") // Headers permitidos
        .allowCredentials(true); // Permite cookies e autenticação
  }
}