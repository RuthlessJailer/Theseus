package com.ruthlessjailer.api.theseus.task.handler;

import com.ruthlessjailer.api.theseus.PluginBase;
import com.ruthlessjailer.api.theseus.task.manager.TaskManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * @author RuthlessJailer
 */
public class SyncFutureHandler implements FutureHandler, Runnable {

	private final Queue<SyncTask> tasks = new ConcurrentLinkedQueue<>();

	SyncFutureHandler() {
		TaskManager.sync.repeat(this, 1);
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		if (!PluginBase.isMainThread()) {
			throw new IllegalStateException("Async");
		}

		if (this.tasks.isEmpty()) {
			return;
		}

		synchronized (this.tasks) {
			SyncTask task;
			while ((task = this.tasks.peek()) != null) {
				if (System.currentTimeMillis() >= task.getWhen()) {
					task.getFuture().run();
					this.tasks.poll();
				}
			}
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
	public <T> Future<T> later(final Supplier<T> supplier) {
		return run(supplier, 100);
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
	public <T> Future<T> run(final Supplier<T> supplier, final long delay) {
		final FutureTask<T> future = new FutureTask<>(supplier::get);
		this.tasks.add(new SyncTask<>(future, System.currentTimeMillis() + delay));
		return future;
	}
}
