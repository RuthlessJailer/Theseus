package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
		final Location l = e.getTo().clone();
		l.setY(l.getBlockY() - 1);
		final Block b = e.getPlayer().getWorld().getBlockAt(l);
		if (!b.getType().isAir()) {
			b.setType(Common.selectRandom(XColor.values()).toMaterial("WHITE_CONCRETE"));
		}
	}
}
