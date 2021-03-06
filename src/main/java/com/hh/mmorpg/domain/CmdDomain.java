package com.hh.mmorpg.domain;

import io.netty.channel.Channel;

public class CmdDomain {

	private String[] domainStringArry;
	private Channel channel;

	public CmdDomain(Channel channel, String str) {
		this.domainStringArry = str.split(" ");
		this.channel = channel;
	}

	public String getStringParam(int index) {
		return domainStringArry[index];
	}

	public int getIntParam(int index) {
		return Integer.parseInt(domainStringArry[index]);
	}
	
	public boolean getBooleanParam(int index) {
		return Boolean.parseBoolean(domainStringArry[index]);
	}


	public Channel getChannel() {
		return channel;
	}

	public String[] getDomainStringArry() {
		return domainStringArry;
	}

}
