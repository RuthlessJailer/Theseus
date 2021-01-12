package com.ruthlessjailer.api.theseus.command

/**
 * @author RuthlessJailer
 */
interface Permission<T> {

	fun get(): T
	fun has(sender: Sender<*>): Boolean
	
}