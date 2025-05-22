package com.conwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "asyncExecutor") // 定义一个 Spring Bean，名称为 "asyncExecutor"，用于异步任务执行
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 创建一个线程池任务执行器对象，用于管理异步任务的线程池

        // 核心线程数是指线程池中始终保持活跃的线程数，即使没有任务，这些线程也不会被销毁
        // 当有任务提交时，会优先使用核心线程来处理
        executor.setCorePoolSize(10);

        // 当任务数量超过核心线程数且任务队列已满时，线程池会创建新的线程，直到达到最大线程数
        // 防止无限创建线程导致资源耗尽
        executor.setMaxPoolSize(20);

        // 任务队列用于存储等待执行的任务，当核心线程都在忙碌时，新任务会进入队列
        // 如果线程数已达最大，则根据拒绝策略处理新任务
        executor.setQueueCapacity(100);

        // 线程名称前缀用于标识线程池中的线程，便于调试和监控
        // 设置为 "async-" 后，线程名会是类似 "async-1"、"async-2" 的格式，帮助快速定位是异步任务线程
        executor.setThreadNamePrefix("asyncThread-");
        executor.initialize();
        return executor;
    }
}
