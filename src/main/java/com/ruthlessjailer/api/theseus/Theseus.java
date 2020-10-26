package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.item.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * For testing purposes only.<p>
 * Will be removed in releases.
 *
 * @author Vadim Hagedorn
 */
public final class Theseus extends PluginBase {

	static {
		Chat.setDebugMode(true);
	}

	@Override
	protected void onStart() {
		System.out.println("Starting on "+MinecraftVersion.SERVER_VERSION+".");

		final ItemStack concrete = XMaterial.BLACK_CONCRETE.toItemStack();
		final ItemStack ice      = XMaterial.BLUE_ICE.toItemStack();

		Common.runTimer(() -> {
			Bukkit.getOnlinePlayers().forEach((p) -> p.getInventory().addItem(concrete, ice));
		}, 20*5);

	}
}
