package com.conwise;

import com.conwise.handler.EdgeListTypeHandler;
import com.conwise.handler.JsonTypeHandler;
import com.conwise.handler.NodeListTypeHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ConnectWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectWiseApplication.class, args);
    }

    @Bean
    public Configuration mybatisConfiguration() {
        Configuration configuration = new Configuration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        typeHandlerRegistry.register(Object.class,new JsonTypeHandler<>(Object.class));
        typeHandlerRegistry.register(List.class,new NodeListTypeHandler());
        typeHandlerRegistry.register(List.class,new EdgeListTypeHandler());
        return configuration;
    }
}
