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
        if (null != t){
            logger.error("Execute Task Error. {}", t);
        }
        long total = counting.decrementAndGet();
        logger.info("afterExecute Pending Task: {}", total);
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (null == task){
            return null;
        }

        long total = counting.incrementAndGet();
        logger.info("submit Task: {}", total);
        Future<?> future = super.submit(task);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (null == task){
            return null;
        }

        long total = counting.incrementAndGet();
        logger.info("submit Task: {}", total);
        Future<T> future = super.submit(task, result);
        return future;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (null == task){
            return null;
        }

        long total = counting.incrementAndGet();
        logger.info("submit Task: {}", total);
        Future<T> future = super.submit(task);
        return future;
    }

    @Override
    public boolean remove(Runnable task) {
        if (null == task){
            return false;
        }

        boolean flag = super.remove(task);
        if (flag){
            long total = counting.decrementAndGet();
            logger.info("remove Task: {}", total);
        }
        return flag;
    }

    public long getNumOfPendingTask(){
        return counting.get();
    }

    PingThread pingThread = null;
    public void startPing(){
        if (pingThread == null){
            pingThread = new PingThread(this);
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
        private ThreadPoolExecutor executor = null;
        public PingThread(ThreadPoolExecutor executor) {
            stopping = false;
            this.executor = executor;
        }

        @Override
        public void run() {
            while (!stopping){

                logger.info("{}", executor);

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
