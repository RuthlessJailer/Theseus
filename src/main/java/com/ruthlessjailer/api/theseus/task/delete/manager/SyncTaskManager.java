package com.ruthlessjailer.api.theseus.task.delete.manager;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author RuthlessJailer
 */
public final class SyncTaskManager implements TaskManager {

	/**
	 * Runs a task given amount of ticks later.
	 *
	 * @param runnable the task to run
	 * @param delay    the delay, it ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	@Override
	public BukkitTask delay(final Runnable runnable, final int delay) {
		return Bukkit.getScheduler().runTaskLater(Checks.instanceCheck(), runnable, delay);
	}

	/**
	 * Repeats a task every given amount of ticks.
	 *
	 * @param runnable the task to run
	 * @param interval the interval and initial delay, in ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	@Override
	public BukkitTask repeat(final Runnable runnable, final int interval) {
		return Bukkit.getScheduler().runTaskTimer(Checks.instanceCheck(), runnable, interval, interval);
	}

	/**
	 * Runs a task without the async catcher.
	 *
	 * @param runnable the task to run
	 */
	@Override
	public void unsafe(final Runnable runnable) {
		later(() -> {
			ReflectUtil.setField("org.spigotmc.AsyncCatcher", "enabled", null, false);
			try {
				runnable.run();
			} catch (final Throwable t) {
				t.printStackTrace();
			}
			ReflectUtil.setField("org.spigotmc.AsyncCatcher", "enabled", null, true);
		});
	}

	/**
	 * Runs a task given amount of ticks later.
	 *
	 * @param bukkit the task to run
	 * @param delay  the delay, it ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	@Override
	public BukkitTask delay(final BukkitRunnable bukkit, final int delay) {
		return bukkit.runTaskLater(Checks.instanceCheck(), delay);
	}

	/**
	 * Repeats a task every given amount of ticks.
	 *
	 * @param bukkit   the task to run
	 * @param interval the interval and initial delay, in ticks
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	@Override
	public BukkitTask repeat(final BukkitRunnable bukkit, final int interval) {
		return bukkit.runTaskTimer(Checks.instanceCheck(), interval, interval);
	}

}
