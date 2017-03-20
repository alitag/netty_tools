package com.alitag.netty_tools;

import io.netty.channel.ChannelInitializer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * <p>
 * 该类是一个配置信息类。我们可以在这个类中集中设置各种信息，用于生成对应的netty的ServerBootstrap或者ClientBootstrap
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class NettyConfig {

	private static int DEFAULT_RECEIVE_BUFFER_SIZE = 1024;
	private static int DEFAULT_SEND_BUFFER_SIZE = 1024;
	private static boolean TCP_NO_DELAY = false;
	static {
		Socket unconnectedSocket = new Socket();
		try {
			DEFAULT_RECEIVE_BUFFER_SIZE = unconnectedSocket.getReceiveBufferSize();
			DEFAULT_SEND_BUFFER_SIZE = unconnectedSocket.getSendBufferSize();
			TCP_NO_DELAY = unconnectedSocket.getTcpNoDelay();
		} catch (SocketException se) {
			try {
				unconnectedSocket.close();
			} catch (IOException ioe) {
			}
		}
	}

	/**
	 * <p>
	 * 使用的ChannelInitializer
	 * </p>
	 */
	public ChannelInitializer<?> channelInitializer = new ChannelInitializeBuilder();

	/**
	 * <p>
	 * 是否启动线程池。默认启用。
	 * </p>
	 */
	public boolean threadPool = true;

	/**
	 * <p>
	 * 客户端连接到服务器时的超时时间，默认为1秒。仅对ConnectorBuilder有效。
	 * </p>
	 */
	public int connectTimeout = 1;

	/**
	 * <p>
	 * socket设置：是否重用地址（ip加端口），默认为true.
	 * </p>
	 */
	public boolean reuseAddress = true;

	/**
	 * <p>
	 * socket设置：是否从socket级别保持连接，默认为true
	 * </p>
	 */
	public boolean socket_keepAlive = true;

	/**
	 * <p>
	 * socket关闭时的延迟时间设置：soLinger，默认为 0
	 * </p>
	 */
	public int socket_soLinger = 0;

	/**
	 * <p>
	 * 接收缓存大小设置，windows XP系统中默认值为8096字节，即4KB.
	 * </p>
	 */
	public int receiver_buffer_size = DEFAULT_RECEIVE_BUFFER_SIZE;

	/**
	 * <p>
	 * 发送缓存大小设置，windows XP系统中默认为8096字节，即8KB
	 * </p>
	 */
	public int send_buffer_size = DEFAULT_SEND_BUFFER_SIZE;

	/**
	 * <P>
	 * 启用/禁用Nagle算法,是否不进行任何延迟就发送数据.Socket中默认设置为false。
	 * </P>
	 * 如果为true，则无论数据大小为多少，都会立刻发出；此时当数据小于TCP包头(40个Byte)大小时，容易产生过载现象，增加带宽占用。 <br>
	 * 如果为false，则当数据较小时（比如小于40个字节），会等到有更多待发送数据才封装成一个包一次性发出，最多会等待300ms。带宽占用减少但延迟增加
	 */
	public boolean tcp_no_delay = TCP_NO_DELAY;

	/**
	 * <p>
	 * 显示出当前的配置内容，格式为每行一个参数，每行形如：
	 * </p>
	 * <p>
	 * param: value
	 * </p>
	 * 
	 * @return 当前的配置内容
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("connectTimeout: " + connectTimeout).append(System.lineSeparator());
		sb.append("socket_reuseAddress: " + reuseAddress).append(System.lineSeparator());
		sb.append("socket_keepAlive: " + socket_keepAlive).append(System.lineSeparator());
		sb.append("socket_soLinger: " + socket_soLinger).append(System.lineSeparator());
		sb.append("receiver_buffer_size: " + receiver_buffer_size).append(System.lineSeparator());
		sb.append("send_buffer_size: " + send_buffer_size).append(System.lineSeparator());
		sb.append("tcp_no_delay: " + tcp_no_delay);
		return sb.toString();
	}

}
