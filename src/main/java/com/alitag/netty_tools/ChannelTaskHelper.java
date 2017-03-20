/**
 *
 */
package com.alitag.netty_tools;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Timer;
import java.util.UUID;

/**
 * ChannelHandlerContext的辅助类,用于向Channel中加入一些自动执行的任务
 * <p>
 * 线程安全：该类线程安全。因为它是不可变类。
 * 
 * @author gchangyi
 * @version 1.0
 */
public class ChannelTaskHelper {

	private static final String PREFIX = ChannelTaskHelper.class.getName();

	private static final String KEY_CANCEL_AUTODISCONNECT = PREFIX + ".cancel_autodisconnect";

	/**
	 * 私有构造函数.防止实例化.
	 */
	private ChannelTaskHelper() {
		// do nothing
	}

	/**
	 * 设定该channel在指定的时间后自动断开.如果channel处于关闭状态,则不进行操作
	 * 
	 * @param channel
	 *            欲断开的channel
	 * @param seconds
	 *            多少秒后断开
	 * @throws IllegalArgumentException
	 *             如果channel为null,或者seconds<0
	 */
	public static void setAutoDisconnect(final Channel channel, final int seconds) {
		ArgumentValidator.notNull(channel, "channel");
		ArgumentValidator.isTrue(seconds >= 0, "seconds should be >=0: " + seconds);
		if (!channel.isActive())
			return;

		TimerTaskExt task = new TimerTaskExt() {
			@Override
			public void run() {
				AttributeKey<String> key = AttributeKey.valueOf(KEY_CANCEL_AUTODISCONNECT);
				if (channel.attr(key).get() != null) {
					//channel.attr(key).remove();	// netty 4.0, 4.1已经不推荐使用
					channel.attr(key).set(null);
				} else {
					channel.close();
				}
				if (this.getOwner() != null)
					this.getOwner().cancel();
			}

			@Override
			public String getName() {
				return "auto disconnect after " + seconds + "s";
			}
		};
		addAutoCancelTask(channel, task, seconds * 1000, 0);
	}

	/**
	 * 取消通过{@link #setAutoDisconnect()}设置的自动断开任务
	 * 
	 * @param session
	 *            欲取消断开任务的连接
	 * @throws IllegalArgumentException
	 *             如果channel为null
	 */
	public static void cancelAutoDisconnect(final Channel channel) {
		ArgumentValidator.notNull(channel, "channel");
		channel.attr(AttributeKey.valueOf(KEY_CANCEL_AUTODISCONNECT));
	}

	/**
	 * 增加一个在channel关闭时会自动取消的任务.可以设置为延时多久后执行,执行一次或每隔一段时间反复执行
	 * 
	 * @param channel
	 *            当前的连接对象
	 * @param task
	 *            要运行的任务
	 * @param delayMillis
	 *            多少毫秒后开始运行
	 * @param period
	 *            隔多久运行一次.如果为0,表示只运行一次
	 * @throws IllegalArgumentException
	 *             如果channel为null,或者task为null,或者delayMillis<0,或者period<0
	 */
	public static void addAutoCancelTask(final Channel channel, final TimerTaskExt task, long delayMillis, long period) {
		ArgumentValidator.notNull(channel, "channel");
		ArgumentValidator.notNull(task, "task");
		ArgumentValidator.isTrue(delayMillis >= 0, "delayMillis should be >=0: " + delayMillis);
		ArgumentValidator.isTrue(period >= 0, "period should be >=0: " + period);

		final Timer timer = new Timer(task.getName());
		task.setOwner(timer);
		if (period > 0) {
			timer.schedule(task, delayMillis, period);
		} else {
			timer.schedule(task, delayMillis);
		}

		// 生成唯一id
		final String attrId = "auto cacel task: " + UUID.randomUUID().toString();
		channel.attr(AttributeKey.valueOf(attrId));

		// channel关闭时自动停止该timer
		channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> paramF) throws Exception {
				AttributeKey<String> key = AttributeKey.valueOf(attrId);
				if (channel.attr(key).get() != null) {
					//channel.attr(key).remove();	// netty 4.0
					channel.attr(key).set(null);
					if (timer != null) {
						timer.cancel();
					}
				}
			}
		});
	}

}
