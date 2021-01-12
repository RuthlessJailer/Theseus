package com.ruthlessjailer.api.theseus.task.delete.handler;

/**
 * @author RuthlessJailer
 */
public enum QueuePriority {

	/**
	 * Gets added to the start of the queue.<p>
	 * Highest priority.
	 */
	IMMEDIATE,
	/**
	 * Gets added to the end of the queue.<p>
	 * Standard priority.
	 */
	NORMAL,
	/**
	 * Gets added to the secondary queue.<p>
	 * Lowest priority.
	 */
	SECONDARY

}
