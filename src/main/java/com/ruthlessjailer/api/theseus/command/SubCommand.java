package com.ruthlessjailer.api.theseus.command;

import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * See wiki for full documentation: https://github.com/RuthlessJailer/Theseus
 * <p>
 * Annotation that enables automatic sub command parsing.
 * <p>
 * Available variables: <p>
 * %s - String <p>
 * %e - Enum (provide class) <p>
 * %i - Integer <p>
 * %d - Double <p>
 * %b - Boolean <p>
 * %p - Player <p>
 * Any arguments provided must be present in the method's parameters, but only enum classes must be provided in
 * annotation parameters (argTypes).
 * <p>
 * Code will be run regardless of trailing arguments.
 * <p>
 * The {@code |} character can be used to show multiple possibilities.
 * <p>
 * Example usage:
 * <p>
 * <pre>{@code
 * public class TestCommand extends CommandBase implements SuperiorCommand {
 *
 * 	public TestCommand() {
 * 		super("test");
 *        }
 *
 *    @Override
 *    protected void runCommand() {
 * 		//this code will be before and regardless of sub commands
 *    }
 *
 *    @SubCommand(inputArgs = "create|new %s %e", argTypes = Material.class)
 * 	private void create(final String name, final Material material) {
 * 		//this code will be run only when the sent command matches the given argument syntax (inputArgs)
 * 		//e.g.:
 * 		//test delete 4 air -> will not trigger
 * 		//test new Name stone -> will trigger
 * 		//test create 1 grass_block false -> will trigger
 *    }
 *
 * }
 * }</pre>
 *
 * @author Vadim Hagedorn
 * @see CommandBase
 * @see SuperiorCommand
 */

@NonNull
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

	String inputArgs();

	Class<?>[] argTypes();

}
