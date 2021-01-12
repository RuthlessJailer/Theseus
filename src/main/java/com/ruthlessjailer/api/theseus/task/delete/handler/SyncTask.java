package com.ruthlessjailer.api.theseus.task.delete.handler;

import lombok.Getter;

import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
@Getter
public final class SyncTask<T> extends Task {

	public SyncTask(final Supplier<T> rawFuture, final long when) {
		super(-1);
		this.future    = new FutureTask<>(rawFuture::get);
		this.when      = when;
		this.rawFuture = rawFuture;
	}

	public SyncTask(final Supplier<T> rawFuture) {
		this(rawFuture, -1);
	}

	public SyncTask(final Supplier<T> rawFuture, final int repeat) {
		super(repeat);
		this.future    = new FutureTask<>(rawFuture::get);
		this.rawFuture = rawFuture;
		this.when      = 0;
	}

	private final FutureTask<T> future;
	private final Supplier<T>   rawFuture;
	private final long          when;

}
