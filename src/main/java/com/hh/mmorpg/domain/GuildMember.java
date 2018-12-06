package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

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
		return "成员 [角色id=" + roleId + ", 名称=" + roleName + ", 等级=" + level + ", 身份=" + memberIdentityName + ", 贡献点="
				+ contributionPoint + ", 是否在线=" + isOnline + "]";
	}

	public static final ResultBuilder<GuildMember> BUILDER = new ResultBuilder<GuildMember>() {

		@Override
		public GuildMember build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt("RoleId");
			int level = result.getInt("level");
			int userId = result.getInt("UserId");
			int guildId = result.getInt("GuildId");
			int memberIdentityId = result.getInt("memberIdentityId");
			int contributionPoint = result.getInt("contributionPoint");
			String roleName = result.getString("roleName");
			return new GuildMember(roleId, userId, roleName, level, memberIdentityId,
					GuildMemberIdentity.getGuildMemberIdentity(memberIdentityId).getName(), contributionPoint, false,
					guildId);
		}
	};
}
