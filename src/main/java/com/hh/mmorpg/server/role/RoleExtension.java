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

	private static final String GET_ALL_ROLE = "getAllRole"; // 获取用户所有的角色
	private static final String CREATE_ROLE = "createRole"; // 创建角色
	private static final String USE_ROLE = "useRole"; // 使用角色
	private static final String GET_USER_NOW_ROLE = "getUserNowRole"; // 获取用户当前角色的信息
	private static final String TRANSFER_OCCUPATION = "transferOccupation"; // 转职

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
		ReplyDomain reply = service.getUserUsingRole(user);
		ExtensionSender.INSTANCE.sendReply(user, reply);
	}
	
	@CmdService(cmd = TRANSFER_OCCUPATION)
	public void transferOccupation(User user, CMDdomain cmdDomain) {
		int occupationId = cmdDomain.getIntParam(2);
		
		ReplyDomain reply = service.transferOccupation(user, occupationId);
		ExtensionSender.INSTANCE.sendReply(user, reply);
	}

}
