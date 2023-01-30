package org.shanksit.japedu.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author forlevinlee
 * @ClassName ThreadPoolConfig
 * <p>
 *  线程池配置：
 *  默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务，
 *  当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中；
 *  当队列满了，就继续创建线程，当线程数量大于等于maxPoolSize后，开始使用拒绝策略拒绝。
 * </p>
 * @Version 1.0
 **/
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程数（默认线程数）
     */
    private static final int TASK_CORE_POOL_SIZE = 20;
    /**
     * 最大线程数
     */
    private static final int TASK_MAX_POOL_SIZE = 50;
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private static final int TASK_KEEP_ALIVE_TIME = 10;
    /**
     * 缓冲队列大小
     */
    private static final int TASK_QUEUE_CAPACITY = 20;
    /**
     * 异步任务线程池名前缀
     */
    private static final String TASK_THREAD_NAME_PREFIX = "dataAccessCommonTaskExecutor-";

    @Bean("dataAccessCommonTaskExecutor")
    public ThreadPoolTaskExecutor dataAccessCommonTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(TASK_KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix(TASK_THREAD_NAME_PREFIX);
        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }



}
