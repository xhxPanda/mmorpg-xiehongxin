package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.hh.mmorpg.jdbc.ResultBuilder;

import io.netty.channel.Channel;

/**
 * user实体类
 * 
 * @author xhx
 *
 */
public class User {

	private int userId;
	private String name;
	private String password;
	private Channel channel;

	// 包
	private ConcurrentLinkedQueue<Runnable> cmdDomains;

	public User(int userId, String name, String password) {
		this.password = password;
		this.userId = userId;
		this.name = name;

		cmdDomains = new ConcurrentLinkedQueue<>();
	}

	public int getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	/**
	 * 把包放进队列中
	 * 
	 * @param cmdDomain
	 */
	public void addCmdDomain(Runnable cmdDomain) {
		cmdDomains.add(cmdDomain);
	}

	public ConcurrentLinkedQueue<Runnable> getCmdDomains() {
		return cmdDomains;
	}

	public static final ResultBuilder<User> BUILDER = new ResultBuilder<User>() {
		@Override
		public User build(ResultSet result) throws SQLException {
			int userId = result.getInt("userId");
			String password = result.getString("password");
			String name = result.getString("name");
			return new User(userId, name, password);
		}

	};
}
