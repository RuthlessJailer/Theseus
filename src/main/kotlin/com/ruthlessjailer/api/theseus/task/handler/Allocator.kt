package com.ruthlessjailer.api.theseus.task.handler

/**
 * @author RuthlessJailer
 */
fun interface Allocator {
	fun apply(last: Long, allocate: Long): Long
}