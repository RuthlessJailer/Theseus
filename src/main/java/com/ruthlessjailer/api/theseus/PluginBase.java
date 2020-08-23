package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public abstract class PluginBase extends JavaPlugin implements Listener {

	private static volatile PluginBase instance;

	@Getter
	private static volatile Logger log;

	protected boolean enabled = false;

	/**
	 * Get the current instance of the plugin.
	 *
	 * @return the found instance of the plugin or {@code null}
	 */
	public static final PluginBase getInstance() {
		try {
			return instance == null
				   ? getPlugin(PluginBase.class)
				   : instance;
		} catch (final Throwable ignored) {
			return null;
		}
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
	 * Shortcut for {@link JavaPlugin#getDataFolder()} ()}.
	 *
	 * @return the {@link File} representing the plugin folder
	 */
	public static final File getFolder() { return getInstance().getDataFolder(); }

	/**
	 * Shortcut for {@link Chat#debug(String, String...)}.
	 */
	protected static final void debug(@NonNull final String... messages) { Chat.debug("Plugin", messages); }

	@Override
	public final void onLoad() {

		try {
			getInstance();
		} catch (final Throwable t) {
			if (MinecraftVersion.lessThan(MinecraftVersion.v1_7)) {
				debug("Detected obsolete server version. Setting instance.");
				instance = this;
			} else {
				throw t;
			}
		}

		debug("Calling beforeStart()");
		this.beforeStart();
		debug("Called beforeStart()");
	}

	@Override
	public final void onDisable() {
		this.beforeStop();

		instance = null;
		log      = null;

		this.onStop();
	}

	@Override
	public final void onEnable() {
		debug("Entering onEnable()");
		instance = getInstance();
		log      = this.getLogger();

		if (!this.getDataFolder().exists()) {//create plugin folder
			debug("Attempting to create plugin folder.");
			if (this.getDataFolder().mkdirs()) {
				Chat.info(String.format("Created folder %s.", this.getDataFolder().getName()));
			} else {
				Chat.warning(String.format("Unable to create folder %s.", this.getDataFolder().getName()));
			}
		}

		debug("Calling onStart()");
		this.onStart();
		debug("Called onStart()");

		this.registerEvents(this);

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
			this.getServer().getPluginManager().registerEvents(listener, this);
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
