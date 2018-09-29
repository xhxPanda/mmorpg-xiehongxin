package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserInfo {

	private int userId;
	private String nickName;
	private int sex;

	public UserInfo(int userId, String nickName, int sex) {
		this.userId = userId;
		this.nickName = nickName;
		this.sex = sex;
	}

	public int getUserId() {
		return userId;
	}

	public String getNickName() {
		return nickName;
	}

	public int getSex() {
		return sex;
	}

	public static final ResultBuilder<UserInfo> BUILD = new ResultBuilder<UserInfo>() {

		@Override
		public UserInfo build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			return new UserInfo(result.getInt("userId"), result.getString("nickName"), result.getInt("sex"));
		}
	};
}
