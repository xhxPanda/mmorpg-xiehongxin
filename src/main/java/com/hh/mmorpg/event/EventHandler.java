package com.hh.mmorpg.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandler {

	private Object classInstance;
	private Method method;

	public EventHandler(Object classInstance, Method method) {
		this.classInstance = classInstance;
		this.method = method;
	}

	public void invodeMethod(EventDealData<?> eventData) {
		try {
			method.invoke(classInstance, eventData);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
