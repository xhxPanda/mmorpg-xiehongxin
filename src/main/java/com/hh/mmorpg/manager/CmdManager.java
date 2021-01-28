package com.hh.mmorpg.manager;

import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hh.mmorpg.CmdHandlerMananger;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.server.scene.SceneService;
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

	private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
			.setNameFormat("demo-pool-%d").build();

	private static ExecutorService executorService;

	private CmdManager() {
		executorService = new ThreadPoolExecutor(5, 200,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
	}

	/**
	 * 每一个命令传进来都放到线程池中运行保证不会堵塞nettyIo
	 * 
	 * @param cmdDomain
	 */
	public void dealCMD(Channel channel, CmdDomain cmdDomain) {

		String cmd = cmdDomain.getStringParam(0);
		if (cmd.equals("doLogin") || cmd.equals("doRegister")) {

			addTask(new Runnable() {

				@Override
				public void run() {
					UserService.INSTANCE.doLoginOrRegister(cmdDomain);
				}
			});

		} else {
			
			User user = UserService.INSTANCE.getUserByChannelId(channel.id().asShortText());
			if (user == null) {
				ExtensionSender.INSTANCE.sendReply(cmdDomain.getChannel(), ReplyDomain.FAILE);
			}
			Scene scene = SceneService.  INSTANCE.getUserScene(user.getUserId());	
			if(scene == null) {
				// 用户有可能处于一个游离于场景的状态，比如还没选人物的时候，或者是在创建人物角色的时候
				addTask(new Runnable() {

					@Override
					public void run() {
						CmdHandlerMananger.INSATANCE.invokeHandler(user, cmdDomain);
					}
				});
				
			} else {
				scene.addCmdDomain(new CmdRunner(user, cmdDomain));
			}
			

		}
	}

	/**
	 * 线程进入队列
	 */
	public void addTask(Runnable runnable) {
		executorService.execute(runnable);
	}

}
