package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 好友实体
 * 
 * @author xhx
 *
 */
public class Friend {

	private int roleId;
	private int friendId;
	private long time;
	private String friendName;
	private String friendOccupation;
	private int friendLevel;
	private boolean online;

	public Friend(int roleId, int friendId, long time) {
		this.roleId = roleId;
		this.friendId = friendId;
		this.time = time;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getFriendId() {
		return friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public String getFriendOccupation() {
		return friendOccupation;
	}

	public int getFriendLevel() {
		return friendLevel;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public void setFriendName(String string) {
		this.friendName = string;
	}

	public void setFriendOccupation(String friendOccupation) {
		this.friendOccupation = friendOccupation;
	}

	public void setFriendLevel(int friendLevel) {
		this.friendLevel = friendLevel;
	}

	public long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "Friend [好友id：" + friendId + ", 好友名称：" + friendName + ", 职业：" + friendOccupation
				+ ", 等级：" + friendLevel + ", 是否在线：" + online + "]";
	}

	public static final ResultBuilder<Friend> BUILDER = new ResultBuilder<Friend>() {

		@Override
		public Friend build(ResultSet result) throws SQLException {
			int roleId = result.getInt("roleId");
			int friendId = result.getInt("friendRoleId");
			long time = result.getLong("time");
			return new Friend(roleId, friendId, time);
		}
	};
}
