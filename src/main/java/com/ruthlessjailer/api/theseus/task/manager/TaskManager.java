package com.ruthlessjailer.api.theseus.task.manager;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author RuthlessJailer
 */
public interface TaskManager {

	SyncTaskManager  sync  = new SyncTaskManager();
	AsyncTaskManager async = new AsyncTaskManager();


	//Runnable methods


	/**
	 * Runs a task {@code 1} tick later.
	 *
	 * @param runnable the task to run
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	default BukkitTask later(final Runnable runnable) {
		return delay(runnable, 1);
	}

	/**
	 * Runs a task given amount of ticks later.
	 *
	 * @param runnable the task to run
	 * @param delay    the delay, it ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	BukkitTask delay(final Runnable runnable, final int delay);

	/**
	 * Repeats a task every given amount of ticks.
	 *
	 * @param runnable the task to run
	 * @param interval the interval and initial delay, in ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	BukkitTask repeat(final Runnable runnable, final int interval);

	/**
	 * Runs a task without the async catcher.
	 *
	 * @param runnable the task to run
	 */
	void unsafe(final Runnable runnable);


	//BukkitRunnable methods


	/**
	 * Runs a task {@code 1} tick later.
	 *
	 * @param runnable the task to run
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	default BukkitTask later(final BukkitRunnable runnable) {
		return delay(runnable, 1);
	}

	/**
	 * Runs a task given amount of ticks later.
	 *
	 * @param runnable the task to run
	 * @param delay    the delay, it ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	BukkitTask delay(final BukkitRunnable runnable, final int delay);

	/**
	 * Repeats a task every given amount of ticks.
	 *
	 * @param runnable the task to run
	 * @param interval the interval and initial delay, in ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	BukkitTask repeat(final BukkitRunnable runnable, final int interval);

}
