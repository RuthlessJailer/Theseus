package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author RuthlessJailer
 */
public final class TypeAdapterInteger<I> extends TypeAdapter<I, Integer> {

	public TypeAdapterInteger() {
		super(Integer.class);
	}

	@Override
	public Integer onConvert(@NonNull final I in) {

		if (in instanceof String) {//in case it has decimal points
			return TypeAdapterRegistry.get(Double.class).convert(in).intValue();
		}

		if (in instanceof Number) {
			return ((Number) in).intValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Integer.parseInt(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? 1 : 0;
		}

		//it matches nothing, return null

		return null;
	}

}
