package com.hh.mmorpg.server.email;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 8)
public class EmailExtension {

	private EmailService service = EmailService.INSTANCE;

	private static final String GET_EMAIL_INFO = "8_1";
	private static final String SEND_EMAIL = "8_2";
	private static final String GET_EMAIL_BONUS = "8_3";

	private static final String NOTIFY_RECIPIENT_EMAIL = "8_100";
	
	@CmdService(cmd = GET_EMAIL_INFO)
	public void getEmailInfo(User user, CMDdomain cmdDomain) {
		
	}
	
	@CmdService(cmd = SEND_EMAIL)
	public void sendEmail(User user, CMDdomain cmdDomain) {
		int recipientRoleId = cmdDomain.getIntParam("rri");
		String bonusStr = cmdDomain.getStringParam("b");
		String content = cmdDomain.getStringParam("c");
		int recipientId = cmdDomain.getIntParam("rui");

		ReplyDomain replyDomain = service.sendEmail(user, recipientRoleId, recipientId, bonusStr, content);
		replyDomain.setStringDomain("cmd", SEND_EMAIL);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = GET_EMAIL_BONUS)
	public void getEmailBonus(User user, CMDdomain cmdDomain) {
		
	}
}
