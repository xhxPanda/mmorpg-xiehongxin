package com.hh.mmorpg.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件机制的处理
 * 
 * @author xhx
 *
 */
public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	private Map<Integer, List<EventBuilder>> eventBuilds;

	private EventHandler() {
		eventBuilds = new HashMap<>();
	}

	public void addHandler(int type, EventBuilder eventBuild) {
		List<EventBuilder> builds = (List<EventBuilder>) eventBuilds.get(type);

		if (builds == null) {
			builds = new ArrayList<>();
			eventBuilds.put(type, builds);
		}

		builds.add(eventBuild);
	}

	public void invodeMethod(int type, EventData eventData) {
		List<EventBuilder> builds = (List<EventBuilder>) eventBuilds.get(type);

		for (EventBuilder eventBuilder : builds) {
			eventBuilder.handler(eventData);
		}
	}

}
