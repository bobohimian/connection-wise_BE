package com.conwise.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 禁用 FAIL_ON_EMPTY_BEANS，避免空对象序列化失败
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 支持 Java 8 日期时间 API
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期序列化为时间戳，改为 ISO 8601 格式字符串
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}