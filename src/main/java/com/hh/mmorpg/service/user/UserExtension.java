package com.hh.mmorpg.service.user;

import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.server.base.ExtensionSupport;

import io.netty.channel.Channel;

public class UserExtension extends ExtensionSupport {

	public static final String LOGIN = "1_1";
	public static final String REGISTER = "1_2";

	private static final String NOTIFY_LOGIN = "1_100";

	public static void notifyLogin(Channel channel, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_LOGIN);

		ExtensionSender.INSTANCE.sendReply(channel, replyDomain);
	}

}
