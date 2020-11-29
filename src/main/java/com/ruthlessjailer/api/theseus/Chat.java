package com.ruthlessjailer.api.theseus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
public final class Chat {

	@Getter
	@Setter
	private static boolean debugMode = false;

	public static void send(@NonNull final CommandSender who, @NonNull final String... what) {
		for (final String s : what) {
			who.sendMessage(colorize(s));
		}
	}

	public static void send(@NonNull final CommandSender who, @NonNull final Collection<String> what) {
		for (final String s : what) {
			who.sendMessage(colorize(s));
		}
	}

	public static void send(@NonNull final String what, @NonNull final CommandSender... who) {
		for (final CommandSender sender : who) {
			send(sender, what);
		}
	}

	public static void send(@NonNull final Collection<String> what, @NonNull final CommandSender... who) {
		for (final CommandSender sender : who) {
			send(sender, what);
		}
	}

	public static void send(@NonNull final String what, @NonNull final Collection<CommandSender> who) {
		for (final CommandSender sender : who) {
			send(sender, what);
		}
	}

	public static void send(@NonNull final Collection<String> what, @NonNull final Collection<CommandSender> who) {
		for (final CommandSender sender : who) {
			send(sender, what);
		}
	}

	public static void broadcast(@NonNull final String... announcement) {
		for (final String s : announcement) {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(Chat.colorize(s));
				Bukkit.getConsoleSender().sendMessage(Chat.colorize(announcement));
			}
		}
	}

	public static void broadcast(@NonNull final Collection<String> announcement) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			for (final String s : announcement) {
				player.sendMessage(Chat.colorize(s));
				Bukkit.getConsoleSender().sendMessage(s);
			}
		}
	}

	public static String colorize(final String string) {
		return ChatColor.translateAlternateColorCodes('&', Common.getString(string));
	}

	public static String[] colorize(final String... strings) {
		if (strings == null) { return new String[]{}; }
		return Arrays.stream(strings).map(Chat::colorize).collect(Collectors.toList()).toArray(new String[strings.length]);
	}


	public static List<String> colorize(final Collection<String> strings) {
		if (strings == null) { return Collections.emptyList(); }
		return strings.stream().map(Chat::colorize).collect(Collectors.toList());
	}

	public static String stripColors(final String string) {
		return Common.getString(string).replaceAll("([&" + ChatColor.COLOR_CHAR + "])([0-9a-fk-or])", "");
	}

	public static String[] stripColors(final String... strings) {
		if (strings == null) { return new String[]{}; }
		return Arrays.stream(strings).map(Chat::stripColors).collect(Collectors.toList()).toArray(new String[strings.length]);
	}

	/**
	 * Prints a debug message.
	 * Will only trigger if {@link Chat#isDebugMode()}.
	 * <p>
	 * Format: {@code "[00:00:00 INFO]: [DEBUG] [PREFIX] message"}
	 * If prefix is null or empty: {@code "[00:00:00 INFO]: [DEBUG] [|] message"}
	 */
	public static void debug(@NonNull final String prefix, @NonNull final String... messages) {
		final String parsed = Common.getString(prefix);
		if (debugMode) {
			for (final String message : messages) {
				final String text = String.format("[DEBUG] [%s] %s", parsed.isEmpty() ? "|" : parsed, message);

				if (PluginBase.hasLog()) {
					PluginBase.getLog().info(text);
				} else {
					System.out.println(new StringBuilder(text).insert(7, ":"));
				}
			}
		}
	}

	/**
	 * Prints a debug message.
	 * Will only trigger if {@link Chat#isDebugMode()}.
	 * <p>
	 * Format: {@code "[00:00:00 INFO]: [DEBUG] [PREFIX] message"}
	 * If prefix is null or empty: {@code "[00:00:00 INFO]: [DEBUG] [|] message"}
	 */
	public static void debug(@NonNull final String prefix, @NonNull final Object... objects) {
		final String parsed = Common.getString(prefix);
		if (debugMode) {
			for (final String message : Common.convert(
					Arrays.asList(objects),
					(o) -> o != null && o.getClass().isArray()
						   ? Arrays.toString((Object[]) o)
						   : (o != null
							  ? o.toString()
							  : "null"))) {

				final String text = String.format("[DEBUG] [%s] %s", parsed.isEmpty() ? "|" : parsed, message);

				if (PluginBase.hasLog()) {
					PluginBase.getLog().info(text);
				} else {
					System.out.println(new StringBuilder(text).insert(7, ":"));
				}
			}
		}
	}

	public static void info(@NonNull final String... messages) {
		for (final String message : messages) {
			if (PluginBase.hasLog()) {
				PluginBase.getLog().info(message);
			} else {
				System.out.println("[INFO]: " + message);
			}
		}
	}

	public static void warning(@NonNull final String... messages) {
		for (final String message : messages) {
			if (PluginBase.hasLog()) {
				PluginBase.getLog().warning(message);
			} else {
				System.out.println("[WARN]: " + message);
			}
		}
	}

	public static void severe(@NonNull final String... messages) {
		for (final String message : messages) {
			if (PluginBase.hasLog()) {
				PluginBase.getLog().warning(message);
			} else {
				System.err.println("[ERROR]: " + message);
			}
		}
	}

	private static String consoleColorize(final String string) {
		return ConsoleColor.translateAlternateColorCodes('&', Common.getString(string));
	}

	public static String[] consoleColorize(final String... strings) {
		if (strings == null) { return new String[]{}; }
		return Arrays.stream(strings).map(Chat::consoleColorize).collect(Collectors.toList()).toArray(new String[strings.length]);
	}

	public static String bungeeColorize(final String string) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', Common.getString(string));
	}

	public static String[] bungeeColorize(final String... strings) {
		if (strings == null) { return new String[]{}; }
		return Arrays.stream(strings).map(Chat::bungeeColorize).collect(Collectors.toList()).toArray(new String[strings.length]);
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
