package com.ruthlessjailer.api.theseus.typeadapter;

import lombok.NonNull;
import org.apache.commons.lang.ClassUtils;

/**
 * @author RuthlessJailer
 */
public abstract class TypeAdapter<I, O> {

	public TypeAdapter(@NonNull final Class<O> out) {
		this.register(out);
	}

	@SuppressWarnings("unchecked")
	private void register(@NonNull final Class<O> out) {
		TypeAdapterRegistry.register((Class<O>) ClassUtils.primitiveToWrapper(out), this);
	}

	/**
	 * Attempts to convert a given object to another.
	 *
	 * @param in the object to convert
	 *
	 * @return the converted value or null
	 */
	public final O convert(final I in) {
		if (in == null) {
			return null;
		}

		try {
			return this.onConvert(in);
		} catch (final Throwable t) {
			return null;
		}
	}

	protected abstract O onConvert(@NonNull final I in);

}
