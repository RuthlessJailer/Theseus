package com.ruthlessjailer.api.theseus.command

/**
 * @author RuthlessJailer
 */
interface Command {

	fun getLabel(): String
	fun getPrefix(): String

	fun canExecute(sender: Sender<*>): Boolean
	fun canTabComplete(sender: Sender<*>): Boolean

	fun execute(sender: Sender<*>, prefix: String, label: String, args: Array<String>)
	fun tabComplete(sender: Sender<*>, prefix: String, label: String, args: Array<String>): List<String>

	fun register()
	fun unregister()

}