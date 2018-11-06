package com.hh.mmorpg.manager;

import com.hh.mmorpg.CmdHandlerMananger;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.service.user.UserService;

public class CMDmanager {

	public static final CMDmanager INSTANCE = new CMDmanager();

	public void dealCMD(CMDdomain cmddomain) {
		// 获取模块
		String cmd = cmddomain.getStringParam(0);
		if (cmd.equals("doLogin") || cmd.equals("doRegister")) {
			UserService.INSTANCE.doLoginOrRegister(cmddomain);
		} else {
			Integer userId = cmddomain.getIntParam(1);
			if (userId != null) {
				User user = UserService.INSTANCE.getUser(userId);
				if (user == null) {
					ExtensionSender.INSTANCE.sendReply(cmddomain.getChannel(), ReplyDomain.FAILE);
				}
				CmdHandlerMananger.INSATANCE.invokeHandler(user, cmddomain);
			}
		}
	}

}
