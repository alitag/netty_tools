package com.alitag.sample.server;

import java.net.InetSocketAddress;

import com.alitag.netty_tools.AcceptorBuilder;
import com.alitag.netty_tools.NettyConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

/**
 * Netty 服务端damo服务入口
 * 
 * @author gchangyi
 *
 */
public class NettyServerService {

	private AcceptorBuilder builder;

	private ServerBootstrap acceptor;

	public void start() throws Exception {
		NettyConfig nettyConfig = new NettyConfig();
		nettyConfig.channelInitializer = new NettyServerInitializer();
		builder = new AcceptorBuilder(nettyConfig);
		acceptor = builder.getAcceptor();
		// 绑定端口，同步等待成功
	    ChannelFuture future = acceptor.bind(new InetSocketAddress(9000)).sync();
	    // 等待服务端监听端口关闭，等待服务端链路关闭之后main函数才退出
	    future.channel().closeFuture().sync();
	}

	public void stop() {
		if (builder != null) {
			builder.shutdownGracefully();
			builder = null;
		}
	}

	 public static void main(String[] args) throws Exception {
		 NettyServerService serverService = new NettyServerService();
		 serverService.start();
	 }
}
