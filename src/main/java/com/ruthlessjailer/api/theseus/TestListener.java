package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TestListener implements Listener {
	@EventHandler
	public void onJoin(final PlayerJoinEvent event){
		MinecraftVersion.v1_14.isAtLeast(MinecraftVersion.v1_12);
	}
}
