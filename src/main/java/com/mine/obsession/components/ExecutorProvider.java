package com.mine.obsession.components;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ExecutorProvider {

    @Bean
    public ThreadPoolExecutor getExecutor() {
        return new ThreadPoolExecutor(2,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }
}
