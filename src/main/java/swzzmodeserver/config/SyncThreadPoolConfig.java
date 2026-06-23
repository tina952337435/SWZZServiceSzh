package swzzmodeserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SyncThreadPoolConfig {

    @Bean("syncTaskExecutor")
    public Executor syncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);        // 核心线程数
        executor.setMaxPoolSize(20);         // 最大线程数
        executor.setQueueCapacity(1000);     // 缓冲队列
        executor.setThreadNamePrefix("sync-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略：由调用方执行
        executor.initialize();
        return executor;
    }
}