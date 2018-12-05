package com.hh.mmorpg.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Attribute {

	private int id;
	private AtomicInteger value;
	private String name;

	public Attribute(int id, int value, String name) {
		this.id = id;
		this.value = new AtomicInteger(value);
		this.name = name;
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

	public void setValue(AtomicInteger value) {
		this.value = value;
	}

	public int changeValue(int decValue) {
		int newValue = value.addAndGet(decValue);
		if (newValue <= 0) {
			value.set(0);
			return 0;
		}

		return value.get();
	}
}
