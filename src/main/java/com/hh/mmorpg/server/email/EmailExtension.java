package com.hh.mmorpg.server.email;

import java.util.Map;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.Email;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.NotifiesWarehouse;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.ExtensionSender;

@Extension
public class EmailExtension {

	private EmailService service = EmailService.INSTANCE;

	private static final String GET_EMAIL_INFO = "getEmailInfo";
	private static final String SEND_EMAIL = "sendEmail";
	private static final String READ_EMAIL = "readEmail";
	private static final String GET_EMAIL_BONUS = "getEmailBonus";

	private static final String NOTIFY_RECIPIENT_EMAIL = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_RECIPIENT_EMAIL");

	@CmdService(cmd = GET_EMAIL_INFO)
	public void getEmailInfo(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(1);
		Map<Integer, Email> emailMap = service.getRoleEmail(roleId);
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setListDomain("email", emailMap.values());

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SEND_EMAIL)
	public void sendEmail(User user, CmdDomain cmdDomain) {
		int recipientRoleId = cmdDomain.getIntParam(1);
		String bonusStr = cmdDomain.getStringParam(2);
		String content = cmdDomain.getStringParam(3);
		int recipientId = cmdDomain.getIntParam(4);

		ReplyDomain replyDomain = service.sendEmail(user, recipientRoleId, recipientId, bonusStr, content);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = READ_EMAIL)
	public void readEmail(User user, CmdDomain cmdDomain) {
		int emailId = cmdDomain.getIntParam(1);

		ReplyDomain replyDomain = service.readEmail(user, emailId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_EMAIL_BONUS)
	public void getEmailBonus(User user, CmdDomain cmdDomain) {
		int emailId = cmdDomain.getIntParam(1);
		ReplyDomain replyDomain = service.getEmailBonus(user, emailId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUserGetEmail(User user, Email email) {
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", NOTIFY_RECIPIENT_EMAIL);
		replyDomain.setStringDomain("e", email.toString());
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
