package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import com.ruthlessjailer.api.theseus.task.manager.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author RuthlessJailer
 */
public class TestListener implements Listener {
	@EventHandler
	public void onJoin(final EntityExplodeEvent e) {
		e.setYield(0);
		final XColor x = Common.selectRandom(XColor.values());
		for (final Block block : e.blockList()) {
			Common.runLater(() -> {
				block.setType(x.toMaterial("WHITE_CONCRETE"));
			});
		}

	}

	@EventHandler
	public void onJoin(final ProjectileHitEvent e) {
		((TNTPrimed) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT)).setFuseTicks(0);
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
//		final Location l = e.getTo().clone();
//		l.setY(l.getBlockY() - 1);
//		final Block b = e.getPlayer().getWorld().getBlockAt(l);
//		if (!b.getType().isAir()) {
//			b.setType(Common.selectRandom(XColor.values()).toMaterial("WHITE_CONCRETE"));
//		}
	}

	public static final Map<UUID, Boolean> GHOSTING = new HashMap<>();

	@EventHandler
	public void onPlace(final BlockPlaceEvent event) {
		if (!GHOSTING.containsKey(event.getPlayer().getUniqueId())) {
			return;
		}

		final BlockData data = event.getBlock().getBlockData();

		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (event.getItemInHand().getAmount() > 1) {
				event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
			} else {
				event.getPlayer().setItemInHand(null);
			}
		}

//		TaskManager.async.delay(() -> event.getPlayer().sendBlockChange(event.getBlock().getLocation(), data), 2);

		if (GHOSTING.get(event.getPlayer().getUniqueId())) {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				if (player.equals(event.getPlayer())) {
					TaskManager.sync.delay(() -> player.sendBlockChange(event.getBlock().getLocation(), Material.AIR.createBlockData()), 2);
					continue;
				}
				TaskManager.sync.delay(() -> player.sendBlockChange(event.getBlock().getLocation(), data), 2);
			}
			event.setCancelled(false);
		} else {
			TaskManager.sync.delay(() -> event.getPlayer().sendBlockChange(event.getBlock().getLocation(), data), 2);
			event.setCancelled(true);
		}
	}
}
