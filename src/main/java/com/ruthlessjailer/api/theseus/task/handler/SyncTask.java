package com.ruthlessjailer.api.theseus.task.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.FutureTask;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public final class SyncTask<T> {

	private final FutureTask<T> future;
	private final long          when;

}
