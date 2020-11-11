package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.NonNull;

/**
 * @author RuthlessJailer
 */
public class TypeAdapterByte<I> extends TypeAdapter<I, Byte> {

	public TypeAdapterByte() {
		super(Byte.class);
	}

	@Override
	protected Byte onConvert(@NonNull final I in) {
		if (in instanceof String) {//in case it has decimal points
			return TypeAdapterRegistry.get(Double.class).convert(in).byteValue();
		}

		if (in instanceof Number) {
			return ((Number) in).byteValue();
		}

		//other primitive wrappers

		if (in instanceof Character) {
			return Byte.parseByte(String.valueOf((Character) in));
		}

		if (in instanceof Boolean) {
			return ((Boolean) in).booleanValue() ? (byte) 1 : (byte) 0;
		}

		//it matches nothing, return null

		return null;
	}
}
