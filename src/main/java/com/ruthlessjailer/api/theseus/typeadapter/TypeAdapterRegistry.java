package com.ruthlessjailer.api.theseus.typeadapter;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.typeadapter.impl.*;
import lombok.NonNull;
import org.apache.commons.lang.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vadim Hagedorn
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class TypeAdapterRegistry<I, O> {

	private static final TypeAdapterRegistry registry = new TypeAdapterRegistry();

	static {//instantiate default type adapters
		new TypeAdapterByte<>();
		new TypeAdapterShort<>();
		new TypeAdapterInteger<>();
		new TypeAdapterLong<>();
		new TypeAdapterFloat<>();
		new TypeAdapterDouble<>();
	}

	private final Map<Class<O>, TypeAdapter<I, O>> adapters = new HashMap<>();

	public static <I, O> void register(@NonNull final Class<O> output, @NonNull final TypeAdapter<I, O> adapter) {
		registry.adapters.put(ClassUtils.primitiveToWrapper(output), adapter);
	}

	public static <I, O> TypeAdapter<I, O> get(@NonNull final Class<O> output) {
		Checks.verify(registry.adapters.containsKey(ClassUtils.primitiveToWrapper(output)),
					  String.format("No TypeAdapter with output class %s found.",
									ReflectUtil.getPath(ClassUtils.primitiveToWrapper(output))),
					  IllegalArgumentException.class);
		return (TypeAdapter<I, O>) registry.adapters.get(ClassUtils.primitiveToWrapper(output));
	}

}
