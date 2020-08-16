package com.ruthlessjailer.api.theseus;

import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectUtil {

	public static final String NMS = "net.minecraft.server";
	public static final String CRAFTBUKKIT = "org.bukkit.craftbukkit";

	/**
	 * Wrapper for {@link Class#forName(String)}
	 *
	 * @param pkg the full path to the class
	 *
	 * @return the found class
	 * @throws ReflectionException if the class is not found
	 * */
	public static Class<?> getClass(final String pkg){
		try {
			return Class.forName(pkg);
		}catch (final ClassNotFoundException e) {
			throw new ReflectionException(String.format("Class %s not found.", pkg));
		}
	}

	/**
	 * Wrapper for {@link Class#forName(String)} but adds {@value CRAFTBUKKIT} to the beginning
	 *
	 * @param pkg the path to the class
	 *
	 * @return the found class
	 * @throws ReflectionException if the class is not found
	 * */
	public static Class<?> getCraftBukkitClass(final String pkg){
		return getClass(CRAFTBUKKIT + pkg);
	}

	/**
	 * Wrapper for {@link Class#forName(String)} but adds {@value NMS} and {@link MinecraftVersion#CURRENT_VERSION} to
	 * the beginning
	 *
	 * @param pkg the path to the class
	 *
	 * @return the found class
	 * @throws ReflectionException if the class is not found
	 * */
	public static Class<?> getNMSClass(final String pkg){
		return getClass(NMS + "." + MinecraftVersion.SERVER_VERSION + "." + pkg);
	}

	/**
	 * Wrapper for {@link Enum#valueOf(Class, String)}
	 *
	 * @param enumType the enum to search
	 * @param name the name of the constant. Spaces will be replaced with underscores and it will be upper cased
	 *
	 * @return the found enum value or {@code null}
	 * */
	public static <E extends Enum<E>> E getEnumSuppressed(@NonNull final Class<E> enumType, @NonNull final String name){
		try{
			return Enum.valueOf(enumType, name.toUpperCase().replaceAll(" ","_"));
		}catch (final IllegalArgumentException e){
			return null;
		}
	}

	/**
	 * Wrapper for {@link Enum#valueOf(Class, String)}, but will attempt to correct the name.
	 * In attempt to correct the name, it will be tried plural, singular, and without underscores.
	 *
	 * @param enumType the enum to search
	 * @param name the name of the constant. Spaces will be replaced with underscores and it will be upper cased
	 *
	 * @return the found enum value or {@code null}
	 * @throws IllegalArgumentException if the value is not found
	 * */
	public static <E extends Enum<E>> E getEnum(@NonNull final Class<E> enumType, @NonNull final String name){
		String parsed = name.toUpperCase().replaceAll(" ", "_");

		if(MinecraftVersion.atLeast(MinecraftVersion.v1_13)){
			if (enumType == Material.class) {
				if(parsed.equals("RAW_FISH")){
					parsed = "SALMON";
				}else if(parsed.equals("MONSTER_EGG")){
					parsed = "ZOMBIE_SPAWN_EGG";
				}
			}else if(enumType == Biome.class){
				if(parsed.equals("ICE_MOUNTAINS")){
					parsed = "SNOWY_TAIGA";
				}
			}
		}

		E result = getEnumSuppressed(enumType, parsed);

		if(result == null){//try without underscores
			result = getEnumSuppressed(enumType, parsed.replaceAll("_", ""));
		}

		if(result == null){//try singular
			result = getEnumSuppressed(enumType, parsed + (parsed.endsWith("S") ? parsed.substring(0, parsed.length() - 1) : ""));
		}

		if(result == null){//try plural
			result = getEnumSuppressed(enumType, parsed + (parsed.endsWith("S") ? "ES" : "S"));
		}

		if(result == null){//throw error
			throw new IllegalArgumentException(String.format("Constant %s not found in enum %s! Available values: %s.",
															 parsed,
															 enumType,
															 StringUtils.join(enumType.getEnumConstants(), ", ")));
		}

		return result;
	}

	/**
	 * Wrapper for {@link Enum#valueOf(Class, String)}, but will try multiple names.
	 *
	 * @param enumType the enum to search
	 * @param name the name of the constant. Spaces will be replaced with underscores and it will be upper cased
	 * @param legacyNames the others names to try
	 *
	 * @return the found enum value or {@code null}
	 * @throws IllegalArgumentException if the value is not found
	 * */
	@NonNull
	public static <E extends Enum<E>> E getEnum(@NonNull final Class<E> enumType, @NonNull final String name,
												@NonNull final String... legacyNames){
		final String[] allNames = new String[legacyNames.length];
		allNames[0] = name;

		E result = getEnumSuppressed(enumType, name);

		if(result != null){
			return result;
		}

		int i = 0;

		for(final String legacyName : legacyNames){
			i++;
			allNames[i] = legacyName;

			result = getEnumSuppressed(enumType,legacyName);

			if(result != null){
				return result;
			}
		}

		throw new IllegalArgumentException(String.format("Constants %s not found in enum %s! Available values: %s.",
														 StringUtils.join(allNames, ", "),
														 enumType,
														 StringUtils.join(enumType.getEnumConstants(), ", ")));
	}

	/**
	 * Wrapper for {@link Class#getDeclaredField(String)}
	 *
	 * @param pkg the full path to the class
	 * @param name the name of the field
	 *
	 * @return the found {@link Field}
	 * @throws ReflectionException if the field is not found
	 * */
	public static Field getField(@NonNull final String pkg, @NonNull final String name){
		return getField(getClass(pkg), name);
	}

	/**
	 * Wrapper for {@link Class#getDeclaredField(String)}
	 *
	 * @param clazz the class to search
	 * @param name the name of the field
	 *
	 * @return the found {@link Field}
	 * @throws ReflectionException if the field is not found
	 * */
	public static Field getField(@NonNull final Class<?> clazz, @NonNull final String name){
		Field field;
		try{
			field = clazz.getDeclaredField(name);
		} catch (final NoSuchFieldException e) {
			try {
				field = clazz.getField(name);
			} catch (final NoSuchFieldException x) {
				throw new ReflectionException(String.format("Field %s in class %s not found.",name,
															clazz.getPackage().getName()));
			}
		}

		return field;
	}

	/**
	 * Wrapper for {@link Field#set(Object, Object)}
	 *
	 * @param pkg the full path to the class
	 * @param name the name of the field
	 * @param instance the instance in which to set the field or {@code null} for static fields
	 * @param value the value to set
	 *
	 * @throws ReflectionException if the field is not found or cannot be modified
	 * */
	public static <T> void setField(@NonNull final String pkg, @NonNull final String name, final Object instance,
									@NonNull final T value){
		setField(getField(pkg,name),instance,value);
	}

	/**
	 * Wrapper for {@link Field#set(Object, Object)}
	 *
	 * @param clazz the class to search
	 * @param name the name of the field
	 * @param instance the instance in which to set the field or {@code null} for static fields
	 * @param value the value to set
	 *
	 * @throws ReflectionException if the field is not found or cannot be modified
	 * */
	public static <T> void setField(@NonNull final Class<?> clazz, @NonNull final String name, final Object instance,
									@NonNull final T value){
		setField(getField(clazz,name),instance,value);
	}

	/**
	 * Wrapper for {@link Field#set(Object, Object)}
	 *
	 * @param field the field to set
	 * @param instance the instance in which to set the field or {@code null} for static fields
	 * @param value the value to set
	 *
	 * @throws ReflectionException if the field cannot be modified
	 * */
	public static <T> void setField(@NonNull final Field field, final Object instance, @NonNull final T value){
		try{
			field.setAccessible(true);
			field.set(instance, value);
		} catch (final IllegalAccessException e) {
			throw new ReflectionException(String.format("Error setting field %s in class %s.", field.getName(),
														field.getClass().getPackage().getName()), e);
		}
	}

	/**
	 * Wrapper for {@link Field#get(Object)}
	 *
	 * @param pkg the full path to the class
	 * @param name the name of the field
	 * @param instance the instance to get the field from or {@code null} for static fields
	 *
	 * @throws ReflectionException if the field is not found or cannot be accessed
	 * */
	public static Object getFieldValue(@NonNull final String pkg, @NonNull final String name, final Object instance){
		return getFieldValue(ReflectUtil.getClass(pkg),name,instance);
	}

	/**
	 * Wrapper for {@link Field#get(Object)}
	 *
	 * @param clazz the class to search
	 * @param name the name of the field
	 * @param instance the instance to get the field from or {@code null} for static fields
	 *
	 * @return the value of the field
	 * @throws ReflectionException if the field is not found or cannot be accessed
	 * */
	public static Object getFieldValue(@NonNull final Class<?> clazz, @NonNull final String name,
									   final Object instance){
		return getFieldValue(getField(clazz,name), instance);
	}

	/**
	 * Wrapper for {@link Field#get(Object)}
	 *
	 * @param field the field to get the value from
	 * @param instance the instance to get the field from or {@code null} for static fields
	 *
	 * @return the value of the field
	 * @throws ReflectionException if the field cannot be accessed
	 * */
	public static Object getFieldValue(@NonNull final Field field, final Object instance){
		try{
			field.setAccessible(true);
			return field.get(instance);
		} catch (final IllegalAccessException e) {
			throw new ReflectionException(String.format("Error getting field %s in class %s.", field.getName(),
														field.getClass().getPackage().getName()), e);
		}
	}

	/**
	 * Wrapper for {@link Class#getDeclaredMethod(String, Class[])}
	 *
	 * @param pkg the full path to the class
	 * @param name the name of the field
	 *
	 * @return the found {@link Method}
	 * @throws ReflectionException if the method is not found
	 * */
	public static Method getMethod(@NonNull final String pkg, @NonNull final String name){
		return getMethod(getClass(pkg),name);
	}

	/**
	 * Wrapper for {@link Class#getDeclaredMethod(String, Class[])}
	 *
	 * @param clazz the class to search
	 * @param name the name of the field
	 *
	 * @return the found {@link Method}
	 * @throws ReflectionException if the method is not found
	 * */
	public static Method getMethod(@NonNull final Class<?> clazz, @NonNull final String name){
		Method method;
		try{
			method = clazz.getDeclaredMethod(name);
		} catch (final NoSuchMethodException e) {
			try {
				method = clazz.getMethod(name);
			} catch (final NoSuchMethodException x) {
				throw new ReflectionException(String.format("Method %s in class %s not found.",name,
															clazz.getPackage().getName()));
			}
		}

		return method;
	}

	/**
	 * Wrapper for {@link Method#invoke(Object, Object...)}
	 *
	 * @param pkg the full path to the class
	 * @param name the name of the method
	 * @param instance the instance in which to invoke the method or {@code null} for static methods
	 * @param parameters the parameters to invoke the method with
	 *
	 *
	 * @return the return of the invoked method
	 * @throws ReflectionException if the method is not found, cannot be invoked, or an exception occurs during the
	 * invocation of the method
	 * */
	public static Object invokeMethod(@NonNull final String pkg, @NonNull final String name, final Object instance,
									  final Object... parameters){
		return invokeMethod(getClass(pkg), name, instance, parameters);
	}

	/**
	 * Wrapper for {@link Method#invoke(Object, Object...)}
	 *
	 * @param clazz the class to search
	 * @param name the name of the method
	 * @param instance the instance in which to invoke the method or {@code null} for static methods
	 * @param parameters the parameters to invoke the method with
	 *
	 *
	 * @return the return of the invoked method
	 * @throws ReflectionException if the method is not found, cannot be invoked, or an exception occurs during the
	 * invocation of the method
	 * */
	public static Object invokeMethod(@NonNull final Class<?> clazz, @NonNull final String name,
									  final Object instance, final Object... parameters){
		return invokeMethod(getMethod(clazz, name), instance, parameters);
	}

	/**
	 * Wrapper for {@link Method#invoke(Object, Object...)}
	 *
	 * @param method the {@link Method} to invoke
	 * @param instance the instance in which to invoke the method or {@code null} for static methods
	 * @param parameters the parameters to invoke the method with
	 *
	 *
	 * @return the return of the invoked method
	 * @throws ReflectionException if the method cannot be invoked or an exception occurs during the
	 * invocation of the method
	 * */
	public static Object invokeMethod(@NonNull final Method method, final Object instance,
									  final Object... parameters){
		try{
			method.setAccessible(true);
			return method.invoke(instance, parameters);
		}  catch (final IllegalAccessException | InvocationTargetException e) {
			throw new ReflectionException(String.format("Error invoking method %s in class %s.", method.getName(),
														method.getClass().getPackage().getName()), e);
		}
	}

	/**
	 * Generic exception for dealing with reflection.
	 */
	public static final class ReflectionException extends RuntimeException{
		private static final long serialVersionUID = 6172883405365570521L;

		public ReflectionException(final String message) {
			super(message);
		}

		public ReflectionException(final String message, final Throwable cause){
			super(message, cause);
		}

		public ReflectionException(final Throwable cause){
			super(cause);
		}

		public ReflectionException(final String message, final Exception exception) {
			super(message, exception);
		}

	}

}
