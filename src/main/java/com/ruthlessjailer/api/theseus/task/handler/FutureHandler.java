package com.ruthlessjailer.api.theseus.task.handler;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public interface FutureHandler {

	AsyncFutureHandler async = new AsyncFutureHandler();
	SyncFutureHandler  sync  = new SyncFutureHandler();


	//Suppliers


	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	<T> Future<T> supply(final Supplier<T> supplier);

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	<T> Future<T> supply(final Supplier<T> supplier, final long delay);


	//Callables


	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	<T> Future<T> call(final Callable<T> callable);

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	<T> Future<T> call(final Callable<T> callable, final long delay);


	//Runnables


	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 *
	 * @return the {@link Future} representation of the {@link Runnable}
	 */
	<T> Future<T> run(final Runnable runnable, final T value);

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the {@link Future} representation of the {@link Runnable}
	 */
	<T> Future<T> run(final Runnable runnable, final T value, final long delay);

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param consumer the task to run
	 * @param interval the amount of milliseconds to wait in between executions
	 *
	 * @return the id of the task
	 *
	 * @see FutureHandler#cancel(UUID)
	 */
	UUID repeat(final Consumer<UUID> consumer, final long interval);

	/**
	 * Runs the task given amount of milliseconds later for a given amount of iterations.
	 *
	 * @param consumer the task to run
	 * @param interval the amount of milliseconds to wait in between executions
	 * @param count    the amount of times to repeat the task before auto-cancelling
	 *
	 * @return the id of the task
	 *
	 * @see FutureHandler#cancel(UUID)
	 */
	UUID repeat(final Consumer<UUID> consumer, final long interval, final int count);

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, see {@link FutureHandler#repeat(Consumer, long)}
	 *
	 * @see FutureHandler#repeat(Consumer, long)
	 */
	void cancel(final UUID id);

}
