package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubCommandManager {

	@Getter
	private static final SubCommandManager instance = new SubCommandManager();

	private final Map<CommandBase, List<SubCommandWrapper>> subCommands = new HashMap<>();

	public static void register(final SuperiorCommand command) {
		Checks.verify(command instanceof CommandBase,
					  "SuperiorCommand implementations must extend CommandBase.",
					  SubCommandException.class);

		assert command instanceof CommandBase;

		final List<SubCommandWrapper> wrappers = new ArrayList<>();

		for (final Method method : command.getClass().getDeclaredMethods()) {
			for (final Annotation annotation : method.getDeclaredAnnotations()) {
				if (annotation.annotationType().equals(SubCommand.class)) {
					final SubCommandWrapper wrapper = getInstance().parseArgs((CommandBase) command, method,
																			  (SubCommand) annotation);
					wrappers.add(wrapper);
					Chat.debug("[Commands]",
							   String.format("Registering method %s in class %s as a sub command.",
											 wrapper.getMethod().getName(),
											 ReflectUtil.getPath(command.getClass())));
				}
			}
		}

		getInstance().subCommands.put((CommandBase) command, wrappers);
	}

	public List<SubCommandWrapper> getSubCommands(final CommandBase command) { return this.subCommands.get(command); }

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
		final Class<?>[] argTypes  = subCommand.argTypes();
		final Argument[] arguments = new Argument[args.length];

		int t = 0;//type counter
		int i = 0;//counter
		int e = 0;//enum counter

		for (final String arg : args) {//initialize types variable
			if (arg.matches("%[sideb]")) {
				t++;
			}
		}

		final Class<?>[] types = new Class[t];

		t = 0;

		for (final String arg : args) {
			final Class<?> type = argTypes[e];

			switch (arg.toLowerCase()) {
				case "%s":

					types[t] = String.class;
					arguments[i] = new Argument(String.class);

					break;
				case "%e":

					Checks.verify(type.isEnum(),
								  String.format(
										  "ArgType %s does not match InputArg %s in method %s in class %s. Only include enums!",
										  ReflectUtil.getPath(type),
										  arg.toLowerCase(),
										  method.getName(),
										  ReflectUtil.getPath(parent.getClass())),
								  SubCommandException.class);

					types[t] = type;
					arguments[i] = new Argument(this.getEnumValueNames((Class<T>) type), (Class<T>) type);

					break;
				case "%i":
				case "%d":

					types[t] = Double.class;
					arguments[i] = new Argument(Double.class);

					break;
				case "%b":

					types[t] = Boolean.class;
					arguments[i] = new Argument(Common.asArray("true", "false"), Boolean.class);

					break;
				default:
					arguments[i] = new Argument(arg.split("\\|"), String.class);
			}

			if (arg.toLowerCase().matches("%[sideb]")) {
				t++;
			}

			if (arg.toLowerCase().matches("%e")) {
				e++;
			}

			i++;
		}

		Checks.verify(e == argTypes.length,
					  String.format("ArgTypes do not match InputArgs in method %s in class %s. Only include enums!",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		this.checkMethod(types, method, parent);

		return new SubCommandWrapper(parent,
									 arguments,
									 method);
	}

	private void checkMethod(final Class<?>[] types, final Method method, final CommandBase parent) {

		final Class<?>[] methodTypes = method.getParameterTypes();

		Checks.verify(methodTypes.length == types.length,
					  String.format("Parameters on method %s in class %s do not " +
									"match ArgTypes.",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		int i = 0;
		for (final Class<?> type : types) {
			Checks.verify(type.equals(methodTypes[i]),
						  String.format("Parameter %s on method %s in class %s does not " +
										"match ArgType %s.",
										methodTypes[i].getName(),
										method.getName(),
										ReflectUtil.getPath(parent.getClass()),
										type.getName()),
						  SubCommandException.class);
			i++;
		}
	}

	private <E extends Enum<E>> String[] getEnumValueNames(final Class<E> clazz) {
		final E[] values = ReflectUtil.getEnumValues(clazz);
		return Common.convert(values, new String[values.length], Enum::name);
	}
}
