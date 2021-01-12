package com.ruthlessjailer.api.theseus.delete.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.OfflinePlayer;


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

	public Argument(final String[] possibilities, @NonNull final Class<?> type, final boolean declaredType, final String description) {
		this.possibilities = possibilities;
		this.type          = type;
		this.declaredType  = declaredType;
		this.description   = description == null && declaredType
							 ? String.class.isAssignableFrom(type)
							   ? "Text"
							   : Number.class.isAssignableFrom(type)
								 ? "Number"
								 : Boolean.class.isAssignableFrom(type)
								   ? "Condition"
								   : OfflinePlayer.class.isAssignableFrom(type)
									 ? "Player"
									 : type.getSimpleName()
							 : description;
		this.infinite      = possibilities == null
							 && (type.equals(String.class)
								 || type.equals(Integer.class)
								 || type.equals(Double.class));
	}

	protected void updatePossibilities(@NonNull final String[] newPossibilities) {//should only be used for updating player names
		this.possibilities = newPossibilities;
	}

}
