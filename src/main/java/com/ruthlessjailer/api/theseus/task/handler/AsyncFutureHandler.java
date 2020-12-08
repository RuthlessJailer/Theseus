package com.ruthlessjailer.api.theseus.task.handler;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public class AsyncFutureHandler implements FutureHandler {

	private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(0);
	private final ForkJoinPool             pool      = new ForkJoinPool();

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link CompletableFuture} representation of the {@link Supplier}
	 */
	@Override
	public <T> CompletableFuture<T> later(final Supplier<T> supplier) {
		return run(supplier, 100);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 *
	 * @return the {@link CompletableFuture} representation of the {@link Supplier}
	 */
	@Override
	public <T> CompletableFuture<T> run(final Supplier<T> supplier, final long delay) {
		return CompletableFuture.supplyAsync(supplier, delay(delay));
	}

	private Executor delay(final long delay) {
		return r -> this.scheduler.schedule(() -> this.pool.execute(r), delay, TimeUnit.MILLISECONDS);
	}
}
