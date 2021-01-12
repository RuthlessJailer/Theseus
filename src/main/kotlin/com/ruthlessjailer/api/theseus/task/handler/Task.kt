package com.ruthlessjailer.api.theseus.task.handler

/**
 * @author RuthlessJailer
 */
abstract class Task(val repeat: Int = -1) {
	@Volatile
	var runs = 0
		private set

	@Synchronized
	fun increment() = runs++
}