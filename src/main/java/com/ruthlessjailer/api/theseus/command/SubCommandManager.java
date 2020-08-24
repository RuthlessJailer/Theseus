package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.command.help.HelpLine;
import com.ruthlessjailer.api.theseus.command.help.HelpMenu;
import com.ruthlessjailer.api.theseus.command.help.HelpMenuFormat;
import com.ruthlessjailer.api.theseus.command.help.HelpPage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
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

	private final Map<CommandBase, HelpMenu> helpMenus = new HashMap<>();

	public void register(@NonNull final SuperiorCommand command) {
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

		this.subCommands.put((CommandBase) command, wrappers);
	}

	public List<String> tabCompleteFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {

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

	public void sendHelpMenuTo(@NonNull final HelpMenu menu, @NonNull final CommandSender sender) {
		for (final HelpPage page : menu.getPages()) {
			for (final HelpLine line : page.getLines()) {
				sender.spigot().sendMessage(line.getFormatted());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> void executeFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {

		wrappers:
		for (final SubCommandWrapper wrapper : getInstance().subCommands.get(command)) {

			final Class<?>[] declaredTypes = wrapper.getDeclaredTypes();

			final Object[] parameters = new Object[declaredTypes.length];

			int i = 0;//counter
			int p = 0;//parameter/declaredType counter

			if (args.length < wrapper.getArguments().length) {//check args
				System.out.println("args too short");
				continue;
			}

			for (final Argument argument : wrapper.getArguments()) {//check constant arguments & parse variable placeholders

				System.out.println(argument.toString());

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

			System.out.println("invoking");

			ReflectUtil.invokeMethod(wrapper.getMethod(), command, parameters);

		}

	}

	public List<SubCommandWrapper> getSubCommands(@NonNull final CommandBase command) {
		return this.subCommands.containsKey(command)
			   ? this.subCommands.get(command)
			   : new ArrayList<>();
	}

	public HelpMenu getHelpMenu(@NonNull final CommandBase command) { return this.helpMenus.get(command); }

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> SubCommandWrapper parseArgs(@NonNull final CommandBase parent, @NonNull final Method method,
															@NonNull final SubCommand subCommand) {
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
			if (arg.toLowerCase().matches("%[sideb](<[a-z0-9_]+>)?")) {
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

			String description = null;

			if (arg.matches("%[sidebSIDEB]<[A-Za-z0-9_]+>")) {//description storage
				description = arg.substring(3, arg.length() - 1);
			}

			switch (arg.toLowerCase().substring(0, 2)) {//get variable, regardless of description
				case "%s"://string

					types[i] = String.class;
					declaredTypes[t] = String.class;
					arguments[i] = new Argument(String.class, true, description);

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
										 true, description);

					break;
				case "%i"://integer
					types[i] = Integer.class;
					declaredTypes[t] = Integer.class;
					arguments[i] = new Argument(Integer.class, true, description);

					break;
				case "%d"://double

					types[i] = Double.class;
					declaredTypes[t] = Double.class;
					arguments[i] = new Argument(Double.class, true, description);

					break;
				case "%b"://boolean

					types[i] = Boolean.class;
					declaredTypes[t] = Boolean.class;
					arguments[i] = new Argument(Common.asArray("true", "false"), Boolean.class, true, description);

					break;
				default:
					types[i] = String.class;
					arguments[i] = new Argument(arg.split("\\|"), String.class, false, null);
			}

			if (arg.toLowerCase().matches("%[sideb](<[a-z0-9_]+>)?")) {//type counter increment
				t++;
			}

			if (arg.toLowerCase().matches("%e(<[a-z0-9_]+>)?")) {//enum counter increment
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

	private void checkMethod(@NonNull final Class<?>[] declaredTypes, @NonNull final Method method, @NonNull final CommandBase parent) {

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

	private <E extends Enum<E>> String[] getEnumValueNames(@NonNull final Class<E> clazz) {
		final E[] values = ReflectUtil.getEnumValues(clazz);
		return Common.convert(values, new String[values.length], Enum::name);
	}

	public HelpMenu generateHelpMenu(@NonNull final CommandBase command, final HelpMenuFormat menuFormat) {

		final List<SubCommandWrapper> subCommands = this.getSubCommands(command);
		final HelpMenuFormat          format      = menuFormat == null ? HelpMenuFormat.DEFAULT_FORMAT : menuFormat;

		final int pageCount = (int) Math.ceil((double) subCommands.size() / (double) format.getPageSize());

		final HelpPage[] pages = new HelpPage[pageCount];

		int l = 0;//line counter
		int p = 0;//page counter

		final ComponentBuilder builder = new ComponentBuilder();

		final HelpLine[] lines = new HelpLine[format.getPageSize()];

		for (final SubCommandWrapper wrapper : subCommands) {

			final StringBuilder fullCommand = new StringBuilder(
					format.getCommand().replace(
							HelpMenuFormat.Placeholder.COMMAND,
							command.getLabel()));

			for (final Argument argument : wrapper.getArguments()) {

				final String append;

				if (argument.isDeclaredType()) {//TODO: formatting
					append = format.getVariable().replace(
							HelpMenuFormat.Placeholder.VARIABLE,
							argument.getDescription());
				} else {
					//	final String prefix = format.getVariable().substring(0, format.getVariable().indexOf(variable));
					//	final String separator = format.getVariable().substring(format.getVariable().lastIndexOf(variable) + variable.length());
					//	System.out.println(separator);
					//	System.out.println(prefix);
					append = format.getChoice().replace(
							HelpMenuFormat.Placeholder.CHOICE,
							StringUtils.join(argument.getPossibilities(), format.getSeparator()));
				}

				System.out.println(Chat.stripColors(append));
				fullCommand.append(append);
			}

			lines[l] = new HelpLine(
					Chat.stripColors(fullCommand.toString()),

					fullCommand.toString(),

					new ComponentBuilder(fullCommand.toString())
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												  new Text(new ComponentBuilder(format.getSuggest().replace(
														  HelpMenuFormat.Placeholder.COMMAND,
														  fullCommand.toString())).create())))
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												  Chat.stripColors(fullCommand.toString())))
							.create());

			builder.append(new TextComponent(fullCommand.toString()));

			builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										 new Text(new ComponentBuilder(format.getSuggest().replace(
												 HelpMenuFormat.Placeholder.COMMAND,
												 fullCommand.toString())).create())));

			builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
										 Chat.stripColors(fullCommand.toString())));

			if (l % format.getPageSize() == 0) {//new page
				pages[p] = new HelpPage(lines);
				p++;
				l = 0;
			}

			l++;
		}

		return this.helpMenus.put(command, new HelpMenu(pages, format.getPageSize(), pageCount));
	}
}
