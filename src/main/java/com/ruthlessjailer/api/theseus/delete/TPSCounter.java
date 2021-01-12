package com.ruthlessjailer.api.theseus.delete;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author RuthlessJailer
 */
@Getter
public final class TPSCounter implements Runnable {

	private static final int    POLL_INTERVAL            = 5;
	private static final long   IDEAL_TIME_BETWEEN_TICKS = 50;//ms
	private static final double IDEAL_TPS                = 20;

	private final Object   lock    = new Object();
	private final double[] history = new double[20];

	private int historyIndex = 0;

	private volatile long   tick         = 0;
	private volatile long   tickStart    = System.currentTimeMillis();//ms
	private volatile long   lastPoll     = System.currentTimeMillis();//ms
	private volatile long   lastPollTick = 0;
	private volatile double lastPollTPS  = 20d;


	TPSCounter() {
//		TaskManager.sync.repeat(this, 1);

		Arrays.fill(this.history, 20d);

		Chat.debug("TPSCounter", "Initialized TPS Counter.");
	}

	@Override
	public void run() {
		if (true/*!PluginBase.isMainThread()*/) {
			throw new IllegalStateException("Async");
		}

		synchronized (this.lock) {
			this.tickStart = System.currentTimeMillis();
			this.tick++;

			final long timeBetweenTicks = Math.max(this.tickStart - this.lastPoll, 1);

			final double tps = (POLL_INTERVAL * IDEAL_TIME_BETWEEN_TICKS * IDEAL_TPS) / (timeBetweenTicks * POLL_INTERVAL);
			//polling interval * 20 tps * 50 ms = ideal tps over period of polling interval
			//average that by dividing the actual time between ticks times the polling interval to get tps

			this.history[this.historyIndex++] = tps;

			if (this.historyIndex >= this.history.length) {//wrap-around history
				this.historyIndex = 0;
			}

			this.lastPoll = this.tickStart;
		}
	}

	public double getTPS() {
		synchronized (this.lock) {
			if (this.tick < this.lastPollTick + POLL_INTERVAL) {//don't update unless it's been a few ticks
				return this.lastPollTPS;
			}

			this.lastPollTick = this.tick;

			this.lastPollTPS = Arrays.stream(this.history).average().orElse(20d);

			return this.lastPollTPS;
		}
	}

	public boolean isAbove(final double target) {
		if (target <= 0) {
			return true;
		}

		return getTPS() >= target;
	}
}
