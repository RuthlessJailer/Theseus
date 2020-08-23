package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import javafx.util.Pair;
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
					Chat.debug("Commands",
							   String.format("Registering method %s in class %s as a sub command.",
											 wrapper.getMethod().getName(),
											 ReflectUtil.getPath(command.getClass())));
				}
			}
		}

		getInstance().subCommands.put((CommandBase) command, wrappers);
	}

	public static List<String> tabCompleteFor(final CommandBase command, final String[] args) {

//		for (final SubCommandWrapper wrapper : getInstance().subCommands.get(command)) {
//			for (final Argument argument : wrapper.getArguments()) {
//				for (final String arg : args) {
//					if (!argument.isInfinite()) {
//						for (final String possibility : argument.getPossibilities()) {
//							if (arg.equalsIgnoreCase(possibility)) {
//
//							}
//						}
//					} else {
//						System.out.println("inifinite_");
//					}
//				}
//			}
//		}

		return new ArrayList<>();
	}

	public static <E extends Enum<E>> void executeFor(final CommandBase command, final String[] args) {

		int i = 0;
		for (final SubCommandWrapper wrapper : getInstance().subCommands.get(command)) {

			final Class<?>[] types = wrapper.getTypes();

			final List<Pair<Object, Class<?>>> pairs = new ArrayList<>();

			for (final Class<?> type : types) {

//				if(type.equals(Double.class)){
//
//				}else if(type.equals(String.class)){
//
//				}else if(type.equals(Boolean.class)){
//
//				}else if(type.isEnum()){
//
//				}

				if (!type.isEnum()) {
					pairs.add(new Pair<>(ReflectUtil.invokeMethod(type, "valueOf", null, args[i]), type));
				} else {
					pairs.add(new Pair<>(ReflectUtil.getEnumSuppressed((Class<E>) type, args[i]), type));
				}

				i++;
			}


			ReflectUtil.invokeMethod(wrapper.getMethod(), null, "null");

			for (final Argument argument : wrapper.getArguments()) {


				final String arg = args[i];

				System.out.println(argument.getType());


			}


		}

	}

	public List<SubCommandWrapper> getSubCommands(final CommandBase command) { return this.subCommands.get(command); }

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> SubCommandWrapper parseArgs(final CommandBase parent, final Method method,
															final SubCommand subCommand) {
		final String[] split = Checks.stringCheck(subCommand.inputArgs(),
												  String.format("InputArgs on method %s in class %s cannot be null " +
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

		for (final String arg : args) {//initialize declaredTypes variable
			if (arg.matches("%[sideb]")) {
				t++;
			}
			i++;
		}

		final Class<?>[] types         = new Class[i];
		final Class<?>[] declaredTypes = new Class[t];

		t = 0;
		i = 0;

		for (final String arg : args) {
			final Class<?> declaredType = argTypes[e];

			switch (arg.toLowerCase()) {
				case "%s"://string

					types[i] = String.class;
					declaredTypes[t] = String.class;
					arguments[i] = new Argument(String.class);

					break;
				case "%e"://enum (class provided)

					Checks.verify(declaredType.isEnum(),
								  String.format(
										  "ArgType %s does not match InputArg %s in method %s in class %s. Only include enums!",
										  ReflectUtil.getPath(declaredType),
										  arg.toLowerCase(),
										  method.getName(),
										  ReflectUtil.getPath(parent.getClass())),
								  SubCommandException.class);

					types[i] = declaredType;
					declaredTypes[t] = declaredType;
					arguments[i] =
							new Argument(this.getEnumValueNames((Class<T>) declaredType), (Class<T>) declaredType,
										 true);

					break;
				case "%i"://integer
				case "%d"://double

					types[i] = Double.class;
					declaredTypes[t] = Double.class;
					arguments[i] = new Argument(Double.class);

					break;
				case "%b"://boolean

					types[i] = Boolean.class;
					declaredTypes[t] = Boolean.class;
					arguments[i] = new Argument(Common.asArray("true", "false"), Boolean.class, true);

					break;
				default:
					types[i] = String.class;
					arguments[i] = new Argument(arg.split("\\|"), String.class, false);
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

		this.checkMethod(declaredTypes, method, parent);

		return new SubCommandWrapper(parent,
									 arguments,
									 types,
									 declaredTypes,
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
