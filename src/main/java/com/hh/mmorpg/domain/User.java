package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

import io.netty.channel.Channel;

public class User {

	private Channel channel;
	private int userId;
	private String password;

	public User(int userId, String password) {
		this.password = password;
		this.userId = userId;
	}

	public Channel getChannel() {
		return channel;
	}

	public int getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public static final ResultBuilder<User> BUILDER = new ResultBuilder<User>() {

		@Override
		public User build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			 while (result.next()) {
				 int userId = result.getInt(1);
				 String password = result.getString(2);
				 return new User(userId, password);
			 }
			return null;
		}
			 
	};
}
