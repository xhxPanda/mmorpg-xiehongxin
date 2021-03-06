package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class FriendApply {

	private int roleId;
	private int applyRoleId;
	private String roleName;
	private int roleLevel;
	private String content;
	private int userId;

	public FriendApply(int applyRoleId, int roleId, String roleName, int roleLevel, String content,  int userId) {
		this.applyRoleId = applyRoleId;
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleLevel = roleLevel;
		this.content = content;
		this.userId = userId;
	}

	public int getApplyRoleId() {
		return applyRoleId;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public int getRoleLevel() {
		return roleLevel;
	}

	public String getContent() {
		return content;
	}
	
	

	public int getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "FriendApply [申请人id：" + applyRoleId + ", 申请人名称：" + roleName + ", 申请人等级：" + roleLevel
				+ ", 申请内容：" + content + "]";
	}

	public static final ResultBuilder<FriendApply> BUILDER = new ResultBuilder<FriendApply>() {

		@Override
		public FriendApply build(ResultSet result) throws SQLException {
			int roleId = result.getInt("friendId");
			int applyId = result.getInt("roleId");
			String roleName = result.getString("roleName");
			int roleLevel = result.getInt("roleLevel");
			String content = result.getString("content");
			int userId = result.getInt("userId");
			return new FriendApply(applyId, roleId, roleName, roleLevel, content, userId);
		}
	};
}
