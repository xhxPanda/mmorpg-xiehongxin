package com.hh.mmorpg.event;

public class EventDealData<T extends EventData> {
	private T data;

	public EventDealData(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}
}
