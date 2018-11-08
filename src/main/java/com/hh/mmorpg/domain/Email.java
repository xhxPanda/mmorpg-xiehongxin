package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class Email {

	private int roleId;
	private int id;
	private String content;
	private String bonus;
	private boolean read;
	private int senderRoleId;
	private String senderRoleName;

	public Email(int roleId, int id, String content, String bonus, boolean read, int senderRoleId,
			String senderRoleName) {
		this.roleId = roleId;
		this.id = id;
		this.content = content;
		this.bonus = bonus;
		this.read = read;
		this.senderRoleId = senderRoleId;
		this.senderRoleName = senderRoleName;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public String getBonus() {
		return bonus;
	}

	public boolean isRead() {
		return read;
	}

	public int getSenderRoleId() {
		return senderRoleId;
	}

	public String getSenderRoleName() {
		return senderRoleName;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return "Email [roleId=" + roleId + ", id=" + id + ", content=" + content + ", 附件=" + bonus + ", 是否已读=" + read
				+ ", 发送人角色id=" + senderRoleId + ", 发送人名字=" + senderRoleName + "]";
	}

	public static final ResultBuilder<Email> BUILDER = new ResultBuilder<Email>() {

		@Override
		public Email build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt("roleId");
			int id = result.getInt("emailId");
			String content = result.getString("content");
			String bonus = result.getString("bonus");
			boolean read = result.getBoolean("read");
			String senderRoleName = result.getString("senderName");
			int senderRoleId = result.getInt("senderId");
			return new Email(roleId, id, content, bonus, read, senderRoleId, senderRoleName);
		}
	};

}
