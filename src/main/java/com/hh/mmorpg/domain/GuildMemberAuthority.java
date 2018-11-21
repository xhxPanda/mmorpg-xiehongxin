package com.hh.mmorpg.domain;

/**
 * 公会各等级的权限，由会长指定，会长拥有最大权力
 * 
 * @author 37
 *
 */
public class GuildMemberAuthority {

	private int guildId;
	private GuildMemberIdentity guildMemberIdentity;
	private boolean isCanTickMember; // 是否能踢人
	private boolean canSeeBank; // 是否可以浏览银行
	private int takeBankMaterialNum; // 成员每天可以获取多少物品在每一层银行里面
	private boolean isCanSendPublicMessage; // 是否可以发公会信息
	private int canUseGold; // 每天可以使用的金币

	public GuildMemberAuthority(int guildId, GuildMemberIdentity guildMemberIdentity) {
		this.guildId = guildId;
		this.guildMemberIdentity = guildMemberIdentity;

		// 会长拥有所有权限
		if (guildMemberIdentity.getId() == GuildMemberIdentity.PRESIDENT.getId()) {
			this.isCanTickMember = true;
			this.isCanSendPublicMessage = true;
			this.canSeeBank = true;
			this.takeBankMaterialNum = -1;
			this.canUseGold = -1;
		} else {
			this.isCanTickMember = false;
			this.isCanSendPublicMessage = false;
			this.canSeeBank = false;
			this.takeBankMaterialNum = 0;
			this.canUseGold = 0;
		}

	}

	public void setCanSeeBank(boolean canSeeBank) {
		this.canSeeBank = canSeeBank;
	}

	public void setTakeBankMaterialNum(int takeBankMaterialNum) {
		this.takeBankMaterialNum = takeBankMaterialNum;
	}

	public void setCanUseGold(int canUseGold) {
		this.canUseGold = canUseGold;
	}

	public int getGuildId() {
		return guildId;
	}

	public boolean isCanSeeBank() {
		return canSeeBank;
	}

	public int getTakeBankMaterialNum() {
		return takeBankMaterialNum;
	}

	public GuildMemberIdentity getGuildMemberIdentity() {
		return guildMemberIdentity;
	}

	public boolean isCanTickMember() {
		return isCanTickMember;
	}

	public boolean isCanSendPublicMessage() {
		return isCanSendPublicMessage;
	}

	public void setGuildMemberIdentity(GuildMemberIdentity guildMemberIdentity) {
		this.guildMemberIdentity = guildMemberIdentity;
	}

	public void setCanTickMember(boolean isCanTickMember) {
		this.isCanTickMember = isCanTickMember;
	}

	public void setCanSendPublicMessage(boolean isCanSendPublicMessage) {
		this.isCanSendPublicMessage = isCanSendPublicMessage;
	}

	public int getCanUseGold() {
		return canUseGold;
	}
}
