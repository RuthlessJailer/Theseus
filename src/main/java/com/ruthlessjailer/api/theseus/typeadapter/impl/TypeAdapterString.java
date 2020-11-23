package com.ruthlessjailer.api.theseus.typeadapter.impl;

import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapter;
import lombok.NonNull;

/**
 * @author RuthlessJailer
 */
public class TypeAdapterString<I> extends TypeAdapter<I, String> {

	public TypeAdapterString() {
		super(String.class);
	}

	@Override
	protected String onConvert(@NonNull final I in) {
		return String.valueOf(in);
	}
}
