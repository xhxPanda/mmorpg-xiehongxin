package com.hh.mmorpg.result;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ReplyDomain {
	private Map<String, Object> domainMap;

	public static final ReplyDomain SUCCESS = new ReplyDomain(ResultCode.SUCCESS);
	public static final ReplyDomain FAILE = new ReplyDomain(ResultCode.FAILE);
	public static final ReplyDomain ILLEGAL = new ReplyDomain(ResultCode.ILLEGAL);

	public ReplyDomain() {
		domainMap = new HashMap<String, Object>();
	}

	public ReplyDomain(int result) {
		domainMap = new HashMap<String, Object>();
		domainMap.put("r", result);
	}

	public void setIntDomain(String key, Integer value) {
		domainMap.put(key, value);
	}

	public void setStringDomain(String key, String value) {
		domainMap.put(key, value);
	}

	public void setBoolDomain(String key, boolean value) {
		domainMap.put(key, value);
	}

	public String parseJsonObject() {
		JSONObject jsonObject = new JSONObject(domainMap);
		return jsonObject.toJSONString();
	}
	
	public boolean isSuccess() {
		return (Integer)domainMap.get("r") == 1;
	}
}
