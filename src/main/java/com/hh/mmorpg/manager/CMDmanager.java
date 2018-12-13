package com.hh.mmorpg.manager;

import com.hh.mmorpg.CmdHandlerMananger;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.service.user.UserService;

public class CmdManager {

	public static final CmdManager INSTANCE = new CmdManager();

	public void dealCMD(CmdDomain cmdDomain) {
		// 获取模块
		String cmd = cmdDomain.getStringParam(0);
		if (cmd.equals("doLogin") || cmd.equals("doRegister")) {
			UserService.INSTANCE.doLoginOrRegister(cmdDomain);
		} else {
			Integer userId = cmdDomain.getIntParam(1);
			if (userId != null) {
				User user = UserService.INSTANCE.getUser(userId);
				if (user == null) {
					ExtensionSender.INSTANCE.sendReply(cmdDomain.getChannel(), ReplyDomain.FAILE);
				}
				CmdHandlerMananger.INSATANCE.invokeHandler(user, cmdDomain);
			}
		}
	}

}
