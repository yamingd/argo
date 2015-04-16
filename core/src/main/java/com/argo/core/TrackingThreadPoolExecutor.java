package com.argo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by user on 1/25/15.
 */
public class TrackingThreadPoolExecutor extends ThreadPoolExecutor {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicLong counting = new AtomicLong();

    public TrackingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.startPing();
    }

    public TrackingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.startPing();
    }

    public TrackingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.startPing();
    }

    public TrackingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.startPing();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        long total = counting.decrementAndGet();
        logger.info("Pending Task: {}", total);
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = super.submit(task);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Future<T> future = super.submit(task, result);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
        return future;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Future<T> future = super.submit(task);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
        return future;
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
    }

    @Override
    public boolean remove(Runnable task) {
        boolean flag = super.remove(task);
        if (flag){
            long total = counting.decrementAndGet();
            logger.info("Pending Task: {}", total);
        }
        return flag;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        RunnableFuture<T> future = super.newTaskFor(runnable, value);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
        return future;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> future = super.newTaskFor(callable);
        long total = counting.incrementAndGet();
        logger.info("Pending Task: {}", total);
        return future;
    }

    public long getNumOfPendingTask(){
        return counting.get();
    }

    PingThread pingThread = null;
    public void startPing(){
        if (pingThread == null){
            pingThread = new PingThread();
        }
        pingThread.start();
    }

    @Override
    public void shutdown() {
        if (pingThread != null) {
            pingThread.stopPing();
        }
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (pingThread != null) {
            pingThread.stopPing();
        }
        return super.shutdownNow();
    }

    public class PingThread extends Thread{

        private volatile boolean stopping = false;

        public PingThread() {
            stopping = false;
        }

        @Override
        public void run() {
            while (!stopping){
                long total = getNumOfPendingTask();
                logger.info("Pending Task: {}", total);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.info("PingThread is Out.");
        }

        public synchronized void stopPing(){
            stopping = true;
        }
    }
}
