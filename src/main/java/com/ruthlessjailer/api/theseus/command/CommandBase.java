package com.ruthlessjailer.api.theseus.command;

import com.ruthlessjailer.api.theseus.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandBase extends Command {

	public static final String DEFAULT_PERMISSION_MESSAGE = "&cYou do not the permission &3${permission}&c needed to run this command!";
	public static final String DEFAULT_PERMISSION_SYNTAX = "${plugin.name}.command.${command.label}";

	private final String label;

	@Setter
	@Getter
	private int minArgs = 0;

	@Getter
	private boolean registered = false;

	protected String[] args;
	protected CommandSender sender;

	public CommandBase(@NonNull final String label) {
		this(parseLabel(label), parseAliases(label));
	}

	private CommandBase(@NonNull final String label, final List<String> aliases) {
		super(label);

		Checks.checkTrue(!(this instanceof CommandExecutor) || !(this instanceof TabCompleter),
						 String.format("Do not implement org.bukkit.CommandExecutor org.bukkit.TabCompleter in " +
									   "command class %s.", this.getClass().getPackage()),
						 CommandException.class);

		this.label = label;
		this.setPermissionMessage(this.getPermissionMessage());
	}

	private static String parseLabel(final String label){
		return label.split("\\|")[0];
	}

	private static List<String> parseAliases(final String label){
		final String[] aliases = label.split("\\|");
		return aliases.length > 1 ? Arrays.asList(Common.copyToEnd(aliases,1)) : new ArrayList<>();
	}

	public final void register(){
		Checks.checkTrue(!this.registered, "Command is already registered", CommandException.class);

		final PluginCommand currentCommand = Bukkit.getPluginCommand(this.label);

		if(currentCommand != null){
			final String plugin = currentCommand.getPlugin().getName();

			if(!plugin.equals(PluginBase.getName0())){
				Chat.warning(String.format("Plugin %s is already using command %s! Stealing...", plugin, this.label));
			}
			Spigot.unregisterCommand(this.label);

			Chat.info(String.format("Muahahahaha! Stole command %s from plugin %s!", this.label, plugin));
		}

		Spigot.registerCommand(this);
		this.registered = true;
	}

	public final void unregister(){
		Checks.checkTrue(this.registered,"Already unregistered.",CommandException.class);

		Spigot.unregisterCommand(this.label);

		this.registered = false;
	}

	public final String getDefaultPermissionSyntax(){
		return DEFAULT_PERMISSION_SYNTAX.replace("${plugin.name}", PluginBase.getName0()).replace("${command.label}",
																						  this.label);
	}

	public final String getDefaultPermissionMessage(){
		return Chat.colorize(DEFAULT_PERMISSION_MESSAGE.replace("${permission}", this.getDefaultPermissionSyntax()));
	}

	@Override
	public final boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel,
							@NotNull final String[] args) {

		this.args = args;
		this.sender = sender;

		return true;
	}

	protected abstract void runCommand();
}
