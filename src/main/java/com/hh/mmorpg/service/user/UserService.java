package com.hh.mmorpg.service.user;

import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.Increment.IncrementManager;
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

/**
 * 处理登录或注册的处理类
 * 
 * @author xhx
 *
 */
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
		int userId = IncrementManager.INSTANCE.increase("userId");
		String name = cmdDomain.getStringParam(1);
		String password = cmdDomain.getStringParam(2);
		User user = new User(userId, name, password);

		doLogin(user, cmdDomain.getChannel());
		UserDao.INSTANCE.insertUser(user);
		ReplyDomain replyDomain = new ReplyDomain("注册" + ResultCode.SUCCESS);
		replyDomain.setIntDomain("用户id", userId);
		UserExtension.notify(cmdDomain.getChannel(), replyDomain);
	}

	private void doLogin(CmdDomain cmdDomain) {

		int userId = cmdDomain.getIntParam(1);
		String password = cmdDomain.getStringParam(2);

		User user = UserDao.INSTANCE.selectUser(userId, password);
		if (user == null) {
			UserExtension.notify(cmdDomain.getChannel(), ReplyDomain.PASSWORD_OR_USERID_WORONG);
		} else {
			doLogin(user, cmdDomain.getChannel());

			ReplyDomain replyDomain = new ReplyDomain("登录" + ResultCode.SUCCESS);
			
			ReplyDomain allRoleDomain = RoleService.INSTANCE.getAllRole(user);
			UserExtension.notify(cmdDomain.getChannel(), replyDomain);
			UserExtension.notify(cmdDomain.getChannel(), allRoleDomain);
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
