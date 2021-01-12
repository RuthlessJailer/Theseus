package com.ruthlessjailer.api.theseus.task.handler

import java.util.*
import java.util.concurrent.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * @author RuthlessJailer
 */
class AsyncFutureHandler : FutureHandler {
	private val scheduler = Executors.newSingleThreadScheduledExecutor()
	private val pool = ForkJoinPool()
	private val repeating: MutableMap<UUID, AsyncTask<*>> = ConcurrentHashMap()

	/**
	 * Returns a delayed executor service to provide for [CompletableFuture.supplyAsync].
	 *
	 * @param delay how long (in milliseconds) to set the delay for the executor
	 *
	 * @return the delayed executor service that executes an operation to the pool
	 */
	private fun delay(delay: Long): Executor {
		return Executor { r: Runnable -> scheduler.schedule({ pool.execute(r) }, delay, TimeUnit.MILLISECONDS) }
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	override fun <T> supply(supplier: Supplier<T?>): CompletableFuture<T?> {
		return supply(supplier, 100)
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 *
	 * @return the [CompletableFuture] representation of the [Supplier]
	 */
	override fun <T> supply(supplier: Supplier<T?>, delay: Long): CompletableFuture<T?> {
		return CompletableFuture.supplyAsync(supplier, delay(delay))
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	override fun <T> call(callable: Callable<T?>): CompletableFuture<T?> {
		return call(callable, 100)
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	override fun <T> call(callable: Callable<T?>, delay: Long): CompletableFuture<T?> {
		return CompletableFuture.supplyAsync({
												 try {
													 return@supplyAsync callable.call()
												 } catch (t: Throwable) {
													 t.printStackTrace()
													 return@supplyAsync null
												 }
											 }, delay(delay))
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	override fun <T> run(runnable: Runnable, value: T?): CompletableFuture<T?> {
		return run(runnable, value, 100)
	}

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	override fun <T> run(runnable: Runnable, value: T?, delay: Long): CompletableFuture<T?> {
		return CompletableFuture.supplyAsync({
												 runnable.run()
												 value
											 }, delay(delay))
	}

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
	override fun repeat(consumer: Consumer<UUID>, interval: Long): UUID {
		return repeat(consumer, interval, -1)
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
	 * @see FutureHandler.cancel
	 */
	override fun repeat(consumer: Consumer<UUID>, interval: Long, count: Int): UUID {
		//TODO fixme
		val id = UUID.randomUUID()
		synchronized(repeating) {
			repeating.put(id, AsyncTask<Any?>(scheduler.scheduleAtFixedRate({
																				val task = repeating[id]!!
																				if (task.runs >= task.repeat && task.repeat != -1) {
																					cancel(id) //cancel the task
																				} else {
																					task.increment() //increment runs and execute
																					consumer.accept(id)
																				}
																			}, interval, interval, TimeUnit.MILLISECONDS) as ScheduledFuture<Any?>, count))
		}
		return id
	}

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, see [FutureHandler.repeat]
	 *
	 * @see FutureHandler.repeat
	 */
	override fun cancel(id: UUID) {
		synchronized(repeating) {
			repeating[id]!!.task.cancel(false)
			repeating.remove(id)
		}
	}
}
