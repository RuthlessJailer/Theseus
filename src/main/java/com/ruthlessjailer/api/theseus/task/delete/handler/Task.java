package com.ruthlessjailer.api.theseus.task.delete.handler;

import lombok.Getter;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class Task {

	private final int repeat;
	private final int runs = 0;

	public Task(final int repeat) {
		this.repeat = repeat;
	}

	public synchronized void increment() {

	}

}
