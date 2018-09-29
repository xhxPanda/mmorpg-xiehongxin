package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;

public class CMDdomain {

	private Map<String, Object> domainMap;
	private Channel channel;

	public CMDdomain(Channel channel, JSONObject jsonParamRoot) {
		this.domainMap = new HashMap<String, Object>();
		for (String key : jsonParamRoot.keySet()) {
			domainMap.put(key, jsonParamRoot.get(key));
		}

		this.channel = channel;
	}

	public String getStringParam(String key) {
		return domainMap.get(key).toString();
	}

	public int getIntParam(String key) {
		return Integer.parseInt(domainMap.get(key).toString());
	}

	public Channel getChannel() {
		return channel;
	}

}
