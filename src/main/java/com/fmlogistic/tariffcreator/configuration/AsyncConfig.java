package com.fmlogistic.tariffcreator.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    public static final String THREAD_ASYNC_PREFIX = "Async Thread";
    public static final String GENERATE_TARIFF_EXCEPTION = "Произошла ошибка при генерации тарифа: ";
    public static final String METHOD_NAME = "Название метода: {}";

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.info(GENERATE_TARIFF_EXCEPTION, ex);
            log.info(METHOD_NAME, method.getName());
        };
    }

    @Override
    public Executor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(THREAD_ASYNC_PREFIX);
        executor.initialize();
        return executor;
    }
}
