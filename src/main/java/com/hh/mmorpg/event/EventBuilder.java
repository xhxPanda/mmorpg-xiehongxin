package com.hh.mmorpg.event;

public interface EventBuilder<T extends EventData> {
	
	void handler(T eventData);

}
