package com.ruthlessjailer.api.theseus.task.delete.handler;

/**
 * @author RuthlessJailer
 */
@FunctionalInterface
public interface Allocator {
	long apply(long last, long allocate);
}
