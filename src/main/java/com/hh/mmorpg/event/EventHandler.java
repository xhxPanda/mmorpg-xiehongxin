package com.hh.mmorpg.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 事件机制的处理
 * 
 * @author xhx
 *
 */
public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	private Map<Class<?>, List<? extends EventBuilder<?>>> eventBuilds;

	private ReentrantLock lock;

	private EventHandler() {
		this.eventBuilds = new HashMap<>();
		this.lock = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	public <E extends EventData, T extends EventBuilder<E>> void addHandler(Class<E> eClass, T eventBuild) {
		lock.lock();
		try {
			List<EventBuilder<E>> builds = (List<EventBuilder<E>>) eventBuilds.get(eClass);

			if (builds == null) {
				builds = new ArrayList<>();
				eventBuilds.put(eClass, builds);
			}

			builds.add(eventBuild);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public <E extends EventData, T extends EventData> void invodeMethod(Class<E> eClass, T eventData) {
		List<EventBuilder<T>> builds = (List<EventBuilder<T>>) eventBuilds.get(eClass);

		for (EventBuilder<T> eventBuilder : builds) {
			eventBuilder.handler(eventData);
		}
	}

}
