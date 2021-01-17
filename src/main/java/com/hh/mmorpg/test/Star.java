package com.hh.mmorpg.test;

import com.hh.mmorpg.server.WebSocketServer;

public class Star {
	
	public static final void main(String args[]) {
		WebSocketServer server = new WebSocketServer();
		
		server.start();
	}
}
