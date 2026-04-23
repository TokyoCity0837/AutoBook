package com.autobook.AI.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AiApiProperties.class)
public class AiConfig {

    @Bean
    public RestClient aiRestClient(AiApiProperties props) {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }
}