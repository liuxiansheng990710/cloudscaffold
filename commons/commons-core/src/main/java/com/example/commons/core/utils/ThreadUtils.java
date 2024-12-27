package com.example.commons.core.utils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.MDC;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.example.commons.core.exceptions.CommonUtilsException;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ThreadUtils {

    /**
     * 线程池增强器
     * TtlExecutors.getTtlExecutorService(阿里线程增强器) 主要解决线程上下文传递问题
     * 通过线程池提交的任务和通过线程池创建的子线程中执行的任务，正确继承和传递线程局部变量（ThreadLocal）
     */
    private static final ExecutorService THREAD_POOL_EXECUTOR = TtlExecutors.getTtlExecutorService(getThreadPoolExecutor());

    /**
     * corePoolSize:核心线程数，此处设置为0，表示当线程池处于空闲状态的时候，销毁所有线程（核心和非核心）
     * maximumPoolSize：最大线程数（核心加非核心）
     * keepAliveTime：线程池中非核心线程的闲置存活时间（此处设置为120s）
     * SynchronousQueue（阻塞队列）：它的特点是没有容量。也就是说，它不能保存任何元素，每个插入操作都要等待一个相应的移除操作，每个移除操作都要等待一个相应的插入操作
     * 优点：这种设计使得任务能够被立即执行，因为 SynchronousQueue 没有容量限制，不需要等待队列中有空闲位置
     * 当提交任务时，如果有空闲线程，任务被立即执行；如果没有空闲线程，新任务会直接与线程池中的线程进行绑定，避免了任务在队列中的排队等待
     * 缺点：由于 SynchronousQueue 没有容量，当线程池中的线程都在执行任务时，如果没有新的任务提交，那么额外的任务将无法存放到队列中
     * 在这种情况下，线程池可能会根据设置的拒绝策略来处理这些额外的任务，例如丢弃、抛出异常等
     * ThreadFactory（线程工厂）：创建线程 这里的 "cache-pool" 可能表示线程池的命名或者用途，而 %d 表示线程数的占位符
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, 32,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("cache-pool-%d").build()) {
            @Override
            protected void afterExecute(Runnable runnable, Throwable throwable) {
                super.afterExecute(runnable, throwable);
                printException(runnable, throwable);
            }
        };
    }

    /**
     * 获取线程执行器
     *
     * @return
     */
    public static ExecutorService getExecutor() {
        if (Objects.nonNull(THREAD_POOL_EXECUTOR)) {
            return THREAD_POOL_EXECUTOR;
        }
        throw new CommonUtilsException("Thread poll executor create failed");
    }

    /**
     * 执行线程
     *
     * @param runnable 任务
     */
    public static void execute(Runnable runnable) {
        //传递链路信息
        Map<String, String> parentContextMap = MDC.getCopyOfContextMap();
        getExecutor().execute(() -> {
            try {
                MDC.setContextMap(parentContextMap);
                runnable.run();
            } catch (Exception e) {
                runnable.run();
            } finally {
                MDC.clear();
            }
        });
    }

    /**
     * 停止任务线程池
     */
    public static void shutdown() {
        getExecutor().shutdown();
    }

    /**
     * 提交线程任务
     *
     * @param runnable
     * @return
     */
    public static Future<?> submit(Runnable runnable) {
        Map<String, String> parentContextMap = MDC.getCopyOfContextMap();
        return getExecutor().submit(() -> {
            try {
                MDC.setContextMap(parentContextMap);
                runnable.run();
            } catch (Exception e) {
                runnable.run();
            } finally {
                MDC.clear();
            }
        });
    }

    /**
     * 提交线程任务
     *
     * @param runnable
     * @param <E>
     * @return
     */
    public static <E> Future<E> submit(Callable<E> runnable) {
        Map<String, String> parentContextMap = MDC.getCopyOfContextMap();
        AtomicReference<E> result = null;
        return getExecutor().<E>submit(() -> {
            try {
                MDC.setContextMap(parentContextMap);
                result.set(runnable.call());
            } catch (Exception e) {
                result.set(runnable.call());
            } finally {
                MDC.clear();
            }
            return result.get();
        });
    }

    /**
     * 获取线程任务执行结果
     *
     * @param future
     * @param <E>
     * @return
     */
    public static <E> E getFuture(Future<E> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            log.error(Throwables.getStackTraceAsString(e));
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            log.error(Throwables.getStackTraceAsString(e));
            return null;
        }
    }

    private static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
