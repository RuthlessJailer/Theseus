package com.ruthlessjailer.api.theseus.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public final class Argument {

	private final boolean  infinite;
	private final String[] possibilities;
	private final Class<?> type;
	private final boolean  declaredType;
	private final String   description;

	public Argument(@NonNull final Class<?> type, @NonNull final Boolean declaredType, final String description) {
		this(null, type, declaredType, description);
	}

	public Argument(final String[] possibilities, @NonNull final Class<?> type,
					@NonNull final boolean declaredType, final String description) {
		this.possibilities = possibilities;
		this.type          = type;
		this.declaredType  = declaredType;
		this.description   = description == null && declaredType ? type.getSimpleName() : description;
		this.infinite      = possibilities == null
							 && (type.equals(String.class)
								 || type.equals(Integer.class)
								 || type.equals(Double.class));
	}

}
