package com.zlg.pressurer2.config.threadPool;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@ComponentScan("com.zlg.pressurer2")
@EnableAsync
public class TaskExecutorConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(8);
        // 设置最大线程数
        executor.setMaxPoolSize(16);
        // 设置队列大小
        executor.setQueueCapacity(200);
        // 设置线程名前缀
        executor.setThreadNamePrefix("AsyncExecutor-");
        //拒绝策略
        executor.setRejectedExecutionHandler(null);
        executor.initialize();
        return executor;
    }



}
