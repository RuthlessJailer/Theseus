package com.ruthlessjailer.api.theseus;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vadim Hagedorn
 */
public final class Common {

	/**
	 * Schedule a task to run.
	 *
	 * @param task the task to run
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	public static <T extends Runnable> BukkitTask runTask(@NonNull final T task) {
		return runTaskLater(0, task);
	}

	/**
	 * Schedule a task to run.
	 * If the plugin instance is null the task will run immediately, independent of the Bukkit scheduler.
	 *
	 * @param delayTicks how long to wait (in ticks)
	 * @param task       the task to run
	 *
	 * @return the {@link BukkitTask} representing the task
	 */
	public static BukkitTask runTaskLater(final int delayTicks, @NonNull final Runnable task) {
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

	/**
	 * Returns the string or a blank string if it is null.
	 *
	 * @param string the string to check
	 *
	 * @return the string or {@code ""}
	 */
	public static String getString(final String string) { return string == null ? "" : string; }

	/**
	 * Performs a regex escape on each character in the string (places {@code "\\"} before each reserved (regex) character.
	 *
	 * @param string the string to escape
	 *
	 * @return the escaped string or {@code ""}
	 */
	public static String escape(final String string) {
		final StringBuilder result = new StringBuilder();

		for (final char c : getString(string).toCharArray()) {
			if (c == '\\'
				|| c == '^'
				|| c == '$'
				|| c == '.'
				|| c == '|'
				|| c == '?'
				|| c == '*'
				|| c == '+'
				|| c == '-'
				|| c == '('
				|| c == ')'
				|| c == '{'
				|| c == '[') {
				result.append("\\");
			}
			result.append(c);
		}

		return result.toString();
	}

	/**
	 * Copies an array from the given start index to the end.
	 *
	 * @param array the array to copy
	 * @param start the index to copy from
	 *
	 * @return the new array
	 */
	public static <T> T[] copyToEnd(@NonNull final T[] array, final int start) {
		return Arrays.copyOfRange(array, start, array.length);
	}

	/**
	 * Copies an array from the start until the given ending index.
	 *
	 * @param array the array to copy
	 * @param end   the index to copy to
	 *
	 * @return the new array
	 */
	public static <T> T[] copyFromStart(@NonNull final T[] array, final int end) {
		return Arrays.copyOfRange(array, 0, end);
	}

	/**
	 * Convenience method to convert varags to array.
	 *
	 * @param objects the objects to put into an array
	 *
	 * @return the objects as an array
	 */
	@SafeVarargs
	public static <T> T[] asArray(@NonNull final T... objects) {
		return objects;
	}

	/**
	 * Converts an array into a different type.
	 *
	 * @param array     the array to convert
	 * @param result    an empty array of the desired type
	 * @param converter the {@link TypeConverter} used to convert
	 *
	 * @return the converted array
	 */
	public static <O, N> N[] convert(@NonNull final O[] array,
									 @NonNull final N[] result,
									 @NonNull final TypeConverter<O, N> converter) {

		Checks.verify(array.length == result.length,
					  "Arrays are not the same length!");

		int i = 0;
		for (final O old : array) {
			result[i] = converter.convert(old);
			i++;
		}

		return result;
	}

	/**
	 * Converts an iterable into a different type list.
	 *
	 * @param iterable  the {@link Iterable} to convert
	 * @param converter the {@link TypeConverter} used to convert
	 *
	 * @return the converted list
	 */
	public static <O, N> List<N> convert(@NonNull final Iterable<O> iterable,
										 @NonNull final TypeConverter<O, N> converter) {

		final List<N> result = new ArrayList<>();

		for (final O old : iterable) {
			result.add(converter.convert(old));
		}

		return result;
	}

}
