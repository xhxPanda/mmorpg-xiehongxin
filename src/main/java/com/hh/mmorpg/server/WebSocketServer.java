package com.hh.mmorpg.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer implements Server {

	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workGroup;

	private ServerBootstrap b;

	public WebSocketServer() {
		bossGroup = new NioEventLoopGroup(1);
		workGroup = new NioEventLoopGroup();
		b = new ServerBootstrap();

	}

	public void start() {
		b.group(bossGroup, workGroup).option(ChannelOption.SO_BACKLOG, 1024)
				.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
						ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));// 将多个消息转化成一个
						ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
						ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());// 解决大码流的问题
						ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
						ch.pipeline().addLast("http-server", new MessageHandler());
					}
				});
		ChannelFuture future = b.bind(new InetSocketAddress(8899));
		try {
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public void shutdown() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}

}