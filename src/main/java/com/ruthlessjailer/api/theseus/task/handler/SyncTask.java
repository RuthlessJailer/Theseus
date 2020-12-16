package com.ruthlessjailer.api.theseus.task.handler;

import lombok.Getter;

import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
@Getter
public final class SyncTask<T> {

	public SyncTask(final Supplier<T> rawFuture, final long when) {
		this.future    = new FutureTask<>(rawFuture::get);
		this.when      = when;
		this.repeat    = -1;
		this.rawFuture = rawFuture;
	}

//	public SyncTask(final FutureTask<T> future, final long when) {
//		this.future    = future;
//		this.rawFuture = null;
//		this.when      = when;
//		this.repeat    = -1;
//	}

	public SyncTask(final Supplier<T> rawFuture) {
		this(rawFuture, -1);
	}

	public SyncTask(final Supplier<T> rawFuture, final int repeat) {
		this.future    = new FutureTask<>(rawFuture::get);
		this.rawFuture = rawFuture;
		this.when      = 0;
		this.repeat    = repeat;
	}

	private final FutureTask<T> future;
	private final Supplier<T>   rawFuture;
	private final long          when;
	private final int           repeat;

	private volatile int runs = 0;

	public synchronized void increment() { this.runs++; }

}
