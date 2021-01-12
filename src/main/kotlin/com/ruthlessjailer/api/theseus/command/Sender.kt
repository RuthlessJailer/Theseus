package com.ruthlessjailer.api.theseus.command

/**
 * @author RuthlessJailer
 */
interface Sender<T> {

	fun get(): T

	fun send(vararg message: String)
	fun sendf(format: String, vararg objects: Any)

}