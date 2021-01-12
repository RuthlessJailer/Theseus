package com.ruthlessjailer.api.theseus.task.delete.handler;

import lombok.Getter;

import java.util.concurrent.ScheduledFuture;

/**
 * @author RuthlessJailer
 */
@Getter
public final class AsyncTask<T> extends Task {

	public AsyncTask(final ScheduledFuture<T> task) {
		this(task, -1);
	}

	public AsyncTask(final ScheduledFuture<T> task, final int repeat) {
		super(repeat);
		this.future = task;
	}

	private final ScheduledFuture<T> future;
}
