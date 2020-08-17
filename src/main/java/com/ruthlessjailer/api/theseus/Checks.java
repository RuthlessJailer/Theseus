package com.ruthlessjailer.api.theseus;

import java.lang.reflect.InvocationTargetException;

public final class Checks {

	public static <T> T nullCheck(final T object, final String falseMessage) {
		if (object == null) {
			throw new NullPointerException(falseMessage);
		}
		return object;
	}

	public static void verify(final boolean condition, final String falseMessage) {
		Checks.verify(condition, falseMessage, RuntimeException.class);
	}

	public static void verify(final boolean condition, final String falseMessage, final Class<? extends RuntimeException> exception) {
		if (!condition) {
			try {
				throw exception.getConstructor(String.class).newInstance(falseMessage);
			} catch (final InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				throw new IllegalArgumentException("Exception " + exception + " not valid. Perhaps it is missing a " +
												   "constructor?");
			}
		}
	}

}
