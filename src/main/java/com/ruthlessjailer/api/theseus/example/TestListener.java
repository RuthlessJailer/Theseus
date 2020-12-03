package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * @author RuthlessJailer
 */
public class TestListener implements Listener {
	@EventHandler
	public void onJoin(final EntityExplodeEvent e) {
		e.setYield(0);
		final byte data = (byte) Common.RANDOM.nextInt(16);
		for (final Block block : e.blockList()) {
			Common.runLater(() -> {
				block.setType(ReflectUtil.getEnum(Material.class, "CONCRETE"));
				ReflectUtil.invokeMethod(ReflectUtil.getMethod(Block.class, "setData", byte.class), block, data);
			});
		}

	}

	@EventHandler
	public void onJoin(final ProjectileHitEvent e) {
		((TNTPrimed) e.getHitBlock().getLocation().getWorld().spawnEntity(e.getHitBlock().getLocation(), EntityType.PRIMED_TNT)).setFuseTicks(0);
	}
}
