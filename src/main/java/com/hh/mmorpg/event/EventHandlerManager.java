package com.hh.mmorpg.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandlerManager {

	public static final EventHandlerManager INSATNCE = new EventHandlerManager();

	private Map<Integer, List<EventHandler>> methodMap;

	private EventHandlerManager() {
		methodMap = new HashMap<>();
	}

	public void register(Object claz) {
		handlerRegister(claz);
	}
	
	public void methodInvoke(int eventType, EventDealData<?> eventData) {
		List<EventHandler> handles = methodMap.get(eventType);
		
		for(EventHandler eventHandler : handles) {
			eventHandler.invodeMethod(eventData);
		}
	}

	private void handlerRegister(Object claz) {
		Class<?> clas = claz.getClass();

		Method[] methods = clas.getDeclaredMethods();
		for (Method method : methods) {
			Event annotation = method.getAnnotation(Event.class);
			if (annotation == null) {
				continue;
			}
			
			addHandle(claz, method, annotation.eventType());

		}
	}

	private void addHandle(Object claz, Method method, int id) {
		List<EventHandler> handlers = methodMap.get(id);
		if (handlers == null) {
			handlers = new ArrayList<>();
			methodMap.put(id, handlers);
		}
		EventHandler handler = new EventHandler(claz, method);
		handlers.add(handler);
	}

}
