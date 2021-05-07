package com.mine.obsession.components;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorProvider {

    @Bean(name = "zip")
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(2,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }
}
