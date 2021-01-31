package com.hh.mmorpg.executor;

import java.util.function.Consumer;

public abstract class EventRunner implements Runnable {

    private Consumer<EventRunner> preRun;

    private Consumer<EventRunner> afterRun;

    public abstract int code();

    public abstract void runEvent();

    public abstract String name();

    @Override
    public void run() {
        preRun.accept(this);

        runEvent();

        afterRun.accept(this);
    }
}
