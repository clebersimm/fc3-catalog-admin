package com.fullcycle.admin.catalog.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.infrastructure.configuration.json.Json;

@Configuration
public class ObjectMapperConfig {
    @Bean
    ObjectMapper objectMapper() {
       return Json.mapper();
    }
}
