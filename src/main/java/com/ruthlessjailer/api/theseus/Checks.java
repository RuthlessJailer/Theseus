package com.ruthlessjailer.api.theseus;

import java.lang.reflect.InvocationTargetException;

public class Checks {

	public static void nullCheck(final Object object, final String falseMessage){
		if(object == null){
			throw new NullPointerException(falseMessage);
		}
	}

	public static void checkTrue(final boolean condition, final String falseMessage) {
		Checks.checkTrue(condition, falseMessage, RuntimeException.class);
	}

	public static void checkTrue(final boolean condition, final String falseMessage, final Class<? extends RuntimeException> exception) {
		if(!condition){
			try {
				throw exception.getConstructor(String.class).newInstance(falseMessage);
			} catch (final InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				throw new IllegalArgumentException("Exception "+exception+ " not valid. Perhaps it is missing a " +
												   "constructor?");
			}
		}
	}

}
