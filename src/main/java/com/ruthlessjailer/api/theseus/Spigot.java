package com.ruthlessjailer.api.theseus;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

import static com.ruthlessjailer.api.theseus.ReflectUtil.*;

/**
 * @author RuthlessJailer
 */
public final class Spigot {

	/**
	 * Registers a command into the {@link CommandMap Bukkit command map}.
	 *
	 * @param command the {@link Command} to register
	 */
	public static void registerCommand(@NonNull final Command command) {
		final CommandMap commandMap = getCommandMap();

		commandMap.register(command.getLabel(), command);
	}

	/**
	 * Unregisters a command and all of its aliases from the {@link CommandMap Bukkit command map}.
	 *
	 * @param label the name of the command to unregister
	 */
	public static void unregisterCommand(@NonNull final String label) {
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

	/**
	 * @return the {@link CommandMap Bukkit command map}.
	 */
	public static SimpleCommandMap getCommandMap() {
		return invokeMethod(getOBCClass("CraftServer"),
							"getCommandMap",
							Bukkit.getServer());
	}

	/**
	 * {@link Spigot#unregisterEnchantment(Enchantment) Unregisters} an {@link Enchantment} (if it exists), and then registers it again.
	 *
	 * @param enchantment the {@link Enchantment} to register
	 */
	public static void registerEnchantment(@NonNull final Enchantment enchantment) {
		unregisterEnchantment(enchantment);

		setField(Enchantment.class, "acceptingNew", null, true);
		Enchantment.registerEnchantment(enchantment);
	}

	/**
	 * Unregisters an {@link Enchantment}.
	 *
	 * @param enchantment the {@link Enchantment} to register
	 */
	public static void unregisterEnchantment(@NonNull final Enchantment enchantment) {

		final Map<String, Enchantment> byName = getFieldValue(Enchantment.class, "byName", null);
		byName.remove(enchantment.getName());

		final Map<NamespacedKey, Enchantment> byKey = getFieldValue(Enchantment.class, "byKey", null);
		byKey.remove(enchantment.getKey());
	}

}
