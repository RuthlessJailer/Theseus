package com.ruthlessjailer.api.theseus;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Theseus extends JavaPlugin {

	@Getter
	private static Theseus instance;
	@Getter
	private static Logger log;


	@SneakyThrows
	@Override
	public void onEnable() {
		Theseus.instance = this;
		Theseus.log   = this.getLogger();
		if(!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}
		this.getServer().getPluginManager().registerEvents(new TestListener(), this);
 	}

	@Override
	public void onDisable() {
		Theseus.instance = null;
	}

}
