package com.ruthlessjailer.api.theseus.task.handler

import java.util.concurrent.FutureTask
import java.util.function.Supplier

/**
 * @author RuthlessJailer
 */
class SyncTask<T> : Task {
	constructor(rawFuture: Supplier<T?>, `when`: Long) {
		future = FutureTask { rawFuture.get() }
		this.`when` = `when`
		this.rawFuture = rawFuture
	}

	constructor(rawFuture: Supplier<T?>, repeat: Int = -1) : super(repeat) {
		future = FutureTask { rawFuture.get() }
		this.rawFuture = rawFuture
		`when` = 0
	}

	val future: FutureTask<T?>
	val rawFuture: Supplier<T?>
	val `when`: Long
}