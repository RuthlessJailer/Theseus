package com.ruthlessjailer.api.theseus;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginBase extends JavaPlugin implements Listener {

	@Getter
	private static volatile PluginBase instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public static boolean hasInstance(){ return instance != null; }

	public static String getName0(){
		return hasInstance() ? getInstance().getName() : "No instance";
	}
}
