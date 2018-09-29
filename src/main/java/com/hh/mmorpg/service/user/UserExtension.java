package com.hh.mmorpg.service.user;

import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.server.base.ExtensionSupport;

import io.netty.channel.Channel;

public class UserExtension extends ExtensionSupport {

	static final String NOTIFY_LOGIN = "1_1";
	static final String NOTIFY_REGISTER = "1_2";

	public static void notifyLogin(Channel channel, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_LOGIN);

		ExtensionSender.INSTANCE.sendReply(channel, replyDomain);
	}

	public static void notifyRegister(Channel Channel, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_REGISTER);

		ExtensionSender.INSTANCE.sendReply(Channel, replyDomain);
	}
}
