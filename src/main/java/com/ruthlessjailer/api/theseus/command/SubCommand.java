package com.ruthlessjailer.api.theseus.command;

import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation enables automatic sub command parsing.
 * <p>
 * Available variables: <p>
 * %s - String <p>
 * %e - Enum (provide class) <p>
 * %i - Integer (will be converted to double) <p>
 * %d - Double <p>
 * %b - Boolean <p>
 * Any arguments provided must be present in the method's parameters.
 * <p>
 * The {@code |} character can be used to show multiple possibilities.
 * <p>
 * Example usage:
 * <p>
 * <pre>{@code
 *    @SubCommand("label create|new %s")
 * 	public void create(final String name){
 * 		//code
 *    }
 * }</pre>
 */

@NonNull
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

	String inputArgs();

	Class<?>[] argTypes();

}
