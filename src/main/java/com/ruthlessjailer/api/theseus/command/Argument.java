package com.ruthlessjailer.api.theseus.command;

import lombok.Getter;

@Getter
public final class Argument {

	private final boolean  infinite;
	private final String[] possibilities;
	private final Class<?> type;

	public Argument(final Class<?> type) {
		this(null, type);
	}

	public Argument(final String[] possibilities, final Class<?> type) {
		this.possibilities = possibilities;
		this.type          = type;
		this.infinite      = possibilities == null
							 && (type.equals(String.class)
								 || type.equals(Integer.class)
								 || type.equals(Double.class));
	}
}
