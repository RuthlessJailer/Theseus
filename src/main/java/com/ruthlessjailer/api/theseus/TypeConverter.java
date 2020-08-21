package com.ruthlessjailer.api.theseus;

/**
 * Interface for converting types.
 *
 * @see Common#convert(Iterable, TypeConverter)
 * @see Common#convert(Object[], Object[], TypeConverter)
 */
public interface TypeConverter<O, N> {

	N convert(O value);

}
