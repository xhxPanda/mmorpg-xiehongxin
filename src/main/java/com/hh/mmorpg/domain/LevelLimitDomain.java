package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 等级限制
 * 
 * @author xhx
 *
 */
public class LevelLimitDomain {

	private int level;
	private int needExp;
	private String resoreHpAndMp;
	private Map<Integer, Integer> addAttributes;

	public LevelLimitDomain(int level, int needExp, String resoreHpAndMp, String addAttributeStr) {
		super();
		this.level = level;
		this.needExp = needExp;
		this.resoreHpAndMp = resoreHpAndMp;

		this.addAttributes = new HashMap<>();
		for (String s : addAttributeStr.split(",")) {
			String[] str = s.split(":");
			int key = Integer.parseInt(str[0]);
			int value = Integer.parseInt(str[1]);
			this.addAttributes.put(key, value);
		}
	}

	public String getResoreHpAndMp() {
		return resoreHpAndMp;
	}

	public Map<Integer, Integer> getAddAttributes() {
		return addAttributes;
	}

	public int getLevel() {
		return level;
	}

	public int getNeedExp() {
		return needExp;
	}

}
