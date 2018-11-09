package com.hh.mmorpg.domain;

import java.lang.reflect.Method;

public class ServiceHandler {

	private int key;
	private Object classInstance;
	private Method method;

	public ServiceHandler(int key, Object classInstance, Method method) {
		this.key = key;
		this.classInstance = classInstance;
		this.method = method;
	}

	public int getKey() {
		return key;
	}

	public Object getClassInstance() {
		return classInstance;
	}

	public Method getMethod() {
		return method;
	}

}
