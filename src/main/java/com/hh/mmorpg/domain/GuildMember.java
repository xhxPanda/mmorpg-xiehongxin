package com.hh.mmorpg.domain;

public class GuildMember {

	private int guildId;
	private int roleId;
	private int userId;
	private String roleName;
	private int level;
	private int memberIdentityId;
	private String memberIdentityName;
	private int contributionPoint;
	private boolean isOnline;

	public GuildMember(int roleId, int userId, String roleName, int level, int memberIdentityId,
			String memberIdentityName, int contributionPoint, boolean isOnline, int guildId) {
		this.roleId = roleId;
		this.userId = userId;
		this.roleName = roleName;
		this.level = level;
		this.memberIdentityId = memberIdentityId;
		this.memberIdentityName = GuildMemberIdentity.getGuildMemberIdentity(memberIdentityId).getName();
		this.contributionPoint = contributionPoint;
		this.isOnline = isOnline;
		this.guildId = guildId;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public int getLevel() {
		return level;
	}

	public int getContributionPoint() {
		return contributionPoint;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public int getMemberIdentityId() {
		return memberIdentityId;
	}

	public String getMemberIdentityName() {
		return memberIdentityName;
	}

	public int getUserId() {
		return userId;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public void setMemberIdentityId(int memberIdentityId) {
		this.memberIdentityId = memberIdentityId;
		this.memberIdentityName = GuildMemberIdentity.getGuildMemberIdentity(memberIdentityId).getName();
	}

	public int getGuildId() {
		return guildId;
	}

	@Override
	public String toString() {
		return "GuildMember [角色id=" + roleId + ", 名称=" + roleName + ", 等级=" + level + ", 身份=" + memberIdentityName
				+ ", 贡献点=" + contributionPoint + ", 是否在线=" + isOnline + "]";
	}

}
