package com.hh.mmorpg.server;

import com.alibaba.fastjson.JSONObject;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.manager.CMDmanager;
import com.hh.mmorpg.service.user.UserService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

// 信息处理类
public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		System.out.println("收到消息：" + msg.text());

		Channel channel = ctx.channel();
		String cmdData = msg.text();
		JSONObject jsonObject = JSONObject.parseObject(cmdData);

		CMDmanager.INSTANCE.dealCMD(new CMDdomain(channel, jsonObject));
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("handlerAdded：" + ctx.channel().id().asShortText());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		UserService.INSTANCE.userLost(ctx.channel());
		System.out.println("handlerRemoved：" + ctx.channel().id().asLongText());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("异常发生");
		System.out.println(cause.getCause().getMessage());
	}

}
