package com.hh.mmorpg.server.pk;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.NotifiesWarehouse;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * pk
 * 
 * @author xhx
 *
 */
@Extension
public class PKExtension {

	private PKService service = PKService.INSATNCE;

	private static final String INVITE_ROLE_PK = "inviteRolePK"; // 邀请对方pk
	private static final String DEAL_PK_APPLICATION = "dealPKApplication"; // 处理pk的请求

	/**
	 * notify
	 */
	public static final String NOTIFY_ROLE_INVITED_PK = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_ROLE_INVITED_PK");
	public static final String NOTIFY_ROLE_JOIN_PK = NotifiesWarehouse.INSTANCE.getNotifyContent("NOTIFY_ROLE_JOIN_PK");
	public static final String NOTIFY_ROLE_PK_WIN = NotifiesWarehouse.INSTANCE.getNotifyContent("NOTIFY_ROLE_PK_WIN");
	public static final String NOTIFY_ROLE_PK_LOSE = NotifiesWarehouse.INSTANCE.getNotifyContent("NOTIFY_ROLE_PK_LOSE");

	@CmdService(cmd = INVITE_ROLE_PK)
	public void inviteRolePK(User user, CmdDomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(1);
		ReplyDomain domain = service.invitePK(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, domain);
	}

	@CmdService(cmd = DEAL_PK_APPLICATION)
	public void dealPKApplication(User user, CmdDomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(1);
		ReplyDomain domain = service.dealPKApply(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, domain);
	}

	public static void notifyUserMessage(User user, ReplyDomain replyDomain) {
//		replyDomain.setStringDomain("cmd", NOTIFY_ROLE_INVITED_PK);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

}
