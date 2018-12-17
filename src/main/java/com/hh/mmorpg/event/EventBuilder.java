package com.hh.mmorpg.event;

public interface EventBuilder<T extends EventData> {

	public void handler(T data);

}
