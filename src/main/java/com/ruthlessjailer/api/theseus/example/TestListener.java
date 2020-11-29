package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.multiversion.MinecraftVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author RuthlessJailer
 */
public class TestListener implements Listener {
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		MinecraftVersion.v1_14.isAtLeast(MinecraftVersion.v1_12);

	}
}
