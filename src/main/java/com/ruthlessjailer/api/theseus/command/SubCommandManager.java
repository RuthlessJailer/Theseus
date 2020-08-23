package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

	public static List<String> tabCompleteFor(final CommandBase command, final CommandSender sender, final String[] args) {

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

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> void executeFor(final CommandBase command, final CommandSender sender, final String[] args) {

		wrappers:
		for (final SubCommandWrapper wrapper : getInstance().subCommands.get(command)) {

			final Class<?>[] declaredTypes = wrapper.getDeclaredTypes();

			final Object[] parameters = new Object[declaredTypes.length];

			int i = 0;//counter
			int p = 0;//parameter/declaredType counter

			if (args.length < wrapper.getArguments().length) {//check args
				System.out.println("args too short");
				break;
			}

			for (final Argument argument : wrapper.getArguments()) {//check constant arguments & parse variable placeholders
				boolean match = false;
				if (!argument.isInfinite()) {
					for (final String possibility : argument.getPossibilities()) {
						if (possibility.equalsIgnoreCase(args[i])) {
							System.out.println("match: " + args[i] + " == " +
											   (argument.getPossibilities().length > 50
												? "[..., " + possibility + ", ...]"
												: Arrays.toString(argument.getPossibilities())));
							match = true;
							break;
						}
					}
					if (!match) {
						System.out.println("no match: " + args[i] + " != " +
										   (argument.getPossibilities().length > 50
											? "[...]"
											: Arrays.toString(argument.getPossibilities())));
						continue wrappers;
					}
				}

				if (argument.isDeclaredType()) {
					final Class<?> declaredType = argument.getType();

					if (declaredType.isEnum()) {//get enum value
						parameters[p] = ReflectUtil.getEnum((Class<E>) declaredType, args[i]);
					} else {//Integer, Double, Boolean
						try {
							parameters[p] = ReflectUtil.invokeMethod(declaredType, "valueOf", null, args[i]);
						} catch (final ReflectUtil.ReflectionException e) {
							if (e.getCause() instanceof InvocationTargetException) {
								System.out.println("no match");
								continue wrappers;
							} else {
								try {
									parameters[p] = ReflectUtil.newInstanceOf(declaredType, args[i]);
								} catch (final ReflectUtil.ReflectionException x) { //try constructor (string doesn't work with valueOf for some reason)
									if (e.getCause() instanceof InvocationTargetException) {
										System.out.println("no match");
										continue wrappers;
									} else {
										e.printStackTrace();
										continue wrappers;
									}
								}
							}
						}
						p++;
					}
				}
				i++;
			}

			i = 0;

			/*for (final Class<?> declaredType : declaredTypes) {//extract variable arguments

				if (declaredType.isEnum()) {//get enum value
					parameters[i] = ReflectUtil.getEnum((Class<E>) declaredType, args[i]);
				} /*else if (declaredType.equals(String.class)) {//special case for string
					parameters[i] = args[i];
				}/ else {//Integer, Double, Boolean
					try {
						System.out.println(declaredType);
						System.out.println(Arrays.toString(declaredType.getDeclaredMethods()));
						parameters[i] = ReflectUtil.invokeMethod(declaredType, "valueOf", null, args[i]);
					} catch (final ReflectUtil.ReflectionException e) {
						if (e.getCause() instanceof InvocationTargetException) {
							System.out.println("no match");
							continue wrappers;
						} else {
							try {
								parameters[i] = ReflectUtil.newInstanceOf(declaredType, args[i]);
							} catch (final ReflectUtil.ReflectionException x) {
								//try constructor (string doesn't work with valueOf for some reason)
								if (e.getCause() instanceof InvocationTargetException) {
									System.out.println("no match");
									continue wrappers;
								} else {
									e.printStackTrace();
									continue wrappers;
								}
							}
						}
					}
				}

				i++;
			}*/

			System.out.println("invoking");
			ReflectUtil.invokeMethod(wrapper.getMethod(), command, parameters);

		}

	}

	public List<SubCommandWrapper> getSubCommands(final CommandBase command) { return this.subCommands.get(command); }

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> SubCommandWrapper parseArgs(final CommandBase parent, final Method method,
															final SubCommand subCommand) {
		final String[] args = Checks.stringCheck(subCommand.inputArgs(),
												 String.format("InputArgs on method %s in class %s cannot be null " +
															   "(or empty)!",
															   method.getName(),
															   method.getClass().getPackage().getName()))
									.split(" ");
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
					arguments[i] = new Argument(null, String.class, true);

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

	private void checkMethod(final Class<?>[] declaredTypes, final Method method, final CommandBase parent) {

		final Class<?>[] methodParameterTypes = method.getParameterTypes();

		Checks.verify(methodParameterTypes.length == declaredTypes.length,
					  String.format("Parameters on method %s in class %s do not " +
									"match ArgTypes.",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		int i = 0;
		for (final Class<?> type : declaredTypes) {
			Checks.verify(type.equals(methodParameterTypes[i]),
						  String.format("Parameter %s on method %s in class %s does not " +
										"match ArgType %s.",
										methodParameterTypes[i].getName(),
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
