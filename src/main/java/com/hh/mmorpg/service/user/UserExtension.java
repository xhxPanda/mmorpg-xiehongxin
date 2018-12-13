package com.hh.mmorpg.service.user;

import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.server.base.ExtensionSupport;

import io.netty.channel.Channel;

public class UserExtension extends ExtensionSupport {

	public static final String LOGIN = "doLogin";
	public static final String REGISTER = "doRegister";

	public static void notify(Channel channel, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(channel, replyDomain);
	}

}
