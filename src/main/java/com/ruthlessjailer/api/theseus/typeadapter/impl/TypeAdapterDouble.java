package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import lombok.NonNull;

/**
 * @author RuthlessJailer
 */
public final class TypeAdapterDouble<I> extends TypeAdapter<I, Double> {
	public TypeAdapterDouble() {
		super(Double.class);
	}

	@Override
	protected Double onConvert(@NonNull final I in) {

		if (in instanceof String) {
			return Double.valueOf((String) in);
		}

		if (in instanceof Number) {
			return ((Number) in).doubleValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Double.parseDouble(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? 1D : 0D;
		}

		//it matches nothing, return null

		return null;
	}
}
