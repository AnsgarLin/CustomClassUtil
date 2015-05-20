package com.custom.java.util;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {
	/**
	 * Start or Reset timer with given time
	 */
	public static Timer start(Timer timer, TimerTask task, long delay) {
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(task, delay);
		return timer;
	}

	/**
	 * Stop and clear timer
	 */
	public static Timer stop(Timer timer) {
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
		return null;
	}
}
