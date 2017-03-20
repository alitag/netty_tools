package com.alitag.netty_tools;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>
 * 该类用于快速构造一个ServerBootstrap。它可以使用默认的NettyConfig值或另外指定各参数。
 * </p>
 * <p>
 * <b>线程安全</b> 该类线程安全，因为已经做了合适的同步处理
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 * 
 */
public class AcceptorBuilder {

	/** 用于持有各种参数，初始值为null，将在构造函数中被初始化。 */
	private final NettyConfig config;

	/** 用于持有将会生成的ServerBootstrap对象 */
	private ServerBootstrap acceptor;

	private EventLoopGroup bossGroup; // 连接线程
	private EventLoopGroup workerGroup; // 处理线程组

	/**
	 * <p>
	 * 默认构造函数。将产生一个NettyConfig对象并使用其默认值。
	 * </p>
	 * 
	 * @see NettyConfig
	 */
	public AcceptorBuilder() {
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
	public AcceptorBuilder(NettyConfig config) {
		ArgumentValidator.notNull(config, "config");
		this.config = config;
	}

	/**
	 * <p>
	 * 得到生成的ServerBootstrap对象
	 * </p>
	 * 
	 * @return 生成的ServerBootstrap对象
	 */
	public synchronized ServerBootstrap getAcceptor() {
		if (acceptor == null) {
			acceptor = new ServerBootstrap();
			if (config.threadPool) {
				bossGroup = new NioEventLoopGroup();
				workerGroup = new NioEventLoopGroup();
				acceptor.group(bossGroup, workerGroup);
			} else {
				acceptor.group();
			}
			acceptor.channel(NioServerSocketChannel.class);
			acceptor.option(ChannelOption.SO_KEEPALIVE, config.socket_keepAlive);
			acceptor.option(ChannelOption.SO_REUSEADDR, config.reuseAddress);
			acceptor.option(ChannelOption.TCP_NODELAY, config.tcp_no_delay);
			acceptor.option(ChannelOption.SO_SNDBUF, config.send_buffer_size);
			acceptor.option(ChannelOption.SO_LINGER, config.socket_soLinger); // 如果soLinger为0，当连接断开后，可以很快重用该端口
			acceptor.childHandler(config.channelInitializer);
		}
		return acceptor;
	}

	/**
	 * <p>
	 * 关闭线程池。如果没有启用或者已经关闭，不会有任何影响(优雅地退出)
	 * </p>
	 * 
	 * @see EventLoopGroup#shutdownGracefully()
	 */
	public synchronized void shutdownGracefully() {
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
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
		if (bossGroup != null) {
			bossGroup.shutdownNow();
			bossGroup = null;
		}
		if (workerGroup != null) {
			workerGroup.shutdownNow();
			workerGroup = null;
		}
	}

	/**
	 * 检查线程池是否被关闭。如果线程池没有开启或者已经关闭，则返回true。
	 */
	public synchronized boolean groupIsDisabledOrTerminated() {
		return bossGroup == null || bossGroup.isTerminated() || workerGroup == null || workerGroup.isTerminated();
	}

	/**
	 * <p>
	 * 得到持有的NettyConfig对象。对于该config的修改不会对已经生成的ServerBootstrap对象产生影响。
	 * </p>
	 * 
	 * @return 得到持有的NettyConfig对象
	 */
	public NettyConfig getNettyConfig() {
		return config;
	}
}
