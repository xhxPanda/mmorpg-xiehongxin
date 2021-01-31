package com.hh.mmorpg.executor;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

public class MultiThreadEventExecutorGroup {
    private EventExecutor[] eventExecutors;

    private int threadNum;

    public void init(int threadNum) {
        eventExecutors = new EventExecutor[threadNum];
        ThreadFactory threadFactory = new DefaultThreadFactory("group_thread");
        for (int i = 0; i< threadNum; i++) {
            eventExecutors[i] = new EventExecutor(new DefaultEventExecutor(), threadFactory, true);
        }
    }

    public void pushRunner(int code, Runnable runnable) {
        eventExecutors[code % threadNum].execute(runnable);
    }
}