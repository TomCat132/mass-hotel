package cn.finetool.common.threadpool;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class DynamicThreadPool {

    private ThreadPoolExecutor threadPoolExecutor;
    
    @PostConstruct
    public void init(){
        // 初始化线程池
        threadPoolExecutor = new ThreadPoolExecutor(
                10, // corePoolSize: 核心线程数
                20, // maximumPoolSize: 最大线程数
                60L, // keepAliveTime: 非核心线程闲置时的存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(100), // 工作队列
                Executors.defaultThreadFactory(), // 线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }


    // 提交任务到线程池
    public Future<?> submitTask(Runnable task) {
        return threadPoolExecutor.submit(task);
    }

    // 关闭线程池
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    // 获取线程池状态等信息的方法
    public int getActiveCount() {
        return threadPoolExecutor.getActiveCount();
    }

    public long getTaskCount() {
        return threadPoolExecutor.getTaskCount();
    }

}
