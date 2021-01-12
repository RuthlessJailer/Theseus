package com.ruthlessjailer.api.theseus.task.handler

import lombok.SneakyThrows
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Future
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.collections.HashMap

/**
 * @author RuthlessJailer
 */
class SyncFutureHandler internal constructor() : FutureHandler, Runnable {

	companion object {
		const val DEFAULT_ALLOCATE = 100L
		const val MIN_ALLOCATE = 5L
	}

	//	private var targetTPS = 18
	private var last = System.currentTimeMillis() //time
	private lateinit var main: Thread
	private lateinit var allocator: Allocator

	private var allocate = DEFAULT_ALLOCATE
		//ms
		get() {
			val value = allocator.apply(last, field)
			last = System.currentTimeMillis()
			field = value

			return field
		}
	private val tasks: Deque<SyncTask<*>> = ConcurrentLinkedDeque()
	private val secondary: Deque<SyncTask<*>> = ConcurrentLinkedDeque()
	private val repeating: MutableMap<UUID, SyncTask<*>> = HashMap()

	/**
	 * This method can only be called once. Call this method with the main thread and allocator.
	 * In order for this to function properly the [run] method must be called repeatedly on the main thread.
	 *
	 * @param main      the main thread
	 * @param allocator an [Allocator] that takes in the last run time and current allocate and provides a [Long] for the amount of milliseconds that is allocated to run sync tasks. One task will be executed every [run()] call regardless of the allocate returned
	 */
	fun initialize(main: Thread, allocator: Allocator) {
		this.main = main
		this.allocator = allocator
	}

	override fun run() {
		check(Thread.currentThread() == main) { "Async" }
		executeRepeating()
		execute(if (tasks.isEmpty()) secondary else tasks, last, allocate)
	}

//	fun setTargetTPS(tps: Int) {
//		require(tps <= MAX_TPS) { "TPS cannot exceed $MAX_TPS" }
//		targetTPS = tps
//	}

	private fun executeRepeating() {
		val iterator: MutableIterator<Map.Entry<UUID, SyncTask<*>>> = repeating.entries.iterator()
		var entry: Map.Entry<UUID, SyncTask<*>>
		while (iterator.hasNext()) {
			entry = iterator.next()
			val task = entry.value
			if (task.runs >= task.repeat && task.repeat != -1) {
				iterator.remove() //cancel the task concurrently
			} else {
				task.increment() //increment runs and execute
				task.rawFuture.get()
			}
		}
	}

	private fun execute(deque: Deque<SyncTask<*>>, start: Long, allocate: Long) {
		var task: SyncTask<*>?
		synchronized(deque) {
			do {
				task = deque.poll()

				if (System.currentTimeMillis() >= task?.`when`!!) {
					task!!.future.run()
				}

			} while (System.currentTimeMillis() - start <= allocate && deque.size >= 1)
		}
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param supplier the task to run
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	override fun <T> supply(supplier: Supplier<T?>): Future<T?> = supply(supplier, 100)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	override fun <T> supply(supplier: Supplier<T?>, delay: Long): Future<T?> = supply(supplier, delay, QueuePriority.NORMAL)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param supplier the task to run
	 * @param delay    the delay in milliseconds
	 * @param priority the [QueuePriority] for the task
	 *
	 * @return the [Future] representation of the [Supplier]
	 */
	@SneakyThrows
	fun <T> supply(supplier: Supplier<T?>, delay: Long, priority: QueuePriority): Future<T?> {
		val task = SyncTask(supplier, System.currentTimeMillis() + delay)
		if (Thread.currentThread() == main) {
			return task.future
		}
		when (priority) {
			QueuePriority.IMMEDIATE -> synchronized(tasks) { tasks.offerFirst(task) }
			QueuePriority.NORMAL    -> synchronized(tasks) { tasks.offer(task) }
			QueuePriority.SECONDARY -> synchronized(secondary) { secondary.offer(task) }
		}
		return task.future
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param callable the task to run
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	override fun <T> call(callable: Callable<T?>): Future<T?> = call(callable, 100)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	override fun <T> call(callable: Callable<T?>, delay: Long): Future<T?> = call(callable, delay, QueuePriority.NORMAL)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param callable the task to run
	 * @param delay    the delay in milliseconds
	 * @param priority the [QueuePriority] for the task
	 *
	 * @return the [Future] representation of the [Callable]
	 */
	fun <T> call(callable: Callable<T?>, delay: Long, priority: QueuePriority): Future<T?> {
		val task = SyncTask({
								try {
									callable.call()
								} catch (e: Exception) {
									throw UnsupportedOperationException("Exception in callable.", e)
								}
							}, System.currentTimeMillis() + delay)
		if (Thread.currentThread() == main) {
			return task.future
		}
		when (priority) {
			QueuePriority.IMMEDIATE -> synchronized(tasks) { tasks.offerFirst(task) }
			QueuePriority.NORMAL    -> synchronized(tasks) { tasks.offer(task) }
			QueuePriority.SECONDARY -> synchronized(secondary) { secondary.offer(task) }
		}
		return task.future
	}

	/**
	 * Runs the task `100` milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	override fun <T> run(runnable: Runnable, value: T?): Future<T?> = run(runnable, value, 100)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the amount of milliseconds to wait before running the task
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	override fun <T> run(runnable: Runnable, value: T?, delay: Long): Future<T?> = run(runnable, value, delay, QueuePriority.NORMAL)

	/**
	 * Runs the task given amount of milliseconds later.
	 *
	 * @param runnable the task to run
	 * @param value    the value
	 * @param delay    the delay in milliseconds
	 * @param priority the [QueuePriority] for the task
	 *
	 * @return the [Future] representation of the [Runnable]
	 */
	fun <T> run(runnable: Runnable, value: T, delay: Long, priority: QueuePriority): Future<T?> {
		val task = SyncTask({
								runnable.run()
								value
							}, System.currentTimeMillis() + delay)
		if (Thread.currentThread() == main) {
			return task.future
		}
		when (priority) {
			QueuePriority.IMMEDIATE -> synchronized(tasks) { tasks.offerFirst(task) }
			QueuePriority.NORMAL    -> synchronized(tasks) { tasks.offer(task) }
			QueuePriority.SECONDARY -> synchronized(secondary) { secondary.offer(task) }
		}
		return task.future
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
	override fun repeat(consumer: Consumer<UUID>, interval: Long): UUID = repeat(consumer, interval, -1)


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
		val id = UUID.randomUUID()
		val supplier: Supplier<Any?> = Supplier {
			consumer.accept(id)
			null
		}
		synchronized(repeating) { repeating.put(id, SyncTask(supplier, count)) }
		return id
	}

	/**
	 * Cancel a repeating task.
	 *
	 * @param id the id of the task, obtained from [scheduling a repeating task][FutureHandler.repeat]
	 *
	 * @see FutureHandler.repeat
	 */
	override fun cancel(id: UUID) {
		synchronized(repeating) { repeating.remove(id) }
	}
}