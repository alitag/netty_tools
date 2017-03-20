package com.alitag.netty_tools;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * <p>
 * 与ChannelHandlerContext相关的工具类。
 * </p>
 * <p>
 * 线程安全：该类线程安全，因为它只提供了无状态的工具函数。
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class ChannelHelper {

	/**
	 * 私有构造函数。防止被实例化。
	 */
	private ChannelHelper() {
		// do nothing
	}

	/**
	 * <p>
	 * 从channel得到本机的InetSocketAddress.如果没得到,返回null
	 * </p>
	 * 
	 * @param channel
	 *            当前的连接对象
	 * @return 本机的InetAddress.如果没得到,返回null
	 * @throws IllegalArgumentException
	 *             如果channel为null
	 */
	public static InetSocketAddress getLocalAddress(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		SocketAddress local = channel.localAddress();
		if (local != null && local instanceof InetSocketAddress) {
			return (InetSocketAddress) local;
		}
		return null;
	}

	/**
	 * <p>
	 * 得到本机ip.如果没得到,返回""
	 * </p>
	 * 
	 * @param channel
	 *            当前的连接对象
	 * @return 本机ip.如果没得到,返回""
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getLocalIp(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		InetSocketAddress local = getLocalAddress(channel);
		InetAddress address;
		if (local != null && ((address = local.getAddress()) instanceof Inet4Address)) {
			return ((Inet4Address) address).getHostAddress();
		}
		return "";
	}

	/**
	 * <p>
	 * 得到本机的端口.如果没得到,返回-1
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @return 本机的端口.如果没得到,返回-1
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static int getLocalPort(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		SocketAddress address = channel.localAddress();
		if (address != null && address instanceof InetSocketAddress) {
			return ((InetSocketAddress) address).getPort();
		}
		return -1;
	}

	/**
	 * <p>
	 * 由session得到对方的InetAddress.如果没得到,返回null
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @return 对方的InetAddress.如果没得到,返回null
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static InetSocketAddress getRemoteAddress(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		SocketAddress remote = channel.remoteAddress();
		if (remote != null && remote instanceof InetSocketAddress) {
			return (InetSocketAddress) remote;
		}
		return null;
	}

	/**
	 * <p>
	 * 由session得到对方的IP地址.如果没得到,返回""
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @return 对方的IP地址.如果没得到,返回""
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getRemoteIp(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		InetSocketAddress remote = getRemoteAddress(channel);
		InetAddress address;
		if (remote != null && (address = remote.getAddress()) instanceof InetAddress) {
			return ((InetAddress) address).getHostAddress();
		}
		return "";

	}

	/**
	 * <p>
	 * 对channel得到对方的Port.如果没得到,返回-1
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @return 对方的Port.如果没得到,返回-1
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static int getRemotePort(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		SocketAddress remote = channel.remoteAddress();
		if (remote != null && remote instanceof InetSocketAddress) {
			return ((InetSocketAddress) remote).getPort();
		}
		return -1;
	}

	/**
	 * <p>
	 * 得到一个channel的ip和port，并以特定的格式返回一个字符串。格式为: [/xxx.xxx.xxx.xxx: port]
	 * </p>
	 * <p>
	 * 因为函数经常用于日志中,所以从性能方面考虑,在channel已经连接成功后,会把对方的ip和port保存起来,以供以后直接使用.
	 * </p>
	 * <p>
	 * 如果没有得到对方的ip和port,返回的格式为[/: -1]
	 * </p>
	 * 
	 * @param channel
	 *            当前的连接对象
	 * @return 对方的ip和port,格式为[/xxx.xxx.xxx.xxx: port].如果没有得到对方的ip和port,返回的格式为[/: -1]
	 * @throws IllegalArgumentException
	 *             如果channel为null
	 */
	public static String getRemoteIpPort1(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		String ip_port = "";
		ip_port = (String) channel.attr(AttributeKey.valueOf(ip_port)).get();
		if ("".equals(ip_port) || ip_port == null) {
			ip_port = "[/" + getRemoteIp(channel) + ": " + getRemotePort(channel) + "]";
			if (channel.isActive()) {
				channel.attr(AttributeKey.valueOf(ip_port));
			}
		}
		return ip_port;
	}

	/**
	 * <p>
	 * 由channel得到对方的ip与port，由':'分隔,格式为xxx.xxx.xxx.xxx: port
	 * </p>
	 * <p>
	 * 如果没有得到对方的ip和port,返回的格式为: -1
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @return 得到对方的ip与port，由':'分隔,格式为xxx.xxx.xxx.xxx: port.没有得到对方的ip和port,返回的格式为: -1
	 * @throws IllegalArgumentException
	 *             如果channel为null
	 */
	public static String getRemoteIpPort2(Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		return getRemoteIp(channel) + ": " + getRemotePort(channel);
	}
}
