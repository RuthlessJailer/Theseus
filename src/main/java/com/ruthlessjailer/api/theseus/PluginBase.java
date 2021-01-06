package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommandException;
import com.ruthlessjailer.api.theseus.menu.MenuBase;
import com.ruthlessjailer.api.theseus.menu.MenuListener;
import com.ruthlessjailer.api.theseus.multiversion.MinecraftVersion;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author RuthlessJailer
 */
public abstract class PluginBase extends JavaPlugin implements Listener {

	private static volatile PluginBase instance;

	@Getter
	private static volatile Logger log;

	protected boolean enabled = false;

	private volatile Thread thread;


	/**
	 * Get the current instance of the plugin.
	 *
	 * @return the found instance of the plugin
	 */
	public static final PluginBase getInstance() {
		return instance == null
			   ? getPlugin(PluginBase.class)
			   : instance;
	}

	/**
	 * Log check.
	 *
	 * @return whether there is a logger yet or not
	 */
	public static boolean hasLog() { return log != null; }

	/**
	 * Instance check.
	 *
	 * @return whether there is an instance yet or not
	 */
	public static final boolean hasInstance() { return instance != null; }

	/**
	 * Gets the name of the current instance.
	 *
	 * @return the name of the instance or {@code "No instance"}
	 */
	public static final String getCurrentName() { return hasInstance() ? getInstance().getName() : "No instance"; }

	/**
	 * Shortcut for {@link JavaPlugin#getFile()}.
	 *
	 * @return the {@link File} representing the JAR file of the plugin
	 */
	public static final File getJar() { return getInstance().getFile(); }

	/**
	 * Shortcut for {@link JavaPlugin#getDataFolder()}.
	 *
	 * @return the {@link File} representing the plugin folder
	 */
	public static final File getFolder() { return getInstance().getDataFolder(); }

	/**
	 * Shortcut for {@link JavaPlugin#getResource(String)}.
	 *
	 * @param file the name of the file
	 *
	 * @return the {@link InputStream} for the resource or null
	 */
	public static final InputStream getPluginResource(@NonNull final String file) { return getInstance().getResource(file); }

	/**
	 * Checks if the current thread is sync or not.
	 *
	 * @return whether or not the current thread is the main thread
	 */
	public static boolean isMainThread() { return hasInstance() ? instance.thread == Thread.currentThread() : Bukkit.isPrimaryThread(); }

	/**
	 * Shortcut for {@link Chat#debug(String, String...)}.
	 */
	protected static final void debug(@NonNull final String... messages) { Chat.debug("Plugin", messages); }

	public static void catchError(@NonNull final Throwable throwable) {
		catchError(throwable, false);
	}

	public static void catchError(@NonNull final Throwable throwable, final boolean disable) {

		Chat.send(Bukkit.getConsoleSender(),
				  "&c------------------- The plugin has encountered an error. --------------------",
				  "&cPlease send the latest log &8(located in /logs/latest.log) &cto the developer.",
				  "&cHere is some information about the server: ",
				  "&c \tVersion: &b" + getInstance().getDescription().getName() + " v" + getInstance().getDescription().getVersion(),
				  "&c \tSpigot: &1" + Bukkit.getServer().getVersion() + "&c",
				  "&c \tBukkit: &5" + Bukkit.getServer().getBukkitVersion(),
				  "&c \tCraftBukkit: &a" + MinecraftVersion.SERVER_VERSION,
				  "&c \tJava: &6" + System.getProperty("java.version"),
				  "&c-----------------------------------------------------------------------------");

		if (throwable instanceof SubCommandException) {
			Chat.send(Bukkit.getConsoleSender(),
					  "&cYou have failed to properly use the sub-command api. Please refer to the documentation/errors and check your methods.",
					  "&c-----------------------------------------------------------------------------");
		}

		if (throwable instanceof ReflectUtil.ReflectionException) {
			Chat.send(Bukkit.getConsoleSender(),
					  "&cReflection error; your server version is either too old or not yet supported.",
					  "&c-----------------------------------------------------------------------------");
		}

		if (throwable instanceof ClassNotFoundException) {
			Chat.send(Bukkit.getConsoleSender(),
					  "&c-----------------------------------------------------------------------------");
		}


		throwable.printStackTrace();
		if (disable) {
			getInstance().setEnabled(false);
		}
		throw new RuntimeException();
	}

	@Override
	public final void onLoad() {

		instance    = this;
		this.thread = Thread.currentThread();
		log         = getLogger();


		if (!Bukkit.isPrimaryThread()) {
			catchError(new IllegalStateException("Async plugin load."));
		}

		debug("Calling beforeStart()");
		try {
			beforeStart();
		} catch (final Throwable t) {
			Chat.severe("Fatal error in beforeStart(), exiting...");
			catchError(t);
			return;
		}
		debug("Called beforeStart()");
	}

	@Override
	public final void onDisable() {
		beforeStop();

		instance = null;
		log      = null;

		for (final Player player : Bukkit.getOnlinePlayers()) {//clear menu metadata to eliminate classloader issues with reloads
			final MenuBase current = MenuBase.getCurrentMenu(player);
			if (current != null) {
				player.closeInventory();
			}

			MenuBase.clearMetadata(player);
		}

		debug("Calling onStop()");
		onStop();
		debug("Called onStop()");
	}

	@SneakyThrows
	@Override
	public final void onEnable() {
		debug("Entering onEnable()");

		if (!getDataFolder().exists()) {//create plugin folder
			debug("Attempting to create plugin folder.");
			if (getDataFolder().mkdirs()) {
				Chat.info(String.format("Created folder %s.", getDataFolder().getName()));
			} else {
				Chat.warning(String.format("Unable to create folder %s.", getDataFolder().getName()));
			}
		}

		registerEvents(this, new MenuListener(), PromptUtil.getListenerInstance());

		debug("Calling onStart()");
		try {
			onStart();
		} catch (final Throwable t) {
			Chat.severe("Fatal error in onStart() in class " + ReflectUtil.getPath(getClass()) + ", exiting...");
			catchError(t);
		} finally {
			debug("Called onStart()");
		}
	}

	/**
	 * Called before {@link JavaPlugin#onEnable()} is called.
	 *
	 * @see JavaPlugin#onLoad()
	 */
	protected void beforeStart() {}

	/**
	 * Called at the end of {@link JavaPlugin#onEnable()}.
	 */
	protected void onStart() {}

	/**
	 * Called at the start of {@link JavaPlugin#onDisable()}.
	 *
	 * @see PluginBase#onDisable()
	 */
	protected void beforeStop() {}

	/**
	 * Called at the end of {@link JavaPlugin#onDisable()}.
	 *
	 * @see PluginBase#onDisable()
	 */
	protected void onStop() {}

	protected void registerEvents(@NonNull final Listener... listeners) {
		for (@NonNull final Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
			debug("Registered listener " + ReflectUtil.getPath(listener.getClass()) + ".");
		}
	}

	protected void registerCommands(@NonNull final Command... commands) {
		for (@NonNull final Command command : commands) {
			Spigot.registerCommand(command);
			debug("Registered command " + ReflectUtil.getPath(command.getClass()) + ".");
		}
	}

	protected void registerCommands(@NonNull final CommandBase... commands) {
		for (@NonNull final CommandBase command : commands) {
			command.register();
			debug("Registered command " + ReflectUtil.getPath(command.getClass()) + ".");
		}
	}
}
