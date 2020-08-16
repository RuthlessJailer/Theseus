package com.ruthlessjailer.api.theseus;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.util.Map;

import static com.ruthlessjailer.api.theseus.ReflectUtil.*;

public final class Spigot {

	public static final void registerCommand(final Command command){
		final CommandMap commandMap = getCommandMap();

		commandMap.register(command.getLabel(),command);

		Checks.checkTrue(command.isRegistered(), String.format("Unable to register command %s.", command.getName()));
	}

	@SuppressWarnings("unchecked")
	public static final void unregisterCommand(final String label){
		final PluginCommand command = Bukkit.getPluginCommand(label);

		final Map<String, Command> commandMap =
				(Map<String, Command>) getFieldValue(SimpleCommandMap.class, "knownCommands", getCommandMap());

		commandMap.remove(label);

		if(command != null){
			if(command.isRegistered()) {
				command.unregister(
						(CommandMap) getFieldValue(Command.class, "commandMap", command));
			}

			for(final String alias : command.getAliases()){
				commandMap.remove(alias);
			}
		}
	}

	public static SimpleCommandMap getCommandMap(){
		return (SimpleCommandMap) getFieldValue(getCraftBukkitClass("CraftServer"), "getCommandMap", Bukkit.getServer());
	}

}
