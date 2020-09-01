package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author Vadim Hagedorn
 */
public class TypeAdapterShort<I> extends TypeAdapter<I, Short> {

	public TypeAdapterShort() {
		super(Short.class);
	}

	@Override
	protected Short onConvert(@NonNull final I in) {
		if (in instanceof String) {//in case it has decimal points
			return TypeAdapterRegistry.get(Double.class).convert(in).shortValue();
		}

		if (in instanceof Number) {
			return ((Number) in).shortValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Short.parseShort(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? (short) 1 : (short) 0;
		}

		//it matches nothing, return null

		return null;
	}
}
