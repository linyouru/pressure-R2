package com.zlg.pressurer2.config.threadPool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@ComponentScan("com.zlg.pressurer2")
@EnableAsync
public class TaskExecutorConfig implements AsyncConfigurer {

    @Value(value = "${threadPool.corePoolSize}")
    private int corePoolSize;
    @Value(value = "${threadPool.maxPoolSize}")
    private int maxPoolSize;
    @Value(value = "${threadPool.queueCapacity}")
    private int queueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        //处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数,
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 设置队列大小
        executor.setQueueCapacity(queueCapacity);
        // 设置线程名前缀
        executor.setThreadNamePrefix("AsyncExecutor-");
        //拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


}
