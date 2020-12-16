package com.ruthlessjailer.api.theseus.task.handler;

import lombok.Getter;

import java.util.concurrent.ScheduledFuture;

/**
 * @author RuthlessJailer
 */
@Getter
public final class AsyncTask<T> {

	public AsyncTask(final ScheduledFuture<T> task) {
		this(task, -1);
	}

	public AsyncTask(final ScheduledFuture<T> task, final int repeat) {
		this.future = task;
		this.repeat = repeat;
	}

	private final ScheduledFuture<T> future;
	private final int                repeat;

	private volatile int runs = 0;

	public synchronized void increment() { this.runs++; }
}
