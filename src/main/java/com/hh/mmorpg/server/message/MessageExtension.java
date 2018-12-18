package com.hh.mmorpg.server.message;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * 
 * @author xhx
  *  消息处理
 */

@Extension
public class MessageExtension {
	private MessageService service = MessageService.INSTANCE;

	private static final String SEND_WORLD_MESSAGE = "sendWorldMessage";
	private static final String SEND_TO_USER = "sendToUser";

	public static final String NOTIFY_USER_WORLD_MESSAGE = "世界信息";
	private static final String NOTIFY_USER_MESSAGE = "私聊信息";

	@CmdService(cmd = SEND_WORLD_MESSAGE)
	public void sendWorldMessage(User user, CmdDomain cmDdomain) {
		String content = cmDdomain.getStringParam(1);

		ReplyDomain replyDomain = service.sendWorldMessage(content);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SEND_TO_USER)
	public void sendToUser(User user, CmdDomain cmdDomain) {
		String content = cmdDomain.getStringParam(1);
		int roleId = cmdDomain.getIntParam(2);
		
		ReplyDomain replyDomain = service.sendMessageToUser(user, roleId, content);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	public static void notifyUserMessage(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_MESSAGE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
