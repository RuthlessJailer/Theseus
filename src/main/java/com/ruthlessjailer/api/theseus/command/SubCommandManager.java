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
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author RuthlessJailer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubCommandManager {

	private static final String  MESSAGE_METHOD_PARAMETERS = "Include all variables in method parameters, but make sure the first two are CommandSender sender and" +
															 " " +
															 "String[]" +
															 " args.";
	private static final String  MESSAGE_ENUMS             = "Only include enums!";
	private static final Pattern VARIABLES_PATTERN         = Pattern.compile("%[sidebp](<[a-z0-9_]+>)?");
	private static final Pattern ENUM_VARIABLE_PATTERN     = Pattern.compile("%e(<[a-z0-9_]+>)?");

	@Getter
	private static final SubCommandManager manager = new SubCommandManager();

	private final Map<CommandBase, List<SubCommandWrapper>> subCommands = new HashMap<>();

	private final Map<CommandBase, HelpMenu> helpMenus = new HashMap<>();

	public static void register(@NonNull final SuperiorCommand command) {
		Checks.verify(command instanceof CommandBase,
					  "SuperiorCommand implementations must extend CommandBase.",
					  SubCommandException.class);

		assert command instanceof CommandBase;

		if (!Bukkit.isPrimaryThread()) {
			Chat.warning("Async call to command /" + ((CommandBase) command).getLabel() + " (" + ReflectUtil.getPath(command.getClass()) + ") while registering.");
		}

		final List<SubCommandWrapper> wrappers = new ArrayList<>();

		for (final Method method : command.getClass().getDeclaredMethods()) {
			for (final Annotation annotation : method.getDeclaredAnnotations()) {
				if (annotation.annotationType().equals(SubCommand.class)) {
					final SubCommandWrapper wrapper = getManager().parseArgs((CommandBase) command, method,
																			 (SubCommand) annotation);
					wrappers.add(wrapper);
					Chat.debug("Commands",
							   String.format("Registering method %s in class %s as a sub command.",
											 wrapper.getMethod().getName(),
											 ReflectUtil.getPath(command.getClass())));
				}
			}
		}

		synchronized (getManager().subCommands) {
			getManager().subCommands.put((CommandBase) command, wrappers);
		}
	}

	public static List<String> tabCompleteFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {
		final List<String> result = new ArrayList<>();

		if (command.isAutoGenerateHelpMenu() && Common.hasPermission(sender, command.getCustomSubCommandPermissionSyntax("help"))) {
			result.add("help");
		}

		wrappers:
		for (final SubCommandWrapper wrapper : getManager().getSubCommands(command)) {

			if (wrapper.getArguments().length >= args.length) {
				for (int i = 0; i < args.length; i++) {
					final String   arg      = args[i];
					final Argument argument = wrapper.getArguments()[i];

					if (argument.getType().equals(OfflinePlayer.class)) {
						argument.updatePossibilities(Common.getPlayerNames().toArray(new String[0]));
					}

					if (!argument.isInfinite()) {
						boolean match = false;
						for (final String possibility : argument.getPossibilities()) {
							if (Common.startsWithIgnoreCase(possibility, arg)) {
								match = true;
							}
						}
						if (!match) {
							continue wrappers;
						}
					}
				}

				if (!wrapper.getArguments()[args.length - 1].isInfinite()) {

					if (!wrapper.getArguments()[0].isInfinite()) {//check perms
						final StringBuilder permission = command.chainCustomSubCommandPermissionSyntax(args[0]);
						for (int i = 1; i < args.length; i++) {
							if (wrapper.getArguments()[i].isInfinite()) {
								continue;
							}
							permission.append(wrapper.getArguments()[i].getPossibilities()[0]);
						}

						System.out.println(command.getCustomSubCommandPermissionSyntax(args[0].toLowerCase()));
						System.out.println(permission.toString());

						if (command.isAutoCheckPermissionForSubCommands() &&
							(!command.hasPermission(sender, permission.toString()) ||
							 !command.hasPermission(sender, command.getCustomSubCommandPermissionSyntax(args[0].toLowerCase())))) {
							return result;
						}

					}

					for (final String possibility : wrapper.getArguments()[args.length - 1].getPossibilities()) {
						if (Common.startsWithIgnoreCase(possibility, args[args.length - 1])) {
							result.add(possibility);
						}
					}
				}

			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> void executeFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {
		if (command.isAutoGenerateHelpMenu() && args.length >= 1) {//automatic help command
			if (args[0].equalsIgnoreCase("help")) {
				int page = 0;

				if (args.length >= 2) {
					try {
						page = Integer.parseInt(args[1]) - 1;//index
					} catch (final NumberFormatException ignored) { }
				}

				getManager().sendHelpMenuTo(getManager().getHelpMenu(command), sender, page);

			}
		}

		wrappers:
		for (final SubCommandWrapper wrapper : getManager().subCommands.get(command)) {

			final Class<?>[] declaredTypes = wrapper.getDeclaredTypes();

			final Object[] parameters = new Object[declaredTypes.length + 2];//sender and args should be first

			parameters[0] = sender;
			parameters[1] = args;

			int i = 0;//args counter
			int p = 2;//parameter/declaredType counter

			if (args.length < wrapper.getArguments().length) {//check args
				continue;
			}

			for (final Argument argument : wrapper.getArguments()) {//check constant arguments & parse variable placeholders
				if (argument.getType().equals(OfflinePlayer.class)) {
					argument.updatePossibilities(Common.getPlayerNames().toArray(new String[0]));
				}

				if (!argument.isInfinite()) {
					boolean match = false;
					for (final String possibility : argument.getPossibilities()) {
						if (possibility.equalsIgnoreCase(args[i])) {
							match = true;
							break;
						}
					}
					if (!match) {
						continue wrappers;
					}
				}

				if (argument.isDeclaredType()) {
					final Class<?> declaredType = argument.getType();

					if (declaredType.isEnum()) {//get enum value
						parameters[p] = ReflectUtil.getEnum((Class<E>) declaredType, args[i]);
					} else {//Integer, Double, Boolean, String, OfflinePlayer
						if (declaredType.equals(Integer.class) || declaredType.equals(Double.class) || declaredType.equals(Boolean.class)) {
							try {
								parameters[p] = ReflectUtil.newInstanceOf(declaredType, args[i]);//valueOf doesn't work with reflection
							} catch (final ReflectUtil.ReflectionException e) {
								if (e.getCause() instanceof InvocationTargetException) {//NumberFormatException
									continue wrappers;
								}
							}
						} else if (declaredType.equals(String.class)) {
							parameters[p] = args[i];
						} else if (declaredType.equals(OfflinePlayer.class)) {
							//final int finalP = p;
							parameters[p] = Bukkit.getPlayer(args[i]);
							if (parameters[p] == null) {
								parameters[p] = Bukkit.getOfflinePlayer(args[i]);//offline players aren't tab-completed, but still work when executing
							}
						}
						p++;
					}
				}
				i++;
			}

			Chat.debug("SubCommands", String.format("Invoking method %s in class %s for args '%s'.",
													wrapper.getMethod().getName(), command.getClass(), StringUtils.join(args, " ")));

			try {
				ReflectUtil.invokeMethod(wrapper.getMethod(), command, parameters);
			} catch (final ReflectUtil.ReflectionException e) {
				if (e.getCause().getCause() instanceof CommandException) {
					return;//no need to print stacktrace of deliberately-thrown CommandException
				}
				e.getCause().getCause().printStackTrace();//ReflectionException -> InvocationTargetException -> whatever caused it
			}

		}
	}

	public static void generateHelpMenu(@NonNull final CommandBase command, final HelpMenuFormat menuFormat) {

		if (!Bukkit.isPrimaryThread()) {
			Chat.warning("Async call to command /" + command.getLabel() + " (" + ReflectUtil.getPath(command.getClass()) + ") while generating help menu.");
		}

		final List<SubCommandWrapper> subCommands = getManager().getSubCommands(command);
		final HelpMenuFormat          format      = menuFormat == null ? HelpMenuFormat.DEFAULT_FORMAT : menuFormat;

		final List<SubCommandWrapper> wrappers = new ArrayList<>(subCommands);

		final int pageCount = (int) Math.ceil((double) subCommands.size() / (double) format.getPageSize());

		final HelpPage[] pages = new HelpPage[pageCount];

		int l = 1;//line counter (starts at 1 because of header)
		int p = 0;//page counter

		HelpLine[] lines = new HelpLine[format.getPageSize() + 2];//+2 for header and footer

		for (final SubCommandWrapper wrapper : subCommands) {

			wrappers.remove(wrapper);

			final StringBuilder fullCommand = new StringBuilder(
					Chat.colorize(format.getCommand().replace(
							HelpMenuFormat.Placeholder.COMMAND,
							command.getLabel())));

			for (final Argument argument : wrapper.getArguments()) {

				final String append;

				if (argument.isDeclaredType()) {
					append = format.getVariable().replace(
							HelpMenuFormat.Placeholder.VARIABLE,
							argument.getDescription());
				} else {

					final String preChoice = format.getChoice().substring(0, format.getChoice().indexOf(HelpMenuFormat.Placeholder.CHOICE));
					final String postChoice =
							format.getChoice().substring(format.getChoice().lastIndexOf(HelpMenuFormat.Placeholder.CHOICE) + HelpMenuFormat.Placeholder.CHOICE.length());

					append = format.getChoice().replace(
							HelpMenuFormat.Placeholder.CHOICE,
							StringUtils.join(argument.getPossibilities(), postChoice + format.getSeparator() + preChoice));
				}

				fullCommand.append(" ").append(Chat.colorize(append));
			}

			lines[l] = new HelpLine(
					Chat.stripColors(fullCommand.toString()),

					Chat.colorize(fullCommand.toString()),

					new ComponentBuilder(Chat.colorize(fullCommand.toString()))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												  new ComponentBuilder(Chat.colorize(format.getSuggest().replace(
														  HelpMenuFormat.Placeholder.COMMAND,
														  fullCommand.toString()))).create()))
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												  Chat.stripColors(fullCommand.toString())))
							.create(),
					command.getCustomPermissionSyntax());

			if (l % format.getPageSize() == 0 || wrappers.isEmpty()) {//new page

				//this never hits if you have less than the pageSize

				//header stuff start

				final ComponentBuilder headerBuilder    = new ComponentBuilder("");//header builder
				final StringBuilder    rawHeaderBuilder = new StringBuilder();

				final String header   = format.getHeader();//header
				final String previous = HelpMenuFormat.Placeholder.PREVIOUS;//previous placeholder
				final String next     = HelpMenuFormat.Placeholder.NEXT;//next placeholder

				final String prePrevious        = header.substring(0, header.indexOf(previous));//anything before the back button
				final String headerPostPrevious = header.substring(header.lastIndexOf(previous) + previous.length());//everything after the back button
				final String postNext           = headerPostPrevious.substring(headerPostPrevious.lastIndexOf(next) + next.length());//anything after the next button
				final String rawHeader          = headerPostPrevious.substring(0, headerPostPrevious.lastIndexOf(next));//everything in between the back and the next buttons

				headerBuilder.append(Chat.colorize(prePrevious), ComponentBuilder.FormatRetention.FORMATTING);//anything before the back button

				headerBuilder.append(Chat.colorize(format.getPrevious()), ComponentBuilder.FormatRetention.FORMATTING)//the back button
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(), p)));


				headerBuilder.append(Chat.colorize(rawHeader.replace(HelpMenuFormat.Placeholder.COMMAND, command.getLabel()))
										 .replaceAll(Common.escape(HelpMenuFormat.Placeholder.PAGE), String.valueOf(p + 1)),
									 ComponentBuilder.FormatRetention.FORMATTING);//everything in between the back and the next buttons

				headerBuilder.append(Chat.colorize(format.getNext()), ComponentBuilder.FormatRetention.FORMATTING)//the next button
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(),
																								p == pageCount
																								? p
																								: p + 2)));

				headerBuilder.append(Chat.colorize(postNext), ComponentBuilder.FormatRetention.FORMATTING);//anything after the next button

				rawHeaderBuilder.append(prePrevious)
								.append(format.getPrevious())
								.append(rawHeader.replace(HelpMenuFormat.Placeholder.COMMAND, command.getLabel()))
								.append(format.getNext())
								.append(postNext);

				lines[0] = new HelpLine(//header with buttons
										Chat.stripColors(rawHeaderBuilder.toString()),
										Chat.colorize(rawHeaderBuilder.toString()),
										headerBuilder.create(),
										"");

				//header stuff end

				//footer stuff start

				final StringBuilder footer = new StringBuilder();

				for (int i = 0; i < Chat.stripColors(format.getHeader()).length(); i += format.getFooter().length()) {
					footer.append(Chat.colorize(format.getFooter()));
				}

				lines[format.getPageSize() + 1] = new HelpLine(//footer
															   Chat.stripColors(footer.toString()),
															   Chat.colorize(footer.toString()),
															   new ComponentBuilder(Chat.colorize(footer.toString())).create(),
															   "");

				//footer stuff end

				pages[p] = new HelpPage(lines);
				p++;
				l = 1;//1 because of header

				lines = new HelpLine[format.getPageSize() + 2];

				continue;
			}

			l++;
		}

		synchronized (getManager().subCommands) {
			getManager().helpMenus.put(command, new HelpMenu(pages, format.getPageSize(), pageCount));
		}
	}

	public void sendHelpMenuTo(@NonNull final HelpMenu menu, @NonNull final CommandSender sender, final int pageNumber) {//it's getting an index, no need to correct

		final int page;

		if (pageNumber <= 0) {
			page = 0;
		} else if (pageNumber >= menu.getPageCount()) {
			page = menu.getPageCount() - 1;
		} else {
			page = pageNumber;
		}

		for (final HelpLine line : menu.getPages()[page].getLines()) {
			if (line != null) {//line can be null if there aren't enough elements to populate the page
				if (sender.isOp() ||
					sender.hasPermission(CommandBase.getStarPermissionSyntax()) ||
					sender.hasPermission(line.getPermission())) {//if they don't have the permission don't send them the command\

					Common.runTask(() -> sender.spigot().sendMessage(line.getFormatted()));
				}
			}
		}

	}

	public List<SubCommandWrapper> getSubCommands(@NonNull final CommandBase command) {
		synchronized (this.subCommands) {
			return this.subCommands.containsKey(command)
				   ? this.subCommands.get(command)
				   : new ArrayList<>();
		}
	}

	public HelpMenu getHelpMenu(@NonNull final CommandBase command) {
		synchronized (this.helpMenus) {
			return this.helpMenus.get(command);
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends Enum<E>> SubCommandWrapper parseArgs(@NonNull final CommandBase parent, @NonNull final Method method, @NonNull final SubCommand subCommand) {

		//variables start
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

		if (args[0].equalsIgnoreCase("help") && parent.isAutoGenerateHelpMenu()) {//help command override
			Chat.warning(String.format("Sub-command %s in class %s overrides default help command. Disabling automatic help menu...",
									   method.getName(), ReflectUtil.getPath(parent.getClass())));
			parent.setAutoGenerateHelpMenu(false);
		}

		//initial arg check

		boolean variables = false;
		for (final String arg : args) {
			if (arg.toLowerCase().matches("%[sidebp](<[a-z_0-9]+>)?")) {
				variables = true;
				break;
			}
		}

		if (!variables && argTypes.length == 0) {//no need to parse args if there are none
			return new SubCommandWrapper(parent,
										 Common.asArray(new Argument(
												 args[0].split("\\|"),
												 String.class,
												 false,
												 null)),
										 Common.asArray(String.class),
										 new Class<?>[0],
										 method);
		}

		//initialize variables

		for (final String arg : args) {//initialize declaredTypes and types variables
			if (arg.toLowerCase().matches(VARIABLES_PATTERN.pattern())) {
				t++;//type
			}
			if (arg.toLowerCase().matches(ENUM_VARIABLE_PATTERN.pattern())) {
				e++;//enum
			}
			i++;//counter
		}

		//make sure enums are provided
		Checks.verify(e == argTypes.length,
					  String.format("ArgTypes do not match InputArgs in method %s in class %s. Only include enums!",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		//make sure that the method has the same amount of declared parameters as there are variables
		Checks.verify(t == method.getParameterCount() - 2,
					  String.format("InputArgs do not match method parameters in method %s in class %s." + MESSAGE_METHOD_PARAMETERS + MESSAGE_ENUMS,
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		final Class<?>[] types         = new Class[i];//all types (choice arguments)
		final Class<?>[] declaredTypes = new Class[t];//declared method types (variables, not choices)

		t = 0;
		i = 0;
		e = 0;

		//variables end

		//main loop
		for (final String arg : args) {

			boolean d = !arg.toLowerCase().matches("%[sidebp](<[a-z_0-9]+>)?");//is default argument (not variable)

			if (!d) {//not default argument; parsing needed

				//begin parsing

				String description = null;

				if (arg.matches("%[sidebpSIDEBP]<[A-Za-z0-9_]+>")) {//description storage
					description = arg.substring(3, arg.length() - 1);
				}

				//main parsing

				switch (arg.toLowerCase().substring(0, 2)) {//get variable, regardless of description

					case "%s"://string

						types[i] = String.class;
						declaredTypes[t] = String.class;
						arguments[i] = new Argument(String.class, true, description);

						break;

					case "%e"://enum (class provided)
						if (argTypes.length <= 0) {
							throw new SubCommandException(String.format(
									"ArgType for InputArg %s in method %s in class %s is missing. " + MESSAGE_ENUMS,
									arg.toLowerCase(),
									method.getName(),
									ReflectUtil.getPath(parent.getClass())));
						}

						final Class<?> enumType = argTypes[e];

						Checks.verify(enumType.isEnum(),
									  String.format(
											  "ArgType %s does not match InputArg %s in method %s in class %s. " + MESSAGE_ENUMS,
											  ReflectUtil.getPath(enumType),
											  arg.toLowerCase(),
											  method.getName(),
											  ReflectUtil.getPath(parent.getClass())),
									  SubCommandException.class);

						types[i] = enumType;
						declaredTypes[t] = enumType;
						arguments[i] = new Argument(getEnumValueNames((Class<E>) enumType), (Class<E>) enumType, true, description);

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

					case "%p"://player

						types[i] = OfflinePlayer.class;
						declaredTypes[t] = OfflinePlayer.class;
						arguments[i] = new Argument(Common.getPlayerNames().toArray(new String[0]), OfflinePlayer.class, true, description);

						break;
					default://none; it's a choice argument
						d = true;
				}

				//end parsing

			}

			if (d) {//default argument assignment; no parsing needed
				types[i]     = String.class;
				arguments[i] = new Argument(arg.split("\\|"), String.class, false);
			}

			//variable incrementation

			if (arg.toLowerCase().matches(VARIABLES_PATTERN.pattern())) {//type counter increment
				t++;
			}

			if (arg.toLowerCase().matches(ENUM_VARIABLE_PATTERN.pattern())) {//enum counter increment
				e++;
			}

			i++;
		}
		//end main loop

		//final checks
		checkMethod(declaredTypes, method, parent);//make sure that the method's match the parse arguments

		//finally, create and return the wrapper
		return new SubCommandWrapper(parent,
									 arguments,
									 types,
									 declaredTypes,
									 method);
	}

	private void checkMethod(@NonNull final Class<?>[] declaredTypes, @NonNull final Method method, @NonNull final CommandBase parent) {

		final Class<?>[] methodParameterTypes = method.getParameterTypes();

		Checks.verify(method.getParameterCount() - 2 == declaredTypes.length,//sender and args
					  String.format("Parameters on method %s in class %s do not match ArgTypes. " + MESSAGE_METHOD_PARAMETERS,
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		if (declaredTypes.length == 0 && method.getParameterCount() == 2) {//nothing to check
			return;
		}

		Checks.verify(methodParameterTypes[0].equals(CommandSender.class),
					  "Parameters on method %s in class %s do not match InputArgs. (First arg must be CommandSender)" + MESSAGE_METHOD_PARAMETERS);

		Checks.verify(methodParameterTypes[1].equals(String[].class),
					  "Parameters on method %s in class %s do not match InputArgs. (Second arg must be String[])" + MESSAGE_METHOD_PARAMETERS);

		int i = 2;//counter (start at 2 because of sender and args)
		for (final Class<?> type : declaredTypes) {//loop through expected declared types

			if (type == null) {//should never throw
				throw new SubCommandException(
						String.format("ArgTypes on method %s in class %s do not match method parameters or InputArgs. " + MESSAGE_METHOD_PARAMETERS,
									  method.getName(),
									  ReflectUtil.getPath(parent.getClass())));
			}

			if (declaredTypes[i] == null) {//method's parameters don't match given args
				throw new SubCommandException(
						String.format("Parameters on method %s in class %s do not match InputArgs. " + MESSAGE_METHOD_PARAMETERS,
									  method.getName(),
									  ReflectUtil.getPath(parent.getClass())));
			}

			//make sure the expected type and the method's declared parameter are same
			Checks.verify(type.equals(ClassUtils.primitiveToWrapper(methodParameterTypes[i])),
						  String.format("Parameter %s on method %s in class %s does not match ArgType %s. " + MESSAGE_METHOD_PARAMETERS,
										methodParameterTypes[i].getName(),
										method.getName(),
										ReflectUtil.getPath(parent.getClass()),
										type.getName()),
						  SubCommandException.class);

			//increment
			i++;
		}
	}

	private <E extends Enum<E>> String[] getEnumValueNames(@NonNull final Class<E> clazz) {
		final E[] values = ReflectUtil.getEnumValues(clazz);
		return Common.convert(values, new String[values.length], Enum::name);
	}
}
