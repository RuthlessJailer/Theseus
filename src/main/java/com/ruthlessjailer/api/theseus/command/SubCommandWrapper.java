package com.ruthlessjailer.api.theseus.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public final class SubCommandWrapper {

	private final CommandBase parent;
	private final Argument[]  arguments;
	private final Method      method;


}
