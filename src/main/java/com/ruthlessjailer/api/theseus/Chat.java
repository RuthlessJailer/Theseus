package com.ruthlessjailer.api.theseus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class Chat {

	@Getter
	@Setter
	private static boolean debugMode = false;

	public static void send(final CommandSender who, final String... what) {
		for (final String s : what) {
			who.sendMessage(Chat.colorize(s));
		}
	}

	public static void send(final CommandSender who, final Collection<String> what) {
		for (final String s : what) {
			who.sendMessage(Chat.colorize(s));
		}
	}

	public static void send(final String what, final CommandSender... who) {
		for (final CommandSender sender : who) {
			Chat.send(sender, what);
		}
	}

	public static void send(final Collection<String> what, final CommandSender... who) {
		for (final CommandSender sender : who) {
			Chat.send(sender, what);
		}
	}

	public static void send(final String what, final Collection<CommandSender> who) {
		for (final CommandSender sender : who) {
			Chat.send(sender, what);
		}
	}

	public static void send(final Collection<String> what, final Collection<CommandSender> who) {
		for (final CommandSender sender : who) {
			Chat.send(sender, what);
		}
	}

	public static void broadcast(final String... announcement) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			for (final String s : announcement) {
				player.sendMessage(Chat.colorize(s));
				Bukkit.getConsoleSender().sendMessage(s);
			}
		}
	}

	public static void broadcast(final Collection<String> announcement) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			for (final String s : announcement) {
				player.sendMessage(Chat.colorize(s));
				Bukkit.getConsoleSender().sendMessage(s);
			}
		}
	}

	public static String colorize(final String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String colorize(final String... strings) {
		return Chat.colorize(StringUtils.join(strings, "\n"));
	}

	public static String stripColors(final String... strings) {
		final StringBuilder result = new StringBuilder();
		for (final String string : strings) {
			result.append(Common.getString(string).replaceAll("([&" + ChatColor.COLOR_CHAR + "])([0-9a-fk-or])", ""))
				  .append("\n");
		}
		return result.toString();
	}

	/**
	 * Prints a debug message.
	 * Will only trigger if {@link Chat#isDebugMode()}.
	 * <p>
	 * Format: {@code "[00:00:00 INFO]: [DEBUG] [PREFIX] message"}
	 * If prefix is null or empty: {@code "[00:00:00 INFO]: [DEBUG] [|] message"}
	 */
	public static void debug(final String prefix, final String... messages) {
		final String parsed = Common.getString(prefix);
		if (debugMode) {
			for (final String message : messages) {
				PluginBase.getLog().info(String.format("[DEBUG] [%s] %s", parsed.isEmpty() ? "|" : parsed, message));
			}
		}
	}

	public static void info(final String... messages) {
		for (final String message : messages) {
			PluginBase.getLog().info(message);
		}
	}

	public static void warning(final String... messages) {
		for (final String message : messages) {
			PluginBase.getLog().warning(message);
		}
	}

	public static void severe(final String... messages) {
		for (final String message : messages) {
			PluginBase.getLog().severe(message);
		}
	}

	private static String consoleColorize(final String string) {
		return ConsoleColor.translateAlternateColorCodes('&', string);
	}

	public static String consoleColorize(final String... strings) {
		return Chat.consoleColorize(StringUtils.join(strings, "\n"));
	}

	public static String bungeeColorize(final String string) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String bungeeColorize(final String... string) {
		return Chat.bungeeColorize(StringUtils.join(string, "\n"));
	}

	@AllArgsConstructor
	public enum ConsoleColor {//TODO: add JANSI compatibility

		CODES("r?0426153f"),
		RESET("\u001B[0m"),//r
		UNKNOWN("\u001B[7m"),//?
		BLACK("\u001B[30m"),//0
		RED("\u001B[31m"),//4
		GREEN("\u001B[32m"),//2
		GOLD("\u001B[33m"),//6
		BLUE("\u001B[34m"),//1
		PURPLE("\u001B[35m"),//5
		AQUA("\u001B[36m"),//3
		WHITE("\u001B[37m");//f

		private final String value;

		public static String translateAlternateColorCodes(final char altColorChar, final String textToTranslate) {
			final StringBuilder sb = new StringBuilder(textToTranslate);
			for (int i = 0; i < sb.length(); i++) {
				if (sb.charAt(i) == altColorChar && ConsoleColor.CODES.toString().indexOf(sb.charAt(i + 1)) != -1) {
					sb.replace(i, i + 2, ConsoleColor.values()[ConsoleColor.CODES.toString()
																				 .indexOf(
																						 sb.charAt(i + 1))].toString());
				}
			}
			return sb.toString();
		}


		@Override
		public String toString() {
			return this.value;
		}
	}

}
