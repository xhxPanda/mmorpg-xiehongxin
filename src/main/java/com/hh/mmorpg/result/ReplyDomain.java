package com.hh.mmorpg.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ReplyDomain {
	private Map<String, Object> domainMap;

	public static final ReplyDomain SUCCESS = new ReplyDomain(ResultCode.SUCCESS);
	public static final ReplyDomain FAILE = new ReplyDomain(ResultCode.FAILE);
	public static final ReplyDomain ILLEGAL = new ReplyDomain(ResultCode.ILLEGAL);
	public static final ReplyDomain IN_CD = new ReplyDomain(ResultCode.IN_CD);
	public static final ReplyDomain REPEAT_CLOTHES = new ReplyDomain(ResultCode.REPEAT_CLOTHES);
	public static final ReplyDomain NOT_ENOUGH = new ReplyDomain(ResultCode.NOT_ENOUGH);
	public static final ReplyDomain HAS_DEAD = new ReplyDomain(ResultCode.HAS_DEAD);
	public static final ReplyDomain MP_NOT_ENOUGH = new ReplyDomain(ResultCode.MP_NOT_ENOUGH);
	public static final ReplyDomain BONS_NOT_EXIT = new ReplyDomain(ResultCode.BONS_NOT_EXIT);
	public static final ReplyDomain CAN_NOT_BATTLE = new ReplyDomain(ResultCode.CAN_NOT_BATTLE);
	public static final ReplyDomain EQUIMENT_DURABILITY_HARM = new ReplyDomain(ResultCode.EQUIMENT_DURABILITY_HARM);
	public static final ReplyDomain BOX_SPACE_NOT_ENOUGH = new ReplyDomain(ResultCode.BOX_SPACE_NOT_ENOUGH);
	public static final ReplyDomain IN_HALO = new ReplyDomain(ResultCode.IN_HALO);
	public static final ReplyDomain IN_SILENT = new ReplyDomain(ResultCode.IN_SILENT);
	public static final ReplyDomain LEVEL_NOT_ENOUGH = new ReplyDomain(ResultCode.LEVEL_NOT_ENOUGH);
	public static final ReplyDomain HAD_HAS_GUILD = new ReplyDomain(ResultCode.HAD_HAS_GUILD);
	public static final ReplyDomain GUILD_FULL = new ReplyDomain(ResultCode.GUILD_FULL);
	public static final ReplyDomain HAS_SENT_APPLY = new ReplyDomain(ResultCode.HAS_SENT_APPLY);
	public static final ReplyDomain HAS_IN_GUILD = new ReplyDomain(ResultCode.HAS_IN_GUILD);
	public static final ReplyDomain LACK_OF_GUILD_JURISDICTION = new ReplyDomain(ResultCode.LACK_OF_GUILD_JURISDICTION);
	public static final ReplyDomain MISSION_NOT_EXIST = new ReplyDomain(ResultCode.MISSION_NOT_EXIST);
	public static final ReplyDomain OTHER_NOT_ONLINE = new ReplyDomain(ResultCode.OTHER_NOT_ONLINE);
	public static final ReplyDomain OTHER_NOT_IN_SCENE = new ReplyDomain(ResultCode.OTHER_NOT_IN_SCENE);
	public static final ReplyDomain NOT_IN_SCENE = new ReplyDomain(ResultCode.NOT_IN_SCENE);
	public static final ReplyDomain IN_TRANSACTION = new ReplyDomain(ResultCode.IN_TRANSACTION);
	public static final ReplyDomain GUILD_AUTHORITY = new ReplyDomain(ResultCode.GUILD_AUTHORITY);
	public static final ReplyDomain HAD_HAS_MISSION = new ReplyDomain(ResultCode.HAD_HAS_MISSION);
	public static final ReplyDomain NPC_NOT_EXIT = new ReplyDomain(ResultCode.NPC_NOT_EXIT);
	public static final ReplyDomain HAS_IN_TEAM = new ReplyDomain(ResultCode.HAS_IN_TEAM);
	public static final ReplyDomain TEAM_FULL = new ReplyDomain(ResultCode.TEAM_FULL);

	public ReplyDomain() {
		domainMap = new HashMap<String, Object>();
	}

	public ReplyDomain(String result) {
		domainMap = new HashMap<String, Object>();
		domainMap.put("r", result);
	}

	public void setIntDomain(String key, Integer value) {
		domainMap.put(key, value);
	}

	public void setStringDomain(String key, String value) {
		domainMap.put(key, value);
	}

	public void setListDomain(String key, Collection<?> list) {
		StringBuffer buffer = new StringBuffer();

		for (Object t : list) {
			if (buffer.length() > 0) {
				buffer.append(",");
			}
			buffer.append(t.toString());
		}

		domainMap.put(key, buffer.toString());
	}

	public void setBoolDomain(String key, boolean value) {
		domainMap.put(key, value);
	}

	public Object getDomain(String key) {
		return domainMap.get(key);
	}

	public String parseJsonObject() {
		JSONObject jsonObject = new JSONObject(domainMap);
		return jsonObject.toJSONString();
	}

	public boolean isSuccess() {
		return domainMap.get("r").equals("成功");
	}
}
