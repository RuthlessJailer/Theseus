package com.ruthlessjailer.api.theseus.task.handler;

import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public interface FutureHandler {

	SyncFutureHandler  sync  = new SyncFutureHandler();
	AsyncFutureHandler async = new AsyncFutureHandler();

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	<T> Future<T> later(final Supplier<T> supplier);

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	<T> Future<T> run(final Supplier<T> supplier, final long delay);

}
