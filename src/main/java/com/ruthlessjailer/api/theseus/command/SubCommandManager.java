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
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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

	public void sendHelpMenuTo(@NonNull final HelpMenu menu, @NonNull final CommandSender sender, final int pageNumber) {

		final int page;

		if (pageNumber == 0 || pageNumber > menu.getPages().length || pageNumber < 0) {
			page = 0;
		} else {
			page = pageNumber - 1;
		}

		for (final HelpLine line : menu.getPages()[page].getLines()) {
			sender.spigot().sendMessage(line.getFormatted());
			System.out.println(line.getRaw());
		}

	}

	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> boolean executeFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {

		if (command.isAutoGenerateHelpMenu()) {//automatic help command
			if (args[0].equalsIgnoreCase("help")) {
				int page = 0;

				if (args.length >= 2) {
					try {
						page = Integer.parseInt(args[1]) - 1;//index
					} catch (final NumberFormatException ignored) { }
				}

				this.sendHelpMenuTo(this.getHelpMenu(command), sender, page);

				return true;

			}
		}

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
					} else {//Integer, Double, Boolean
						try {
							parameters[p] = ReflectUtil.invokeMethod(declaredType, "valueOf", null, args[i]);
						} catch (final ReflectUtil.ReflectionException e) {
							if (e.getCause() instanceof InvocationTargetException) {
								continue wrappers;
							} else {
								try {
									parameters[p] = ReflectUtil.newInstanceOf(declaredType, args[i]);
								} catch (final ReflectUtil.ReflectionException x) { //try constructor (string doesn't work with valueOf for some reason)
									if (!(e.getCause() instanceof InvocationTargetException)) {
										e.printStackTrace();
									}
									continue wrappers;
								}
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
			} catch (final ReflectUtil.ReflectionException ignored) {
				return false;
			}

		}
		return true;
	}

	public List<SubCommandWrapper> getSubCommands(@NonNull final CommandBase command) {
		return this.subCommands.containsKey(command)
			   ? this.subCommands.get(command)
			   : new ArrayList<>();
	}

	public HelpMenu getHelpMenu(@NonNull final CommandBase command) { return this.helpMenus.get(command); }

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> SubCommandWrapper parseArgs(@NonNull final CommandBase parent, @NonNull final Method method, @NonNull final SubCommand subCommand) {
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

		int l = 1;//line counter (starts at 1 because of header)
		int p = 0;//page counter

		final HelpLine[] lines = new HelpLine[(subCommands.size() < format.getPageSize())//in case the size is less
											  ? (subCommands.size() + 2)
											  : (format.getPageSize() + 2)];//+2 for header and footer

		for (final SubCommandWrapper wrapper : subCommands) {

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

				System.out.println(Chat.stripColors(append));
				fullCommand.append(" ").append(Chat.colorize(append));
			}

			lines[l] = new HelpLine(
					Chat.stripColors(fullCommand.toString()),

					Chat.colorize(fullCommand.toString()),

					new ComponentBuilder(Chat.colorize(fullCommand.toString()))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												  new Text(new ComponentBuilder(format.getSuggest().replace(
														  HelpMenuFormat.Placeholder.COMMAND,
														  fullCommand.toString())).create())))
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												  Chat.stripColors(fullCommand.toString())))
							.create());

			if ((l - 1 != 0) && (l - 1 % format.getPageSize() == 0) || (l == subCommands.size()) || (subCommands.size() < format.getPageSize())) {//new page; -1 because
				// of header

				//this never hits if you have less than the pageSize

				//header stuff start

				final ComponentBuilder headerBuilder    = new ComponentBuilder();//header builder
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
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(), p == 0 ? p : p - 1)));

				headerBuilder.append(Chat.colorize(rawHeader.replace(HelpMenuFormat.Placeholder.COMMAND, command.getLabel())),
									 ComponentBuilder.FormatRetention.FORMATTING);//everything in between the
				// back and
				// the next
				// buttons

				headerBuilder.append(Chat.colorize(format.getNext()), ComponentBuilder.FormatRetention.FORMATTING)//the next button
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(), p == pageCount - 1 ? p : p + 1)));

				headerBuilder.append(Chat.colorize(postNext), ComponentBuilder.FormatRetention.FORMATTING);//anything after the next button

				rawHeaderBuilder.append(prePrevious)
								.append(format.getPrevious())
								.append(rawHeader.replace(HelpMenuFormat.Placeholder.COMMAND, command.getLabel()))
								.append(format.getNext())
								.append(postNext);

				lines[0] = new HelpLine(//header with buttons
										Chat.stripColors(rawHeaderBuilder.toString()),
										Chat.colorize(rawHeaderBuilder.toString()),
										headerBuilder.create());

				//header stuff end

				lines[l + 1] = new HelpLine(//footer
											Chat.stripColors(format.getFooter()),
											Chat.colorize(format.getFooter()),
											new ComponentBuilder(Chat.colorize(format.getFooter())).create());

				pages[p] = new HelpPage(lines);
				p++;
				l = 1;//1 because of header
			}

			l++;
		}

		return this.helpMenus.put(command, new HelpMenu(pages, format.getPageSize(), pageCount));
	}
}
