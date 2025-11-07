package com.oc.springproject5.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {
    @Bean ObjectMapper objectMapper() {
        return  new ObjectMapper();
    }
}
