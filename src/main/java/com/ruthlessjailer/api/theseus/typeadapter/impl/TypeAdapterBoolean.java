package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author RuthlessJailer
 */
public class TypeAdapterBoolean<I> extends TypeAdapter<I, Boolean> {

	public TypeAdapterBoolean() {
		super(Boolean.class);
	}

	@Override
	protected Boolean onConvert(@NonNull final I in) {
		if (in instanceof String) {//parse string
			return Boolean.valueOf((String) in);
		}

		if (in instanceof Number) {//1 = true, 0 = false, all else = null
			final int converted = TypeAdapterRegistry.get(Double.class).convert(in).intValue();
			switch (converted) {
				case 1:
					return true;
				case 0:
					return false;
				default:
					return null;
			}
		}

		return null;//it matches nothing; return null
	}
}
