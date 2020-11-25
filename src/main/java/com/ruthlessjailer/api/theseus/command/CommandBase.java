package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.*;
import com.ruthlessjailer.api.theseus.command.help.HelpMenuFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author RuthlessJailer
 */
public abstract class CommandBase extends Command {

	public static final String         DEFAULT_PERMISSION_MESSAGE =
			"&cYou do not the permission &3${permission}&c needed to run this command!";
	public static final String         DEFAULT_PERMISSION_SYNTAX  = "${plugin.name}.command.${command.label}";
	@Getter
	private static      String         starPermissionSyntax       = getDefaultStarPermissionSyntax();
	private final       boolean        isSuperior                 = this instanceof SuperiorCommand;
	protected           String         label;
	protected           String[]       args;
	protected           CommandSender  sender;
	protected           boolean        registered                 = false;
	@Getter
	private             String         customPermissionSyntax     = getDefaultPermissionSyntax();
	@Getter
	private             String         customPermissionMessage    = getDefaultPermissionMessage();//bukkit's name is same that's why custom
	@Setter
	@Getter
	private             int            minArgs                    = 0;
	@Setter
	@Getter
	private             boolean        tabCompleteSubCommands     = true;
	@Getter
	@Setter
	private             boolean        autoGenerateHelpMenu       = true;
	@Getter
	@Setter
	private             HelpMenuFormat helpMenuFormatOverride     = HelpMenuFormat.DEFAULT_FORMAT;

	public CommandBase(@NonNull final String label) {
		this(CommandBase.parseLabel(label), CommandBase.parseAliases(label));
	}

	private CommandBase(@NonNull final String label, final List<String> aliases) {
		super(label, "description", "usageMessage", aliases);

		Checks.verify(!(this instanceof CommandExecutor) || !(this instanceof TabCompleter),
					  String.format("Do not implement org.bukkit.CommandExecutor org.bukkit.TabCompleter in " +
									"command class %s.", getClass().getPackage()),
					  CommandException.class);

		this.label = label;
		this.setCustomPermissionMessage(getCustomPermissionMessage());
	}

	protected static String getDefaultStarPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX
				.replace("${plugin.name}",
						 PluginBase.getCurrentName())
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

	private final String getDefaultPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX
				.replace("${plugin.name}",
						 PluginBase.getCurrentName())
				.replace("${command.label}",
						 getLabel());
	}

	public final String getDefaultPermissionMessage() {
		return Chat.colorize(
				CommandBase.DEFAULT_PERMISSION_MESSAGE.replace("${permission}", getDefaultPermissionSyntax()));
	}

	protected final void setCustomPermissionSyntax(@NonNull final String syntax) {
		this.customPermissionSyntax = syntax.replace("${plugin.name}", PluginBase.getCurrentName())
											.replace("${command.label}",
													 getLabel());
	}

	protected final void setCustomPermissionMessage(@NonNull final String message) {
		this.customPermissionMessage = message.replace("${permission}",
													   getCustomPermissionSyntax());
	}

	@Override
	public final synchronized boolean execute(final CommandSender sender, final String label, final String[] args) {

		Chat.debug("Commands", "Command /" + label + " with args " + Arrays.toString(args) + " executed by " + sender.getName() + ".");

		if (!Bukkit.isPrimaryThread()) {
			Chat.warning("Async call to command /" + label + " (" + ReflectUtil.getPath(getClass()) + ").");
		}

		if (!sender.hasPermission(getStarPermissionSyntax()) || !sender.hasPermission(getCustomPermissionSyntax())) {
			sender.sendMessage(getCustomPermissionMessage());
			return false;
		}

		this.label  = label;
		this.args   = args;
		this.sender = sender;

		if (!(this.autoGenerateHelpMenu && args.length >= 1 && args[0].equalsIgnoreCase("help"))) {//don't run on help command
			this.runCommand(sender, args, label);
		}

		if (this.isSuperior) {
			SubCommandManager.executeFor(this, sender, args);
		}

		return true;
	}

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
		return this.tabComplete(sender, alias, args, null);
	}

	@Override
	public final List<String> tabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) throws IllegalArgumentException {
		return this.tabCompleteSubCommands && this.isSuperior
			   ? SubCommandManager.tabCompleteFor(this, sender, args)
			   : this.onTabComplete(sender, alias, args, location);
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
	public boolean hasPermission(final Permissible permissible, final String permission) {
		if (permissible == null || permission == null) {
			return false;
		}

		return permissible.isOp() ||
			   permissible.hasPermission(getStarPermissionSyntax()) ||
			   permissible.hasPermission(getCustomPermissionSyntax()) ||
			   permissible.hasPermission(permission);
	}

	protected abstract void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label);

	protected List<String> onTabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) { return new ArrayList<>(); }
}
