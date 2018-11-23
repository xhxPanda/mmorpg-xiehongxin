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
	private String resoreHpAndMp;

	public LevelLimitDomain(int level, int needExp, String resoreHpAndMp) {
		super();
		this.level = level;
		this.needExp = needExp;
		this.resoreHpAndMp = resoreHpAndMp;
	}

	public String getResoreHpAndMp() {
		return resoreHpAndMp;
	}
	
	public int getLevel() {
		return level;
	}

	public int getNeedExp() {
		return needExp;
	}

}
