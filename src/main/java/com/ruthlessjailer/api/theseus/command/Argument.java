package com.ruthlessjailer.api.theseus.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;


/**
 * @author RuthlessJailer
 */
@Getter
@ToString
public final class Argument {

	private final boolean  infinite;
	private final Class<?> type;
	private final boolean  declaredType;
	private final String   description;
	private       String[] possibilities;//non-final because player names can change/players can leave and join

	public Argument(@NonNull final String[] possibilities, @NonNull final Class<?> type, @NonNull final Boolean declaredType) {
		this(possibilities, type, declaredType, null);
	}

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

	protected void updatePossibilities(@NonNull final String[] newPossibilities) {//should only be used for updating player names
		this.possibilities = newPossibilities;
	}

}
