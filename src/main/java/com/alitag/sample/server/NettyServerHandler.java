package com.alitag.sample.server;

import com.alitag.netty_tools.ChannelTaskHelper;
import com.alitag.netty_tools.TimerTaskExt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		final Channel channel = ctx.channel();
		ChannelTaskHelper.addAutoCancelTask(channel, new TimerTaskExt() {
			private long preTime;
			public String getName() {
				return "Netty time server.";
			}

			public void run() {
				long current = System.currentTimeMillis();
				if (current != preTime) {
					channel.writeAndFlush(System.currentTimeMillis() + System.lineSeparator());
					preTime = current;
				}
			};
		}, 0, 1000);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state().equals(IdleState.READER_IDLE)) {
				// TODO
			} else if (event.state().equals(IdleState.WRITER_IDLE)) {
				channel.writeAndFlush(System.currentTimeMillis() + System.lineSeparator());
			} else if (event.state().equals(IdleState.ALL_IDLE)) {
				// TODO
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String param) throws Exception {
		System.out.print(ctx.channel() + " : " + param);
	}

}