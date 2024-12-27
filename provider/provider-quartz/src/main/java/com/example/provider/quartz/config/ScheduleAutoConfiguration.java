package com.example.provider.quartz.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * <p>
 * 定时任务集群配置
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 18:01
 */
@Configuration
public class ScheduleAutoConfiguration {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        // quartz参数
        Properties prop = new Properties();
        prop.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "quartzScheduler");
        prop.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_ID, StdSchedulerFactory.AUTO_GENERATE_INSTANCE_ID);
        // 线程池配置
        prop.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, org.quartz.simpl.SimpleThreadPool.class.getName());
        prop.put(StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadCount", "20");
        prop.put(StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadPriority", "5");
        // JobStore配置
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, LocalDataSourceJobStore.class.getName());
        // 集群配置
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".isClustered", "true");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".clusterCheckinInterval", "15000");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".maxMisfiresToHandleAtATime", "1");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".txIsolationLevelSerializable", "true");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".acquireTriggersWithinLock", "true");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".misfireThreshold", "12000");
        prop.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".tablePrefix", "QRTZ_");
        factory.setQuartzProperties(prop);
        factory.setSchedulerName("quartzScheduler");
        // 延时启动
        factory.setStartupDelay(1);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        // 设置自动启动，默认为true
        factory.setAutoStartup(true);
        return factory;
    }
}
