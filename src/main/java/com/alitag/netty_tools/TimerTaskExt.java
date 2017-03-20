package com.alitag.netty_tools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 该类扩展了TimerTask。增加了一个指定Timer的引用和一个getName()的虚方法。
 * <p>
 *
 * @author gchangyi
 * @version 1.0
 */
public abstract class TimerTaskExt extends TimerTask {
	private Timer owner;

	public Timer getOwner() {
		return owner;
	}

	public void setOwner(Timer owner) {
		this.owner = owner;
	}

	/**
	 * 得到task的名字
	 *
	 * @return
	 */
	public abstract String getName();
}
