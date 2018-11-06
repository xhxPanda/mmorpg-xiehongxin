package com.hh.mmorpg.domain;

import io.netty.channel.Channel;

public class CMDdomain {

	private String[] domainStringArry;
	private Channel channel;

	public CMDdomain(Channel channel, String str) {
		this.domainStringArry = str.split(" ");
		this.channel = channel;
	}

	public String getStringParam(int index) {
		return domainStringArry[index];
	}

	public int getIntParam(int index) {
		return Integer.parseInt(domainStringArry[index]);
	}

	public Channel getChannel() {
		return channel;
	}

	public String[] getDomainStringArry() {
		return domainStringArry;
	}

}
