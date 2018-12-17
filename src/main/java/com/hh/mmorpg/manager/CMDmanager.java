package com.hh.mmorpg.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hh.mmorpg.CmdHandlerMananger;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.service.user.UserService;

import io.netty.channel.Channel;

/**
 * 命令处理类
 * 
 * @author 37
 *
 */
public class CmdManager {

	public static final CmdManager INSTANCE = new CmdManager();

	private ExecutorService executorService;

	private CmdManager() {
		executorService = Executors.newFixedThreadPool(5);
	}

	/**
	 * 每一个命令传进来都放到线程池中运行保证不会堵塞nettyIo
	 * 
	 * @param cmdDomain
	 */
	public void dealCMD(Channel channel, CmdDomain cmdDomain) {

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				String cmd = cmdDomain.getStringParam(0);
				if (cmd.equals("doLogin") || cmd.equals("doRegister")) {
					UserService.INSTANCE.doLoginOrRegister(cmdDomain);
				} else {
					Integer userId = cmdDomain.getIntParam(1);
					if (userId != null) {
						User user = UserService.INSTANCE.getUserByChannelId(channel.id().asShortText());
						if (user == null) {
							ExtensionSender.INSTANCE.sendReply(cmdDomain.getChannel(), ReplyDomain.FAILE);
						}
						CmdHandlerMananger.INSATANCE.invokeHandler(user, cmdDomain);
					}
				}
			}
		});
	}
}
