package com.hh.mmorpg.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ServiceHandler {

	private int key;
	private Object classInstance;
	private Method method;

	public ServiceHandler(int key, Object classInstance, Method method) {
		this.key = key;
		this.classInstance = classInstance;
		this.method = method;
	}

//	public void invodeMethod(String key, Object... args) {
//		try {
//			methodMap.get(key).invoke(classInstance, args);
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

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
