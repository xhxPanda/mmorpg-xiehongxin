package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class GuildApply {

	private int roleId;
	private int userId;
	private String name;
	private int guildId;
	private String content;

	public GuildApply(int roleId, int userId, String name, int guildId, String content) {
		this.roleId = roleId;
		this.userId = userId;
		this.name = name;
		this.guildId = guildId;
		this.content = content;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getName() {
		return name;
	}

	public int getGuildId() {
		return guildId;
	}

	public String getContent() {
		return content;
	}

	public int getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "申请 [roleId=" + roleId + ", 名称=" + name + ", 申请内容=" + content + "]";
	}

	public static final ResultBuilder<GuildApply> BUILDER = new ResultBuilder<GuildApply>() {

		@Override
		public GuildApply build(ResultSet result) throws SQLException {
			int roleId = result.getInt("roleId");
			int userId = result.getInt("userId");
			String name = result.getString("name");
			int guildId = result.getInt("guildId");
			String content = result.getString("content");
			return new GuildApply(roleId, userId, name, guildId, content);
		}
	};
}
