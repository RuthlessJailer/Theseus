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
import java.util.*;

/**
 * @author Vadim Hagedorn
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubCommandManager {

	@Getter
	private static final SubCommandManager manager = new SubCommandManager();

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

		this.subCommands.put((CommandBase) command, wrappers);
	}

	public List<String> tabCompleteFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {
		final List<String> result = new ArrayList<>();

		wrappers:
		for (final SubCommandWrapper wrapper : this.getSubCommands(command)) {

			if (wrapper.getArguments().length >= args.length) {
				for (int i = 0; i < args.length; i++) {
					final String   arg      = args[i];
					final Argument argument = wrapper.getArguments()[i];

					if (!argument.isInfinite()) {
						boolean match = false;
						for (final String possibility : argument.getPossibilities()) {
							if (Common.startsWithIgnoreCase(possibility, arg)) {
								match = true;
							}
							if (!match) {
								continue wrappers;
							}
						}
					}
				}
				if (!wrapper.getArguments()[args.length - 1].isInfinite()) {
					//we got past all the checks
					result.addAll(Arrays.asList(wrapper.getArguments()[args.length - 1].getPossibilities()));
				}
			}
		}

		return result;
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
				sender.spigot().sendMessage(line.getFormatted());
				System.out.println(line.getRaw());
			}
		}

	}

	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> boolean executeFor(@NonNull final CommandBase command, @NonNull final CommandSender sender, final String[] args) {

		if (command.isAutoGenerateHelpMenu() && args.length >= 1) {//automatic help command
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
		for (final SubCommandWrapper wrapper : getManager().subCommands.get(command)) {

			final Class<?>[] declaredTypes = wrapper.getDeclaredTypes();

			final Object[] parameters = new Object[declaredTypes.length];

			int i = 0;//counter
			int p = 0;//parameter/declaredType counter

			if (args.length < wrapper.getArguments().length) {//check args
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
			if (arg.toLowerCase().matches("%[sideb](<[a-z_0-9]+>)?")) {
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
			if (arg.toLowerCase().matches("%[sideb](<[a-z0-9_]+>)?")) {
				t++;//type
			}
			if (arg.toLowerCase().matches("%e(<[a-z_0-9]+>)?")) {
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
		Checks.verify(t == method.getParameterCount(),
					  String.format("InputArgs do not match method parameters in method %s in class %s. Only include enums!",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		final Class<?>[] types         = new Class[i];//all types (choice arguments)
		final Class<?>[] declaredTypes = new Class[t];//declared method types (variables, not choices)

		t = 0;
		i = 0;
		e = 0;

		//variables end

		Chat.debug("sub-command, pre  parse", args, argTypes, declaredTypes, types);

		//main loop

		for (final String arg : args) {

			boolean d = !arg.toLowerCase().matches("%[sideb](<[a-z_0-9]+>)?");//is default argument (not variable)

			if (!d) {//not default argument; parsing needed

				//begin parsing

				//TODO: possible remove this as it only causes problems and is only used in one place
				final Class<?> declaredType = argTypes.length > 0 ? argTypes[e] : null;//shouldn't throw ArrayIndexOutOfBoundsException ever again

				String description = null;

				if (arg.matches("%[sidebSIDEB]<[A-Za-z0-9_]+>")) {//description storage
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

						if (declaredType == null) {//when ArrayIndexOutOfBoundsException when declaring declaredType was fixed it became nullable
							throw new SubCommandException(String.format(
									"ArgType for InputArg %s in method %s in class %s is missing. Only include enums!",
									arg.toLowerCase(),
									method.getName(),
									ReflectUtil.getPath(parent.getClass())));
							//d = true;
							//break;
						}

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
						arguments[i] = new Argument(this.getEnumValueNames((Class<E>) declaredType), (Class<E>) declaredType, true, description);

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

			if (arg.toLowerCase().matches("%[sideb](<[a-z0-9_]+>)?")) {//type counter increment
				t++;
			}

			if (arg.toLowerCase().matches("%e(<[a-z0-9_]+>)?")) {//enum counter increment
				e++;
			}

			i++;
		}
		//end main loop

		Chat.debug("sub-command, post parse", args, argTypes, declaredTypes, types);

		//final checks
		this.checkMethod(declaredTypes, method, parent);//make sure that the method's match the parse arguments

		//finally, create and return the wrapper
		return new SubCommandWrapper(parent,
									 arguments,
									 types,
									 declaredTypes,
									 method);
	}

	private void checkMethod(@NonNull final Class<?>[] declaredTypes, @NonNull final Method method, @NonNull final CommandBase parent) {

		final Class<?>[] methodParameterTypes = method.getParameterTypes();

		Checks.verify(method.getParameterCount() == declaredTypes.length,
					  String.format("Parameters on method %s in class %s do not " +
									"match ArgTypes.",
									method.getName(),
									ReflectUtil.getPath(parent.getClass())),
					  SubCommandException.class);

		if (declaredTypes.length == 0 && method.getParameterCount() == 0) {//nothing to check
			return;
		}

		int i = 0;//counter
		for (final Class<?> type : declaredTypes) {//loop through expected declared types

			if (type == null) {//should never throw
				throw new SubCommandException(
						String.format("ArgTypes on method %s in class %s do not match method parameters or InputArgs.",
									  method.getName(),
									  ReflectUtil.getPath(parent.getClass())));
			}

			if (declaredTypes[i] == null) {//method's parameters don't match given args
				throw new SubCommandException(
						String.format("Parameters on method %s in class %s do not match InputArgs.",
									  method.getName(),
									  ReflectUtil.getPath(parent.getClass())));
			}

			//make sure the expected type and the method's declared parameter are same
			Checks.verify(type.equals(methodParameterTypes[i]),
						  String.format("Parameter %s on method %s in class %s does not match ArgType %s.",
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

	public HelpMenu generateHelpMenu(@NonNull final CommandBase command, final HelpMenuFormat menuFormat) {

		final List<SubCommandWrapper> subCommands = this.getSubCommands(command);
		final HelpMenuFormat          format      = menuFormat == null ? HelpMenuFormat.DEFAULT_FORMAT : menuFormat;

		final List<SubCommandWrapper> wrappers = new ArrayList<>(subCommands);

		final int pageCount = (int) Math.ceil((double) subCommands.size() / (double) format.getPageSize());

		final HelpPage[] pages = new HelpPage[pageCount];

		int l = 1;//line counter (starts at 1 because of header)
		int p = 0;//page counter

		HelpLine[] lines = new HelpLine[(subCommands.size() < format.getPageSize())//in case the size is less
										? (subCommands.size() + 2)
										: (format.getPageSize() + 2)];//+2 for header and footer

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

				System.out.println(Chat.stripColors(append));
				fullCommand.append(" ").append(Chat.colorize(append));
			}

			lines[l] = new HelpLine(
					Chat.stripColors(fullCommand.toString()),

					Chat.colorize(fullCommand.toString()),

					new ComponentBuilder(Chat.colorize(fullCommand.toString()))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												  new Text(new ComponentBuilder(Chat.colorize(format.getSuggest().replace(
														  HelpMenuFormat.Placeholder.COMMAND,
														  fullCommand.toString()))).create())))
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												  Chat.stripColors(fullCommand.toString())))
							.create());

			if (l % format.getPageSize() == 0 || wrappers.isEmpty()) {//new page

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
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(), p)));


				headerBuilder.append(Chat.colorize(rawHeader.replace(HelpMenuFormat.Placeholder.COMMAND, command.getLabel()))
										 .replaceAll(Common.escape(HelpMenuFormat.Placeholder.PAGE), String.valueOf(p + 1)),
									 ComponentBuilder.FormatRetention.FORMATTING);//everything in between the back and the next buttons

				headerBuilder.append(Chat.colorize(format.getNext()), ComponentBuilder.FormatRetention.FORMATTING)//the next button
							 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s help %d", command.getLabel(), p == pageCount ? p : p + 2)));

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

				lines[format.getPageSize() + 1] = new HelpLine(//footer
															   Chat.stripColors(format.getFooter()),
															   Chat.colorize(format.getFooter()),
															   new ComponentBuilder(Chat.colorize(format.getFooter())).create());

				pages[p] = new HelpPage(lines);
				p++;
				l = 1;//1 because of header

				lines = new HelpLine[format.getPageSize() + 2];

				continue;
			}

			l++;
		}

		return this.helpMenus.put(command, new HelpMenu(pages, format.getPageSize(), pageCount));
	}
}
