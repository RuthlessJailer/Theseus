package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubCommandManager {

	@Getter
	private static final SubCommandManager       instance    = new SubCommandManager();
	@Getter
	private final        List<CommandBase>       commands    = new ArrayList<>();
	@Getter
	private final        List<SubCommandWrapper> subCommands = new ArrayList<>();

	public static void register(final SuperiorCommand command) {
		Checks.verify(command instanceof CommandBase,
					  "SuperiorCommand implementations must extend CommandBase.",
					  SubCommandException.class);

		assert command instanceof CommandBase;

		getInstance().commands.add((CommandBase) command);


		SubCommandWrapper wrapper;

		for (final Method method : command.getClass().getDeclaredMethods()) {
			for (final Annotation annotation : method.getDeclaredAnnotations()) {
				if (annotation.annotationType().equals(SubCommand.class)) {
					final SubCommand subCommand = (SubCommand) annotation;

					wrapper = getInstance().parseArgs((CommandBase) command, method, subCommand);

					getInstance().subCommands.add(wrapper);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> SubCommandWrapper parseArgs(final CommandBase parent, final Method method,
															final SubCommand subCommand) {
		final String[] split = Checks.stringCheck(subCommand.inputArgs(),
												  String.format("InutArgs on method %s in class %s cannot be null " +
																"(or empty)!",
																method.getName(),
																method.getClass().getPackage().getName()))
									 .split(" ");
		final String     label     = split[0];
		final String[]   args      = split.length == 1 ? new String[0] : Common.copyToEnd(split, 1);
		final Class<?>[] types     = subCommand.argTypes();
		final Argument[] arguments = new Argument[args.length - 1];

		int i = 0;
		int j = 0;

		for (final String arg : args) {
			if (arg.matches("%s|d|i|e|b")) {
				switch (arg.toLowerCase()) {
					case "%s":
						Checks.verify(types[i].equals(String.class),
									  String.format("ArgType %s does not match InputArg %s in method %s in class %s.",
													types[i].getPackage().getName(),
													args[i].toLowerCase(),
													method.getName(),
													ReflectUtil.getPackage(method.getClass())),
									  SubCommandException.class);

						arguments[j] = new Argument(String.class);

						break;
					case "%i":
					case "%d":
						Checks.verify(types[i].equals(Double.class) || types[i].equals(Integer.class),
									  String.format("ArgType %s does not match InputArg %s in method %s in class %s.",
													ReflectUtil.getPackage(types[i]),
													args[i].toLowerCase(),
													method.getName(),
													method.getClass().getPackage().getName()),
									  SubCommandException.class);

						arguments[j] = new Argument(Double.class);

						break;
					case "%e":

						Checks.verify(types[i].isEnum(),
									  String.format("ArgType %s does not match InputArg %s in method %s in class %s.",
													ReflectUtil.getPackage(types[i]),
													args[i].toLowerCase(),
													method.getName(),
													method.getClass().getPackage().getName()),
									  SubCommandException.class);

						arguments[j] = new Argument(this.getEnumValues((Class<T>) types[i]), (Class<T>) types[i]);

						break;

					case "%b":

						Checks.verify(types[i].equals(Boolean.class),
									  String.format("ArgType %s does not match InputArg %s in method %s in class %s.",
													ReflectUtil.getPackage(types[i]),
													args[i].toLowerCase(),
													method.getName(),
													method.getClass().getPackage().getName()),
									  SubCommandException.class);

						arguments[j] =
								new Argument(Common.asArray("yes", "no", "y", "n", "true", "false"), Boolean.class);

						break;
				}
				i++;
			}
			j++;
		}

		Checks.verify(i == types.length,
					  String.format("ArgTypes do not match InputArgs in method %s in class %s.",
									method.getName(),
									ReflectUtil.getPackage(method.getClass())),
					  SubCommandException.class);


		return new SubCommandWrapper(parent,
									 arguments,
									 method);
	}

	private <E extends Enum<E>> String[] getEnumValues(final Class<E> clazz) {
		final E[] values = ReflectUtil.getEnumValues(clazz);
		return Common.convert(values, new String[values.length], Enum::name);
	}

}
