package com.webank.wsbcs.chdis.base.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import com.webank.wsbcs.chdis.base.properties.AspectProperties;
import com.webank.wsbcs.chdis.base.properties.ExecutorProperties;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    @Autowired
    private ExecutorProperties executorProperties;
    @Autowired
    private AspectProperties aspectProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    /**
     * thread pool for scheduler parallel task (not async): pull block, trans monitor, statistic
     * trans, delete info, reset groupList
     * 
     * @return ThreadPoolTaskScheduler
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskExecutor() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(30);
        scheduler.setThreadNamePrefix("custom-task-");
        scheduler.setAwaitTerminationSeconds(600);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    /**
     * pull block and trans from chain
     * 
     * @return
     */
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        log.info("start asyncExecutor init..");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueSize());
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); // 从Runner改为abort抛出
        // init executor
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "lockExecutorService")
    public ExecutorService lockPoolExecutor() {
        ExecutorService executorService = new ThreadPoolExecutor(
                aspectProperties.getMaxLockPoolSize(), aspectProperties.getMaxLockPoolSize(), 1,
                TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(10), new CallerRunsPolicy());
        return executorService;
    }

}
