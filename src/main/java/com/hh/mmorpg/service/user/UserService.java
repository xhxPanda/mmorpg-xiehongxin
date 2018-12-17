package com.hh.mmorpg.service.user;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.manager.CmdManager;
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

	// userId与user的map
	private ConcurrentHashMap<Integer, User> userChannelMap;
	// channel与userId的map
	private ConcurrentHashMap<String, Integer> channelUserMap;
	
	ScheduledExecutorService executorService;

	private UserService() {
		this.userChannelMap = new ConcurrentHashMap<Integer, User>();
		this.channelUserMap = new ConcurrentHashMap<String, Integer>();
		
		executorService = Executors.newSingleThreadScheduledExecutor();
		start();
		EventHandler.INSTANCE.addHandler(EventType.USER_LOST, userLostEvent);
	}

	/**
	 * 登录与注册的分发器
	 * 
	 * @param cmdDomain
	 */
	public void doLoginOrRegister(CmdDomain cmdDomain) {
		if (cmdDomain.getStringParam(0).equals(UserExtension.LOGIN)) {
			doLogin(cmdDomain);
		} else {
			doRegister(cmdDomain);
		}
	}

	/**
	 * 真正处理注册的类
	 * 
	 * @param cmdDomain
	 */
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

	/**
	 * 处理登录
	 * 
	 * @param cmdDomain
	 */
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

	/**
	 * 因为登录跟注册都需要直接登录，所以就抽象一个方法出来
	 * 
	 * @param user
	 * @param channel
	 */
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

	/**
	 * 获取user
	 * 
	 * @param userId
	 * @return
	 */
	public User getUser(Integer userId) {
		return userChannelMap.get(userId);
	}

	/**
	 * 使用channle id获取User
	 * 
	 * @param channelId
	 * @return
	 */
	public User getUserByChannelId(String channelId) {
		Integer userId = channelUserMap.get(channelId);

		if (userId == null) {
			return null;
		}

		return userChannelMap.get(userId);
	}

	/**
	 * 用户掉线的处理类
	 * 
	 * @param channel
	 */
	public void userLost(Channel channel) {
		int userId = channelUserMap.remove(channel.id().asShortText());
		User user = userChannelMap.remove(userId);
		if (user.getChannel() != null && user.getChannel().isOpen()) {
			user.getChannel().close();
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		// 抛出用户离线事件
		UserLostData data = new UserLostData(user, role);
		EventHandler.INSTANCE.invodeMethod(EventType.USER_LOST, data);
	}

	private EventBuilder<UserLostData> userLostEvent = new EventBuilder<UserLostData>() {

		@Override
		public void handler(UserLostData userLostData) {
			int userId = userLostData.getUser().getUserId();

			System.out.println("userId为：" + userId + "的用户下线了");
		}
	};
	

	public void start() {
		
		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				UserService.INSTANCE.runCmdDomain();
			}
		}, 0, 200, TimeUnit.MICROSECONDS);
	}

	/**
	 * 分发命令执行，令每一个用户的命令能有序执行
	 */
	public void runCmdDomain() {
		for (User user : userChannelMap.values()) {
			try {
				CmdManager.INSTANCE.addTask(user.getCmdDomains().take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
