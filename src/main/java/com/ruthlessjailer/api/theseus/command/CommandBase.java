package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.*;
import com.ruthlessjailer.api.theseus.command.help.HelpMenuFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vadim Hagedorn
 */
public abstract class CommandBase extends Command {

	protected static final String DEFAULT_PERMISSION_MESSAGE =
			"&cYou do not the permission &3${permission}&c needed to run this command!";
	protected static final String DEFAULT_PERMISSION_SYNTAX  = "${plugin.name}.command.${command.label}";

	private final boolean isSuperior = this instanceof SuperiorCommand;

	protected String        label;
	protected String[]      args;
	protected CommandSender sender;

	protected boolean registered = false;

	@Getter
	private String customPermissionSyntax = this.getDefaultPermissionSyntax();

	@Getter
	private String customPermissionMessage = this.getDefaultPermissionMessage();

	@Setter
	@Getter
	private int minArgs = 0;

	@Setter
	@Getter
	private boolean tabCompleteSubCommands = true;

	@Getter
	@Setter
	private boolean autoGenerateHelpMenu = true;

	@Getter
	@Setter
	private HelpMenuFormat helpMenuFormatOverride = HelpMenuFormat.DEFAULT_FORMAT;

	public CommandBase(@NonNull final String label) {
		this(CommandBase.parseLabel(label), CommandBase.parseAliases(label));
	}

	private CommandBase(@NonNull final String label, final List<String> aliases) {
		super(label, "description", "usageMessage", aliases);

		Checks.verify(!(this instanceof CommandExecutor) || !(this instanceof TabCompleter),
					  String.format("Do not implement org.bukkit.CommandExecutor org.bukkit.TabCompleter in " +
									"command class %s.", this.getClass().getPackage()),
					  CommandException.class);

		this.label = label;
		this.setCustomPermissionMessage(this.getCustomPermissionMessage());
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

		final PluginCommand currentCommand = Bukkit.getPluginCommand(this.getLabel());

		if (currentCommand != null) {
			final String plugin = currentCommand.getPlugin().getName();

			if (!plugin.equals(PluginBase.getCurrentName())) {
				Chat.warning(String.format("Plugin %s is already using command %s! Stealing...", plugin, this.getLabel()));
			}
			Spigot.unregisterCommand(this.getLabel());

			Chat.info(String.format("Muahahahaha! Stole command %s from plugin %s!", this.getLabel(), plugin));
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

		Spigot.unregisterCommand(this.getLabel());

		this.registered = false;
	}

	private final String getDefaultPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX
				.replace("${plugin.name}",
						 PluginBase.getCurrentName())
				.replace("${command.label}",
						 this.getLabel());
	}

	public final String getDefaultPermissionMessage() {
		return Chat.colorize(
				CommandBase.DEFAULT_PERMISSION_MESSAGE.replace("${permission}", this.getDefaultPermissionSyntax()));
	}

	protected final void setCustomPermissionSyntax(@NonNull final String syntax) {
		this.customPermissionSyntax = syntax.replace("${plugin.name}", PluginBase.getCurrentName())
											.replace("${command.label}",
													 this.getLabel());
	}

	protected final void setCustomPermissionMessage(@NonNull final String message) {
		this.customPermissionMessage = message.replace("${permission}",
													   this.getCustomPermissionSyntax());
	}

	@Override
	public final synchronized boolean execute(final CommandSender sender, final String label, final String[] args) {

		Chat.debug("Commands", "Command /" + label + " with args " + Arrays.toString(args) + " executed by " + sender.getName() + ".");

		if (!Bukkit.isPrimaryThread()) {
			Chat.warning("Async call to command /" + label + " (" + ReflectUtil.getPath(this.getClass()) + ").");
		}

		if (!sender.hasPermission(this.getCustomPermissionSyntax())) {
			sender.sendMessage(this.getCustomPermissionMessage());
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

	protected abstract void runCommand(@NonNull final CommandSender sender, final String[] args, @NonNull final String label);

	protected List<String> onTabComplete(final CommandSender sender, final String alias, final String[] args, final Location location) { return new ArrayList<>(); }
}
