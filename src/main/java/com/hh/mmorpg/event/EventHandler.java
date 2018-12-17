package com.hh.mmorpg.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 事件机制的处理
 * 
 * @author xhx
 *
 */
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

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
	}

}
