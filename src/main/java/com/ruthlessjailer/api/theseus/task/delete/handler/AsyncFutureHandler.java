package com.ruthlessjailer.api.theseus.task.delete.handler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public final class AsyncFutureHandler implements FutureHandler {

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final ForkJoinPool             pool      = new ForkJoinPool();
	private final Map<UUID, AsyncTask>     repeating = new ConcurrentHashMap<>();

	/**
	 * Returns a delayed executor service to provide for {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
	 *
	 * @param delay how long (in milliseconds) to set the delay for the executor
	 *
	 * @return the delayed executor service that executes an operation to the pool
	 */
	private Executor delay(final long delay) {
		return r -> this.scheduler.schedule(() -> this.pool.execute(r), delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	@Override
	public <T> CompletableFuture<T> supply(final Supplier<T> supplier) {
		return supply(supplier, 100);
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
	public <T> CompletableFuture<T> supply(final Supplier<T> supplier, final long delay) {
		return CompletableFuture.supplyAsync(supplier, delay(delay));
	}

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	@Override
	public <T> CompletableFuture<T> call(final Callable<T> callable) {
		return call(callable, 100);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	@Override
	public <T> CompletableFuture<T> call(final Callable<T> callable, final long delay) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return callable.call();
			} catch (final Throwable t) {
				t.printStackTrace();
				return null;
			}
		}, delay(delay));
	}

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 *
	 * @return the {@link Future} representation of the {@link Runnable}
	 */
	@Override
	public <T> CompletableFuture<T> run(final Runnable runnable, final T value) {
		return run(runnable, value, 100);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the {@link Future} representation of the {@link Runnable}
	 */
	@Override
	public <T> CompletableFuture<T> run(final Runnable runnable, final T value, final long delay) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return value;
		}, delay(delay));
	}

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
	@Override
	public UUID repeat(final Consumer<UUID> consumer, final long interval) {
		return repeat(consumer, interval, -1);
	}

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
	@Override
	public UUID repeat(final Consumer<UUID> consumer, final long interval, final int count) {
		final UUID id = UUID.randomUUID();
		synchronized (this.repeating) {
			this.repeating.put(id, new AsyncTask(this.scheduler.scheduleAtFixedRate(() -> {
				final AsyncTask task = this.repeating.get(id);
				if (task.getRuns() >= task.getRepeat() && task.getRepeat() != -1) {
					cancel(id);//cancel the task
				} else {
					task.increment();//increment runs and execute
					consumer.accept(id);
				}
			}, interval, interval, TimeUnit.MILLISECONDS), count));
		}
		return id;
	}

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, see {@link FutureHandler#repeat(Consumer, long)}
	 *
	 * @see FutureHandler#repeat(Consumer, long)
	 */
	@Override
	public void cancel(final UUID id) {
		synchronized (this.repeating) {
			this.repeating.get(id).getFuture().cancel(false);
			this.repeating.remove(id);
		}
	}
}
