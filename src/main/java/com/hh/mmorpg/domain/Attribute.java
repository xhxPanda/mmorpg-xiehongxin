package com.hh.mmorpg.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Attribute {

	private int id;
	private AtomicInteger value;
	private String name;
	private int max;

	public Attribute(int id, int value, String name, int max) {
		this.id = id;
		this.value = new AtomicInteger(value);
		this.name = name;
		this.max = max;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value.get();
	}

	public String getName() {
		return name;
	}

	public int getMax() {
		return max;
	}

	public int changeValue(int decValue) {
		int newValue = value.addAndGet(decValue);
		if (newValue <= 0) {
			value.set(0);
			return 0;
		} else if (max != -1 && value.get() >= max) {
			value.set(max);
			return max;
		}

		return value.get();
	}
}
