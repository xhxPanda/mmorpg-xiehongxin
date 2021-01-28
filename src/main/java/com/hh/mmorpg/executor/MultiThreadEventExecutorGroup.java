package com.hh.mmorpg.executor;

import java.util.concurrent.ThreadFactory;

public class MultiThreadEventExecutorGroup {
    private EventExecutor[] eventExecutors;

    public void init(int threadNum) {
        eventExecutors = new EventExecutor[threadNum];
//        ThreadFactory threadFactory = T
        for (int i = 0; i< threadNum; i++) {

        }
    }

}
