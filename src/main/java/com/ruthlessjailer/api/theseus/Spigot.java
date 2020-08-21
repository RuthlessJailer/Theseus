package com.ruthlessjailer.api.theseus;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.util.Map;

import static com.ruthlessjailer.api.theseus.ReflectUtil.*;

public final class Spigot {

	public static final void registerCommand(@NonNull final Command command) {
		final CommandMap commandMap = getCommandMap();

		commandMap.register(command.getLabel(), command);

		//Checks.verify(command.isRegistered(), String.format("Unable to register command %s.", command.getName()),
		//			  CommandException.class);
	}

	public static final void unregisterCommand(@NonNull final String label) {
		final PluginCommand command = Bukkit.getPluginCommand(label);

		final Map<String, Command> commandMap = getFieldValue(SimpleCommandMap.class, "knownCommands", getCommandMap());

		commandMap.remove(label);

		if (command != null) {
			if (command.isRegistered()) {
				command.unregister(getFieldValue(Command.class, "commandMap", command));
			}

			for (final String alias : command.getAliases()) {
				commandMap.remove(alias);
			}
		}
	}

	public static SimpleCommandMap getCommandMap() {
		return invokeMethod(getOBCClass("CraftServer"),
							"getCommandMap",
							Bukkit.getServer());
	}

}
