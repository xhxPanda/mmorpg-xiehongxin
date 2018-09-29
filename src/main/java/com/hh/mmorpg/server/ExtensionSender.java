package com.hh.mmorpg.server;

import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ExtensionSender {

	public static final ExtensionSender INSTANCE = new ExtensionSender();

	public void sendReply(Channel channel, ReplyDomain replyDomain) {
		channel.writeAndFlush(replyDomain.parseJsonObject());
	}

	public void sendReply(User user, ReplyDomain replyDomain) {
		user.getChannel().writeAndFlush(new TextWebSocketFrame(replyDomain.parseJsonObject()));
	}
}
