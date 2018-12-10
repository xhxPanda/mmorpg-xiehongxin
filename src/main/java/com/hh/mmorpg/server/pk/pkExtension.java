package com.hh.mmorpg.server.pk;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * pk
 * 
 * @author xhx
 *
 */
@Extension(id = 13)
public class pkExtension {

	private PKService service = PKService.INSATNCE;

	private static final String INVITE_ROLE_PK = "inviteRolePK"; // 邀请对方pk
	private static final String DEAL_PK_APPLICATION = "dealPKApplication"; // 处理pk的请求

	public static final String NOTIFY_ROLE_INVITED_PK = "邀请pk";
	public static final String NOTIFY_ROLE_JOIN_PK = "进入pk";
	public static final String NOTIFY_ROLE_PK_WIN = "pk胜出";
	public static final String NOTIFY_ROLE_PK_LOSE = "pk落败";

	@CmdService(cmd = INVITE_ROLE_PK)
	public void inviteRolePK(User user, CMDdomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(2);
		ReplyDomain domain = service.invitePK(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, domain);
	}

	@CmdService(cmd = DEAL_PK_APPLICATION)
	public void dealPKApplication(User user, CMDdomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(2);
		ReplyDomain domain = service.dealPKApply(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, domain);
	}
	
	public static void notifyUserMessage(User user, ReplyDomain replyDomain) {
//		replyDomain.setStringDomain("cmd", NOTIFY_ROLE_INVITED_PK);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

}
