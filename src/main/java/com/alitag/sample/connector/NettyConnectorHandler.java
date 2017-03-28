package com.alitag.sample.connector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyConnectorHandler extends SimpleChannelInboundHandler<String> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channel.writeAndFlush("Hello world!" + System.lineSeparator());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String param) throws Exception {
		System.out.print(ctx.channel() + " : " + param + System.lineSeparator());

	}

}