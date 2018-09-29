package com.hh.mmorpg.service.user;

import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;

import io.netty.channel.Channel;

public class UserService {

	public static final UserService INSTANCE = new UserService();

	private ConcurrentHashMap<Integer, User> userChannelMap;
	private ConcurrentHashMap<Channel, Integer> channelUserMap;

	private UserService() {
		userChannelMap = new ConcurrentHashMap<Integer, User>();
	}

	public void doLoginOrRegister(CMDdomain cmddomain) {
		if (cmddomain.getStringParam("cmd").equals(UserExtension.NOTIFY_LOGIN)) {
			doLogin(cmddomain);
		} else {
			doRegister(cmddomain);
		}
	}

	private void doRegister(CMDdomain cmddomain) {
		int userId = cmddomain.getIntParam("uid");
		String password = cmddomain.getStringParam("p");
		User user = new User(userId, password);

		UserDao.INSTANCE.insertUser(user);

		user.setChannel(cmddomain.getChannel());
		userChannelMap.put(user.getUserId(), user);
		ReplyDomain replyDomain = new ReplyDomain(1);
		replyDomain.setIntDomain("uid", user.getUserId());
		UserExtension.notifyRegister(cmddomain.getChannel(), replyDomain);
	}

	private void doLogin(CMDdomain cmddomain) {
		int userId = cmddomain.getIntParam("uid");
		String password = cmddomain.getStringParam("p");
		User user = UserDao.INSTANCE.selectUser(userId, password);
		if (user == null) {
			UserExtension.notifyLogin(cmddomain.getChannel(), ReplyDomain.FAILE);
		}
		user.setChannel(cmddomain.getChannel());
		userChannelMap.put(user.getUserId(), user);
		ReplyDomain replyDomain = new ReplyDomain(1);
		replyDomain.setIntDomain("uid", user.getUserId());
		UserExtension.notifyLogin(cmddomain.getChannel(), replyDomain);
	}

	public User getUser(Integer userId) {
		// TODO Auto-generated method stub
		return userChannelMap.get(userId);
	}

	public User getUser(Channel channel) {
		return userChannelMap.get(channelUserMap.get(channel));
	}
}
