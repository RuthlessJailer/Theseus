package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandBase extends Command {

	protected static final String DEFAULT_PERMISSION_MESSAGE =
			"&cYou do not the permission &3${permission}&c needed to run this command!";
	protected static final String DEFAULT_PERMISSION_SYNTAX  = "${plugin.name}.command.${command.label}";

	private final List<SubCommandWrapper> subCommands = new ArrayList<>();

	protected String        label;
	protected String[]      args;
	protected CommandSender sender;

	@Getter
	protected boolean registered = false;

	@Setter
	@Getter
	private int minArgs = 0;

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
		this.setPermissionMessage(this.getPermissionMessage());
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

		final PluginCommand currentCommand = Bukkit.getPluginCommand(this.label);

		if (currentCommand != null) {
			final String plugin = currentCommand.getPlugin().getName();

			if (!plugin.equals(PluginBase.getCurrentName())) {
				Chat.warning(String.format("Plugin %s is already using command %s! Stealing...", plugin, this.label));
			}
			Spigot.unregisterCommand(this.label);

			Chat.info(String.format("Muahahahaha! Stole command %s from plugin %s!", this.label, plugin));
		}

		Spigot.registerCommand(this);

		if (this instanceof SuperiorCommand) {
			SubCommandManager.register((SuperiorCommand) this);
		}

		this.registered = true;
	}

	public final void unregister() {
		Checks.verify(this.registered, "Already unregistered.", CommandException.class);

		Spigot.unregisterCommand(this.label);

		this.registered = false;
	}

	private final String getDefaultPermissionSyntax() {
		return CommandBase.DEFAULT_PERMISSION_SYNTAX.replace("${plugin.name}", PluginBase.getCurrentName())
													.replace("${command.label}",
															 this.label);
	}

	public final String getDefaultPermissionMessage() {
		return Chat.colorize(
				CommandBase.DEFAULT_PERMISSION_MESSAGE.replace("${permission}", this.getDefaultPermissionSyntax()));
	}

	@Override
	public final boolean execute(final CommandSender sender, final String label,
								 final String[] args) {

		this.label  = label;
		this.args   = args;
		this.sender = sender;

		this.runCommand();

		return true;
	}

	protected abstract void runCommand();
}
