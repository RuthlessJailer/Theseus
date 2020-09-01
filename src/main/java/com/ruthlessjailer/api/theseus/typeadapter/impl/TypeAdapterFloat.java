package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author Vadim Hagedorn
 */
public class TypeAdapterFloat<I> extends TypeAdapter<I, Float> {

	public TypeAdapterFloat() {
		super(Float.class);
	}

	@Override
	protected Float onConvert(@NonNull final I in) {
		if (in instanceof String) {//in case it has decimal points
			return TypeAdapterRegistry.get(Double.class).convert(in).floatValue();
		}

		if (in instanceof Number) {
			return ((Number) in).floatValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Float.parseFloat(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? 1F : 0F;
		}

		//it matches nothing, return null

		return null;
	}
}
