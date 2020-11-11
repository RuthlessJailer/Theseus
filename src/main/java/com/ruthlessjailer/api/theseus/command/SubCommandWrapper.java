package com.ruthlessjailer.api.theseus.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @author RuthlessJailer
 */
@AllArgsConstructor
@Getter
public final class SubCommandWrapper {

	private final CommandBase parent;
	private final Argument[]  arguments;
	private final Class<?>[]  types;//all arg types, including set strings
	private final Class<?>[]  declaredTypes;//types that show up in method (ie all variables)
	private final Method      method;

}
