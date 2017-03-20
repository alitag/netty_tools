package com.alitag.netty_tools;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>
 * 该类用于快速构造一个Bootstrap。它可以使用默认的NettyConfig值或另外指定各参数。
 * </p>
 * <p>
 * <b>线程安全</b> 该类线程安全，因为已经做了合适的同步处理
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class ConnectorBuilder {

	/** 用于持有各种参数，初始值为null，将在构造函数中被初始化。 */
	private final NettyConfig config;

	/** 用于持有生成的Bootstrap对象 */
	private Bootstrap connector;

	private EventLoopGroup group;

	/**
	 * <p>
	 * 默认构造函数。将产生一个NettyConfig对象并使用其默认值。
	 * </p>
	 */
	public ConnectorBuilder() {
		config = new NettyConfig();
	}

	/**
	 * <p>
	 * 构造函数。将使用指定的NettyConfig中的参数。
	 * </p>
	 * 
	 * @param config
	 *            将会使用的参数
	 * @throws IllegalArgumentException
	 *             如果config为null
	 */
	public ConnectorBuilder(NettyConfig config) {
		ArgumentValidator.notNull(config, "config");
		this.config = config;
	}

	/**
	 * <p>
	 * 得到生成的Bootstrap对象
	 * </p>
	 * 
	 * @return 生成的Bootstrap对象
	 */
	public synchronized Bootstrap getConnector() {
		if (connector == null) {
			connector = new Bootstrap();
			if (config.threadPool) {
				group = new NioEventLoopGroup();
				connector.group(group);
			} else {
				connector.group();
			}
			connector.channel(NioSocketChannel.class);
			connector.option(ChannelOption.SO_KEEPALIVE, config.socket_keepAlive);
			connector.option(ChannelOption.SO_REUSEADDR, config.reuseAddress);
			connector.option(ChannelOption.TCP_NODELAY, config.tcp_no_delay);
			connector.option(ChannelOption.SO_RCVBUF, config.receiver_buffer_size);
			connector.option(ChannelOption.SO_LINGER, config.socket_soLinger); // 如果soLinger为0，当连接断开后，可以很快重用该端口
			connector.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.connectTimeout); // 多少秒没有连上服务器则返回
			connector.handler(config.channelInitializer);
		}
		return connector;
	}

	/**
	 * <p>
	 * 关闭线程池。如果没有启用或者已经关闭，不会有任何影响。
	 * </p>
	 * 
	 * @see EventLoopGroup#shutdownGracefully()
	 */
	public synchronized void shutdownGracefully() {
		if (group != null) {
			group.shutdownGracefully();
			group = null;
		}
	}

	/**
	 * <p>
	 * 立刻关闭线程池。池中未运行的任务将会被取消。
	 * </p>
	 * 
	 * @see EventLoopGroup#shutdownNow()
	 */
	@SuppressWarnings("deprecation")
	public synchronized void shutdownNow() {
		if (group != null) {
			group.shutdownNow();
			group = null;
		}
	}

	/**
	 * 检查线程池是否被关闭。如果线程池没有开启或者已经关闭，则返回true。
	 * 
	 * @return 线程池是否被关闭
	 */
	public synchronized boolean groupIsDisabledOrTerminated() {
		return group == null || group.isTerminated();
	}

	/**
	 * <p>
	 * 得到持有的NettyConfig对象。对于该config的修改不会对已经生成的Bootstrap对象产生影响。
	 * </p>
	 * 
	 * @return 得到持有的NettyConfig对象
	 */
	public NettyConfig getNettyConfig() {
		return config;
	}
}
