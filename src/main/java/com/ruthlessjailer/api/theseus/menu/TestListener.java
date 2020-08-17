package com.ruthlessjailer.api.theseus.menu;

import com.ruthlessjailer.api.theseus.MinecraftVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TestListener implements Listener {
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		MinecraftVersion.v1_14.isAtLeast(MinecraftVersion.v1_12);

	}
}
