package com.hh.mmorpg.server.role;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 3)
public class RoleExtension {

	private RoleService service = RoleService.INSTANCE;

	private static final String GET_ALL_ROLE = "3_1";
	private static final String CREATE_ROLE = "3_2";
	private static final String USER_ROLE = "3_3";
	private static final String GET_USER_NOW_ROLE = "3_4";

	@CmdService(cmd = GET_ALL_ROLE)
	public void getAllRole(User user, CMDdomain cmdDomain) {

		ReplyDomain replyDomain = service.getAllRole(user);

		replyDomain.setStringDomain("cmd", GET_ALL_ROLE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = CREATE_ROLE)
	public void creatRole(User user, CMDdomain cmdDomain) {
		int roleDomainId = cmdDomain.getIntParam("rdi");
		String name = cmdDomain.getStringParam("n");

		ReplyDomain replyDomain = service.creatRole(user, roleDomainId, name);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = USER_ROLE)
	public void useRole(User user, CMDdomain cmdDomain) {
		int roleId = cmdDomain.getIntParam("ri");
		ReplyDomain reply = service.userUseRole(user, roleId);

		reply.setStringDomain("cmd", USER_ROLE);
		ExtensionSender.INSTANCE.sendReply(user, reply);
	}

	@CmdService(cmd = GET_USER_NOW_ROLE)
	public void getUserNowRolet(User user, CMDdomain cmdDomain) {
		service.getUserUsingRole(user);
	}
	
}
