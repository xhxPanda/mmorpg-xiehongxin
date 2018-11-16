package com.hh.mmorpg.domain;

public class GuildMember {

	private int roleId;
	private int userId;
	private String roleName;
	private int level;
	private int memberIdentityId;
	private String memberIdentityName;
	private int contributionPoint;
	private boolean isOnline;

	public GuildMember(int roleId, int userId, String roleName, int level, int memberIdentityId,
			String memberIdentityName, int contributionPoint, boolean isOnline) {
		this.roleId = roleId;
		this.userId = userId;
		this.roleName = roleName;
		this.level = level;
		this.memberIdentityId = memberIdentityId;
		this.memberIdentityName = memberIdentityName;
		this.contributionPoint = contributionPoint;
		this.isOnline = isOnline;
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

	public void setMemberIdentityId(int memberIdentityId) {
		this.memberIdentityId = memberIdentityId;
		this.memberIdentityName = GuildMemberIdentity.getGuildMemberIdentity(memberIdentityId).getName();
	}

}
