package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.*;
import com.ruthlessjailer.api.theseus.command.help.HelpMenuFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author RuthlessJailer
 */
public abstract class CommandBase extends Command {

	public static final String DEFAULT_PERMISSION_MESSAGE            = "&cYou do not have the permission &3${permission}&c needed to run this command!";
	public static final String DEFAULT_PERMISSION_SYNTAX             = "${plugin.name}.command.${command.label}";
	public static final String DEFAULT_SUB_COMMAND_PERMISSION_SYNTAX = "${permission}.${sub.command}";
	public static final String DEFAULT_PLAYER_FALSE_MESSAGE          = "&cThis command must be executed by a player!";

	@Getter
	private static String starPermissionSyntax = getDefaultStarPermissionSyntax();

	protected final String  label;
	private final   boolean isSuperior = this instanceof SuperiorCommand;
	protected       boolean registered = false;

	@Getter
	private String customPermissionSyntax = getDefaultPermissionSyntax();

	private String customSubCommandPermissionSyntax = getDefaultSubCommandPermissionSyntax();//custom getter and chain getter

	@Getter
	private String customPermissionMessage = getDefaultPermissionMessage();//bukkit's name is same that's why custom

	@Setter
	@Getter
	private int minArgs = 0;

	@Setter
	@Getter
	private boolean tabCompleteSubCommands = true;

	@Getter
	@Setter
	private boolean autoCheckPermissionForSubCommands = true;

	@Getter
	@Setter
	private boolean autoGenerateHelpMenu = true;

	@Getter
	@Setter
	private HelpMenuFormat helpMenuFormatOverride = HelpMenuFormat.DEFAULT_FORMAT;

	public CommandBase(@NonNull final String label) {
		this(parseLabel(label), parseAliases(label));
	}

	private CommandBase(@NonNull final String label, final List<String> aliases) {
		super(label, "description", DEFAULT_PERMISSION_MESSAGE
					  .replace("${permission}", DEFAULT_PERMISSION_SYNTAX
							  .replace("${plugin.name}", PluginBase.getCurrentName().toLowerCase())
							  .replace("${command.label}", label))//default permission message for bukkit (unused)
				, aliases);

		Checks.verify(!(this instanceof CommandExecutor) || !(this instanceof TabCompleter),
					  String.format("Do not implement org.bukkit.CommandExecutor org.bukkit.TabCompleter in command class %s.",
									ReflectUtil.getPath(getClass())),
					  CommandException.class);

		this.label = label;
		setCustomPermissionMessage(getCustomPermissionMessage());
	}

	protected static String getDefaultStarPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX
				.replace("${plugin.name}",
						 PluginBase.getCurrentName().toLowerCase())
				.replace("${command.label}",
						 "*");
	}

	protected static void setCustomStarPermissionSyntax(@NonNull final String customStarPermissionSyntax) {
		starPermissionSyntax = customStarPermissionSyntax;
	}

	private static String parseLabel(final String label) {
		return label.split("\\|")[0];
	}

	private static List<String> parseAliases(final String label) {
		final String[] aliases = label.split("\\|");
		return aliases.length > 1 ? Arrays.asList(Common.copyToEnd(aliases, 1)) : new ArrayList<>();
	}

	public final void register() {
		Checks.verify(!this.registered, "Command is already registered", CommandException.class);

		final PluginCommand currentCommand = Bukkit.getPluginCommand(getLabel());

		if (currentCommand != null) {
			final String plugin = currentCommand.getPlugin().getName();

			if (!plugin.equals(PluginBase.getCurrentName())) {
				Chat.warning(String.format("Plugin %s is already using command %s! Stealing...", plugin, getLabel()));
			}
			Spigot.unregisterCommand(getLabel());

			Chat.info(String.format("Muahahahaha! Stole command %s from plugin %s!", getLabel(), plugin));
		}

		Spigot.registerCommand(this);

		if (this.isSuperior) {
			SubCommandManager.register((SuperiorCommand) this);
			SubCommandManager.generateHelpMenu(this, this.helpMenuFormatOverride);
		}

		this.registered = true;
	}

	public final void unregister() {
		Checks.verify(this.registered, "Already unregistered.", CommandException.class);

		Spigot.unregisterCommand(getLabel());

		this.registered = false;
	}

	private String getDefaultPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX
				.replace("${plugin.name}",
						 PluginBase.getCurrentName().toLowerCase())
				.replace("${command.label}",
						 getLabel());
	}

	private String getDefaultSubCommandPermissionSyntax() {
		return CommandBase.DEFAULT_SUB_COMMAND_PERMISSION_SYNTAX
				.replace("${permission}",
						 getDefaultPermissionSyntax());
	}

	private String getDefaultPermissionMessage() {
		return Chat.colorize(
				CommandBase.DEFAULT_PERMISSION_MESSAGE.replace("${permission}", getDefaultPermissionSyntax()));
	}

	protected final void setCustomPermissionSyntax(@NonNull final String syntax) {
		this.customPermissionSyntax = syntax.replace("${plugin.name}", PluginBase.getCurrentName().toLowerCase())
											.replace("${command.label}",
													 getLabel());
	}

	protected final void setCustomSubCommandPermissionSyntax(@NonNull final String syntax) {
		this.customSubCommandPermissionSyntax = syntax.replace("${permission}", getCustomPermissionSyntax());
	}

	protected final void setCustomPermissionMessage(@NonNull final String message) {
		this.customPermissionMessage = message.replace("${permission}",
													   getCustomPermissionSyntax());
	}

	public final String getCustomSubCommandPermissionSyntax(@NonNull final String subCommandName) {
		return this.customSubCommandPermissionSyntax.replace("${sub.command}", subCommandName);
	}

	/**
	 * Convenience method for deep sub-commands. Usage as follows: <pre>{@code
	 * chainCustomSubCommandPermissionSyntax("sub").append(getCustomSubCommandPermissionSyntax("command")).toString();
	 * }</pre>
	 *
	 * @param subCommandName the starting sub-command label
	 *
	 * @return a {@link StringBuilder}
	 */
	public final StringBuilder chainCustomSubCommandPermissionSyntax(@NonNull final String subCommandName) {
		return new StringBuilder(getCustomSubCommandPermissionSyntax(subCommandName));
	}

	@Override
	public final boolean execute(final CommandSender sender, final String label, final String[] args) {

		Chat.debug("Commands", "Command /" + label + " with args " + Arrays.toString(args) + " executed by " + sender.getName() + ".");

		try {
			if (!hasPermission(sender, getCustomPermissionSyntax())) {
				Chat.send(sender, getCustomPermissionMessage());
				return false;
			}

			if (!(this.autoGenerateHelpMenu && args.length >= 1 && args[0].equalsIgnoreCase("help"))) {//don't run on help command
				runCommand(sender, args, label);
			}

			if (this.isSuperior) {
				SubCommandManager.executeFor(this, sender, args);
			}
		} catch (final CommandException ignored) {
		} catch (final Throwable t) {
			PluginBase.catchError(t);
			return false;
		}

		return true;
	}

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
		return tabComplete(sender, alias, args, null);
	}

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) throws IllegalArgumentException {
		return this.tabCompleteSubCommands && this.isSuperior
			   ? SubCommandManager.tabCompleteFor(this, sender, args)
			   : onTabComplete(sender, alias, args, location);
	}

	/**
	 * Checks if given {@link Permissible} {@link Permissible#isOp() is op}, has the {@link CommandBase#getStarPermissionSyntax() star permission}, has the
	 * {@link CommandBase#getCustomPermissionSyntax() command's permission}, or has the given permission.
	 *
	 * @param permissible the {@link Permissible} to check
	 * @param permission  the permission to check (last resort)
	 *
	 * @return {@code true} if the {@link Permissible} {@link Permissible#isOp() is op}, has the {@link CommandBase#getStarPermissionSyntax() star permission},
	 * 		has the {@link CommandBase#getCustomPermissionSyntax() command's permission}, or has the given permission; {@code false} if either argument is null or the
	 * 		given requirements are not met.
	 *
	 * @see Common#hasPermission(Permissible, String)
	 */
	public final boolean hasPermission(final Permissible permissible, final String permission) {
		if (permissible == null) {
			return false;
		}

		return permissible.isOp() ||
			   permissible.hasPermission(getStarPermissionSyntax()) ||
			   permissible.hasPermission(getCustomPermissionSyntax()) ||
			   (permission != null && permissible.hasPermission(permission));
	}

	protected abstract void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label);

	protected List<String> onTabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) { return new ArrayList<>(); }

	/**
	 * Returns the {@link Player} instance of the given {@link CommandSender}.
	 *
	 * @param sender the {@link CommandSender} convert
	 *
	 * @return the converted {@link Player}
	 *
	 * @throws CommandException if the sender is not a {@link Player}
	 */
	protected final Player getPlayer(@NonNull final CommandSender sender) {
		return getPlayer(sender, DEFAULT_PLAYER_FALSE_MESSAGE);
	}

	/**
	 * Returns the {@link Player} instance of the given {@link CommandSender}.
	 *
	 * @param sender       the {@link CommandSender} convert
	 * @param falseMessage the message to send to the {@link CommandSender} if they are not a {@link Player}
	 *
	 * @return the converted {@link Player}
	 *
	 * @throws CommandException if the sender is not a {@link Player}
	 */
	protected final Player getPlayer(@NonNull final CommandSender sender, @NonNull final String falseMessage) {
		if (!(sender instanceof Player)) {
			Chat.send(sender, falseMessage);
			throw new CommandException();
		}

		return (Player) sender;
	}

	/**
	 * Joins the provided array.
	 *
	 * @param startIndex the starting index, inclusive
	 * @param args       the {@link String}[] to parse
	 *
	 * @return the {@link String#join(CharSequence, CharSequence...) joined} {@link String}, {@link Arrays#copyOfRange(Object[], int, int) copied} from the {@code
	 * 		startIndex}
	 */
	protected final String joinArgs(final int startIndex, @NonNull final String[] args) {
		return String.join(" ", Common.copyToEnd(args, startIndex));
	}

	protected void send(@NonNull final CommandSender sender, @NonNull final String... messages) {
		Chat.send(sender, messages);
	}

	protected void sendf(@NonNull final CommandSender sender, @NonNull final String message, final Object... parameters) {
		Chat.sendf(sender, message, parameters);
	}

	protected void broadcast(@NonNull final String... messages) {
		Chat.broadcast(messages);
	}

	protected void broadcastf(@NonNull final String messages, final Object... parameters) {
		Chat.broadcastf(messages, parameters);
	}
}
