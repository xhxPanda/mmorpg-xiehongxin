package com.hh.mmorpg.domain;

/**
 * 等级限制
 * 
 * @author xhx
 *
 */
public class LevelLimitDomain {

	private int level;
	private int needExp;

	public LevelLimitDomain(int level, int needExp) {
		this.level = level;
		this.needExp = needExp;
	}

	public int getLevel() {
		return level;
	}

	public int getNeedExp() {
		return needExp;
	}

}
