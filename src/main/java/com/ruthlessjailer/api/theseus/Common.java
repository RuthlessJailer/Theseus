package com.ruthlessjailer.api.theseus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

public final class Common {

	public static <T extends Runnable> BukkitTask runTask(final T task) {
		return runTaskLater(0, task);
	}

	public static BukkitTask runTaskLater(final int delayTicks, final Runnable task) {
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		final JavaPlugin      instance  = PluginBase.getInstance();
		final BukkitRunnable  runnable;

		if (instance == null) {//runs anyway if the plugin is disable improperly
			task.run();
			throw new IllegalStateException("Plugin instance is null. Task was run anyway.");
		}

		if (task instanceof BukkitRunnable) {
			runnable = (BukkitRunnable) task;
			return delayTicks == 0 ? runnable.runTask(instance) : runnable.runTaskLater(instance, delayTicks);
		} else {
			return delayTicks == 0
				   ? scheduler.runTask(instance, task)
				   : scheduler.runTaskLater(instance, task, delayTicks);
		}
	}

	public static String getString(final String string) { return string == null ? "" : string; }

	public static <T> T[] copyToEnd(final T[] objects, final int start) {
		return Arrays.copyOfRange(objects, start, objects.length - 1);
	}

	public static <T> T[] copyFromStart(final T[] objects, final int end) {
		return Arrays.copyOfRange(objects, 0, end);
	}

}
