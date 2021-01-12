package com.ruthlessjailer.api.theseus.task.handler

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * @author RuthlessJailer
 */
interface FutureHandler {


	//Suppliers


	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	fun <T> supply(supplier: Supplier<T?>): Future<T?>

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	fun <T> supply(supplier: Supplier<T?>, delay: Long): Future<T?>


	//Callables


	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	fun <T> call(callable: Callable<T?>): Future<T?>

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	fun <T> call(callable: Callable<T?>, delay: Long): Future<T?>


	//Runnables


	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	fun <T> run(runnable: Runnable, value: T?): Future<T?>

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	fun <T> run(runnable: Runnable, value: T?, delay: Long): Future<T?>

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param consumer the task to run
	 * @param interval the amount of milliseconds to wait in between executions
	 *
	 * @return the id of the task
	 *
	 * @see FutureHandler.cancel
	 */
	fun repeat(consumer: Consumer<UUID>, interval: Long): UUID

	/**
	 * Runs the task given amount of milliseconds later for a given amount of iterations.
	 *
	 * @param consumer the task to run
	 * @param interval the amount of milliseconds to wait in between executions
	 * @param count    the amount of times to repeat the task before auto-cancelling
	 *
	 * @return the id of the task
	 *
	 * @see FutureHandler.cancel
	 */
	fun repeat(consumer: Consumer<UUID>, interval: Long, count: Int): UUID

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, see [FutureHandler.repeat]
	 *
	 * @see FutureHandler.repeat
	 */
	fun cancel(id: UUID)

	companion object {
		val async = AsyncFutureHandler()
		val sync = SyncFutureHandler()
	}
}