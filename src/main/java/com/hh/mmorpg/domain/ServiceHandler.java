package com.hh.mmorpg.domain;

import java.lang.reflect.Method;

public class ServiceHandler {

	private Object classInstance;
	private Method method;

	public ServiceHandler(Object classInstance, Method method) {
		this.classInstance = classInstance;
		this.method = method;
	}

	public Object getClassInstance() {
		return classInstance;
	}

	public Method getMethod() {
		return method;
	}

}
