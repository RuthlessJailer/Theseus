package com.ruthlessjailer.api.theseus.task.delete.handler;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public final class SyncFutureHandler implements FutureHandler, Runnable {

	private static final int                 MAX_TPS          = 20;
	private static final int                 DEFAULT_ALLOCATE = 50;//ms
	private static final int                 MIN_ALLOCATE     = 5;//ms
	//	@Getter
//	private              int                 targetTPS        = 18;
	private              long                last             = System.currentTimeMillis();//time
	private              long                allocate         = DEFAULT_ALLOCATE;//ms
	private final        Deque<SyncTask>     tasks            = new ConcurrentLinkedDeque<>();
	private final        Deque<SyncTask>     secondary        = new ConcurrentLinkedDeque<>();
	private final        Map<UUID, SyncTask> repeating        = new HashMap<>();
	private final        Lock                lock             = new ReentrantLock();
	private volatile     Thread              main;
	private volatile     boolean             initialized;
	private volatile     Allocator           allocator;

	SyncFutureHandler() {
//		TaskManager.sync.repeat(this, 1);
	}

	/**
	 * This method can only be called once. Call this method with the main thread and allocator.<p>
	 * In order for this to function properly the {@link #run()} method must be called repeatedly on the main thread.
	 *
	 * @param main      the main thread
	 * @param allocator a {@link Allocator} that takes in the last run time and current allocate and provides a {@link Long} for the amount of milliseconds that is allocated to run sync tasks. One
	 *                  task will be executed
	 *                  every {@link #run()} call
	 *                  regardless of the allocate returned.
	 */
	public void initialize(@NonNull final Thread main, final Allocator allocator) {
		if (this.initialized) {
			throw new UnsupportedOperationException("Already initialized!");
		}

		synchronized (this.lock) {
			this.main        = main;
			this.allocator   = allocator == null ? (l, a) -> 100L : allocator;
			this.initialized = true;
		}
	}

	private boolean isMainThread() {
		if (!this.initialized) {
			throw new UnsupportedOperationException("Not initialized.");
		}

		return Thread.currentThread() == this.main;
	}

	@Override
	public void run() {
		if (isMainThread()) {
			throw new IllegalStateException("Async");
		}

		executeRepeating();

		this.allocate = this.allocator.apply(this.last, this.allocate);
		this.last     = System.currentTimeMillis();

		if (this.tasks.isEmpty()) {
			execute(this.secondary, this.last, this.allocate);
			return;
		}

		execute(this.tasks, this.last, this.allocate);
	}

	/*public void setTargetTPS(final int tps) {
		if (tps > MAX_TPS) {
			throw new IllegalArgumentException("TPS cannot exceed " + MAX_TPS);
		}

		this.targetTPS = tps;
	}

	private long getAllocate() {
		//1 tick = 50ms
		//goal is to go for 2 ticks (or whatever target tps is set to)
		//2 ticks is 100ms or 1/10 of a sec

		final long current = System.currentTimeMillis();
		final long diff    = DEFAULT_ALLOCATE + (this.last - (this.last = current));
		//0 is a perfect tick, negative we're behind, and positive ahead

		if (diff == 0) {//we're on schedule
			this.allocate = Math.min(DEFAULT_ALLOCATE, this.allocate + 1);
		} else if (diff < 0) {//we're behind; take that from this tick's allocate
			this.allocate = Math.max(DEFAULT_ALLOCATE / 10, this.allocate + diff);
		} else if (Common.hasTPSCounter() && !Common.getTPSCounter().isAbove(this.targetTPS)) {//tps is low but last tick was ok
			this.allocate = Math.max(DEFAULT_ALLOCATE / 10, this.allocate - 1);
		}

		return this.allocate;
	}*/

	private void executeRepeating() {
		final Iterator<Map.Entry<UUID, SyncTask>> iterator = this.repeating.entrySet().iterator();
		Map.Entry<UUID, SyncTask>                 entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			final SyncTask task = entry.getValue();

			if (task.getRuns() >= task.getRepeat() && task.getRepeat() != -1) {
				iterator.remove();//cancel the task concurrently
			} else {
				task.increment();//increment runs and execute
				task.getRawFuture().get();
			}
		}
	}

	private void execute(@NonNull final Deque<SyncTask> deque, final long start, final long allocate) {
		boolean  wait = false;
		SyncTask task;
		synchronized (deque) {
			do {
				task = deque.peek();
				if (task == null) {
					if (wait) {
						try {
							deque.wait(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
						wait = false;
						deque.poll();//remove null task and move on
						task = deque.peek();
					} else {
						break;
					}
				}

				if (task != null) {
					if (System.currentTimeMillis() >= task.getWhen()) {
						task.getFuture().run();
						wait = true;
						deque.poll();//remove the ran task
					}
				}
			}
			while (System.currentTimeMillis() - start <= allocate);
		}
	}

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	@Override
	public <T> Future<T> supply(final Supplier<T> supplier) {
		return supply(supplier, 100);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	@Override
	public <T> Future<T> supply(final Supplier<T> supplier, final long delay) {
		return supply(supplier, delay, QueuePriority.NORMAL);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 * @param priority the {@link QueuePriority} for the task
	 *
	 * @return the {@link Future} representation of the {@link Supplier}
	 */
	@SneakyThrows
	public <T> Future<T> supply(final Supplier<T> supplier, final long delay, final QueuePriority priority) {
		final SyncTask<T> task = new SyncTask<>(supplier, System.currentTimeMillis() + delay);

		if (isMainThread()) {//don't schedule if it's on the main thread already
			return task.getFuture();
		}

		switch (priority) {
			case IMMEDIATE:
				synchronized (this.tasks) {
					this.tasks.offerFirst(task);
				}
				break;
			case NORMAL:
				synchronized (this.tasks) {
					this.tasks.offer(task);
				}
				break;
			case SECONDARY:
				synchronized (this.secondary) {
					this.secondary.offer(task);
				}
				break;
		}

		return task.getFuture();
	}

	/**
	 * Runs the task {@code 100} milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	@Override
	public <T> Future<T> call(final Callable<T> callable) {
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
	public <T> Future<T> call(final Callable<T> callable, final long delay) {
		return call(callable, delay, QueuePriority.NORMAL);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the delay in milliseconds
	 * @param priority the {@link QueuePriority} for the task
	 *
	 * @return the {@link Future} representation of the {@link Callable}
	 */
	public <T> Future<T> call(final Callable<T> callable, final long delay, final QueuePriority priority) {
		final SyncTask<T> task = new SyncTask<>(() -> {
			try {
				return callable.call();
			} catch (final Throwable t) {
				throw new UnsupportedOperationException("Error in callable.");
			}
		}, System.currentTimeMillis() + delay);

		if (isMainThread()) {//don't schedule if it's on the main thread already
			return task.getFuture();
		}

		switch (priority) {
			case IMMEDIATE:
				synchronized (this.tasks) {
					this.tasks.offerFirst(task);
				}
				break;
			case NORMAL:
				synchronized (this.tasks) {
					this.tasks.offer(task);
				}
				break;
			case SECONDARY:
				synchronized (this.secondary) {
					this.secondary.offer(task);
				}
				break;
		}
		return task.getFuture();
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
	public <T> Future<T> run(final Runnable runnable, final T value) {
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
	public <T> Future<T> run(final Runnable runnable, final T value, final long delay) {
		return run(runnable, value, delay, QueuePriority.NORMAL);
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the delay in milliseconds
	 * @param priority the {@link QueuePriority} for the task
	 *
	 * @return the {@link Future} representation of the {@link Runnable}
	 */
	public <T> Future<T> run(final Runnable runnable, final T value, final long delay, final QueuePriority priority) {
		final SyncTask<T> task = new SyncTask<>(() -> {
			runnable.run();
			return value;
		}, System.currentTimeMillis() + delay);

		if (isMainThread()) {//don't schedule if it's on the main thread already
			return task.getFuture();
		}

		switch (priority) {
			case IMMEDIATE:
				synchronized (this.tasks) {
					this.tasks.offerFirst(task);
				}
				break;
			case NORMAL:
				synchronized (this.tasks) {
					this.tasks.offer(task);
				}
				break;
			case SECONDARY:
				synchronized (this.secondary) {
					this.secondary.offer(task);
				}
				break;

		}
		return task.getFuture();
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
		final Supplier supplier = () -> {
			consumer.accept(id);
			return null;
		};
		synchronized (this.repeating) {
			this.repeating.put(id, new SyncTask(supplier, count));
		}
		return id;
	}

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, obtained from {@link FutureHandler#repeat(Consumer, long) scheduling a repeating task}
	 *
	 * @see FutureHandler#repeat(Consumer, long)
	 */
	@Override
	public void cancel(final UUID id) {
		synchronized (this.repeating) {
			this.repeating.remove(id);
		}
	}

}
