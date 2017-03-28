package com.alitag.sample.connector;

import com.alitag.netty_tools.ConnectorBuilder;
import com.alitag.netty_tools.NettyConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Netty 客户端damo服务入口
 * 
 * @author gchangyi
 *
 */
public class NettyConnectorService {
	private ConnectorBuilder builder;
	private Bootstrap connector;
	private Channel channel;

	public Channel start() throws Exception {
		NettyConfig nettyConfig = new NettyConfig();
		nettyConfig.channelInitializer = new NettyConnectorInitializer();
		nettyConfig.connectTimeout = 20; // 客户端连接超时时间, 20秒
		builder = new ConnectorBuilder(nettyConfig);
		connector = builder.getConnector();

		ChannelFuture future = connector.connect("127.0.0.1", 9000).sync();
		channel = future.channel();
		// Wait until the connection is closed.
		channel.closeFuture().sync();
		return channel;
	}

	public void stop() {
		if (builder != null) {
			builder.shutdownGracefully();
			builder = null;
		}
	}

	public static void main(String[] args) throws Exception {
		NettyConnectorService connectorService = new NettyConnectorService();
		connectorService.start();
	}

}
