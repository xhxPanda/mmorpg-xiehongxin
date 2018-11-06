package com.hh.mmorpg.server.message;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * 
 * @author xhx
  *  消息处理
 */

@Extension(id = 6)
public class MessageExtension {
	private MessageService service = MessageService.INSTANCE;

	private static final String SEND_WORLD_MESSAGE = "6_1";
	private static final String SEND_TO_USER = "6_2";

	public static final String NOTIFY_USER_WORLD_MESSAGE = "6_100";
	private static final String NOTIFY_USER_MESSAGE = "6_101";

	@CmdService(cmd = SEND_WORLD_MESSAGE)
	public void sendWorldMessage(User user, CMDdomain cmDdomain) {
		String content = cmDdomain.getStringParam("c");

		ReplyDomain replyDomain = service.sendWorldMessage(content);
		replyDomain.setStringDomain("cmd", SEND_WORLD_MESSAGE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SEND_TO_USER)
	public void sendToUser(User user, CMDdomain cmDdomain) {
		String content = cmDdomain.getStringParam("c");
		int userId = cmDdomain.getIntParam("ruid");
		
		ReplyDomain replyDomain = service.sendMessageToUser(user, userId, content);
		replyDomain.setStringDomain("cmd", SEND_TO_USER);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	public static void notifyUserMessage(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_MESSAGE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
