package com.hh.mmorpg.service.user;

import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.role.RoleService;

import io.netty.channel.Channel;

public class UserService {

	public static final UserService INSTANCE = new UserService();

	private ConcurrentHashMap<Integer, User> userChannelMap;
	private ConcurrentHashMap<String, Integer> channelUserMap;

	private UserService() {
		this.userChannelMap = new ConcurrentHashMap<Integer, User>();
		this.channelUserMap = new ConcurrentHashMap<String, Integer>();

		EventHandlerManager.INSATNCE.register(this);
	}

	public void doLoginOrRegister(CmdDomain cmdDomain) {
		if (cmdDomain.getStringParam(0).equals(UserExtension.LOGIN)) {
			doLogin(cmdDomain);
		} else {
			doRegister(cmdDomain);
		}
	}

	private void doRegister(CmdDomain cmdDomain) {
		int userId = cmdDomain.getIntParam(1);
		String password = cmdDomain.getStringParam(2);
		User user = new User(userId, password);

		int result = UserDao.INSTANCE.insertUser(user);
		if (result > 0) {
			doLogin(user, cmdDomain.getChannel());
		}
	}

	private void doLogin(CmdDomain cmdDomain) {

		int userId = cmdDomain.getIntParam(1);
		String password = cmdDomain.getStringParam(2);

		User user = UserDao.INSTANCE.selectUser(userId, password);
		if (user == null) {
			UserExtension.notifyLogin(cmdDomain.getChannel(), ReplyDomain.FAILE);
		} else {
			doLogin(user, cmdDomain.getChannel());

			ReplyDomain replyDomain = new ReplyDomain("登录" + ResultCode.SUCCESS);
			UserExtension.notifyLogin(cmdDomain.getChannel(), replyDomain);
		}
	}

	private void doLogin(User user, Channel channel) {

		int userId = user.getUserId();

		// 顶下线
		if (userChannelMap.containsKey(userId)) {
			Channel oldChannel = userChannelMap.get(userId).getChannel();
			userChannelMap.get(userId).setChannel(channel);
			channelUserMap.remove(oldChannel.id().asShortText());
		} else {
			user.setChannel(channel);
		}

		channelUserMap.put(channel.id().asShortText(), user.getUserId());
		userChannelMap.put(user.getUserId(), user);
	}

	public User getUser(Integer userId) {
		return userChannelMap.get(userId);
	}

	public void userLost(Channel channel) {
		int userId = channelUserMap.remove(channel.id().asShortText());
		User user = userChannelMap.remove(userId);
		if (user.getChannel() != null && user.getChannel().isOpen()) {
			user.getChannel().close();
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		// 抛出用户离线事件
		UserLostData data = new UserLostData(user, role);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.USER_LOST, new EventDealData<UserLostData>(data));
	}

	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		int userId = userLostData.getUser().getUserId();

		System.out.println("userId为：" + userId + "的用户下线了");
	}
}
