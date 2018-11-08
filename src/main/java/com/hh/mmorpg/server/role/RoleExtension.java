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

	private static final String GET_ALL_ROLE = "getAllRole";
	private static final String CREATE_ROLE = "createRole";
	private static final String USE_ROLE = "useRole";
	private static final String GET_USER_NOW_ROLE = "getUserNowRole";

	@CmdService(cmd = GET_ALL_ROLE)
	public void getAllRole(User user, CMDdomain cmdDomain) {

		ReplyDomain replyDomain = service.getAllRole(user);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = CREATE_ROLE)
	public void creatRole(User user, CMDdomain cmdDomain) {
		int roleDomainId = cmdDomain.getIntParam(2);
		String name = cmdDomain.getStringParam(3);

		ReplyDomain replyDomain = service.creatRole(user, roleDomainId, name);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = USE_ROLE)
	public void useRole(User user, CMDdomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);
		ReplyDomain reply = service.userUseRole(user, roleId);
		ExtensionSender.INSTANCE.sendReply(user, reply);
	}

	@CmdService(cmd = GET_USER_NOW_ROLE)
	public void getUserNowRolet(User user, CMDdomain cmdDomain) {
		service.getUserUsingRole(user);
	}

}
