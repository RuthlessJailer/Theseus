package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestMenu;
import org.bukkit.Bukkit;

/**
 * For testing purposes only.<p>
 * Will be removed in releases.
 *
 * @author RuthlessJailer
 */

public final class Theseus extends PluginBase {

	static {
		Chat.setDebugMode(true);
	}

	@Override
	protected void onStart() {
		System.out.println("Starting on " + MinecraftVersion.SERVER_VERSION + ".");
//
//		final ItemStack concrete = XMaterial.BLACK_CONCRETE.toItemStack();
//		final ItemStack ice      = XMaterial.BLUE_ICE.toItemStack();
//
//		System.out.println(concrete.getItemMeta().getDisplayName());
//		System.out.println(ice.getItemMeta().getDisplayName());

		final TestMenu menu = new TestMenu();

		Common.runTimer(() -> {
			Bukkit.getOnlinePlayers().forEach(menu::displayTo);
		}, 20 * 10);


	}
}
