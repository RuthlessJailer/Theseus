package com.ruthlessjailer.util.dionysus;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static Logger log;

    @Override
    public void onEnable() {
        log = getLogger();
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Plugin getPlugin(){ return this; }
}
