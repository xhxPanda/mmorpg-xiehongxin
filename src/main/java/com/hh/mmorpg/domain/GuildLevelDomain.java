package com.hh.mmorpg.domain;

/**
 * 公会等级限制要求
 * @return
 */
public class GuildLevelDomain {

	private int level;
	private int canJoinMemberNum;
	private int needDonatePoint;

	public GuildLevelDomain(int level, int canJoinMemberNum, int needDonatePoint) {
		this.level = level;
		this.canJoinMemberNum = canJoinMemberNum;
		this.needDonatePoint = needDonatePoint;
	}

	public int getLevel() {
		return level;
	}

	public int getCanJoinMemberNum() {
		return canJoinMemberNum;
	}

	public int getNeedDonatePoint() {
		return needDonatePoint;
	}

}
