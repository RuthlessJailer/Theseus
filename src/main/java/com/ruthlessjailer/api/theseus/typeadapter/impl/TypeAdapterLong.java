package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author Vadim Hagedorn
 */
public class TypeAdapterLong<I> extends TypeAdapter<I, Long> {

	public TypeAdapterLong() {
		super(Long.class);
	}

	@Override
	protected Long onConvert(@NonNull final I in) {
		if (in instanceof String) {//in case it has decimal points
			return TypeAdapterRegistry.get(Double.class).convert(in).longValue();
		}

		if (in instanceof Number) {
			return ((Number) in).longValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Long.parseLong(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? 1L : 0L;
		}

		//it matches nothing, return null

		return null;
	}
}
