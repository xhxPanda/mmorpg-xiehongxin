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
		String cmd = cmddomain.getStringParam("cmd");
		if (cmd.equals("1_1") || cmd.equals("1_2")) {
			UserService.INSTANCE.doLoginOrRegister(cmddomain);
		} else {
			Integer userId = cmddomain.getIntParam("uid");
			if (userId != null) {
				User user = UserService.INSTANCE.getUser(userId);
				if (user == null) {
					ExtensionSender.INSTANCE.sendReply(cmddomain.getChannel(), ReplyDomain.FAILE);
				}
				CmdHandlerMananger.INSATANCE.invokeHandler(cmd, user, cmddomain);
			}
		}
	}

}
