package com.ruthlessjailer.api.theseus;

import com.ruthlessjailer.api.theseus.example.TestCommand;
import com.ruthlessjailer.api.theseus.example.TestListener;
import com.ruthlessjailer.api.theseus.multiversion.MinecraftVersion;

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

//		final ItemStack concrete = XMaterial.BLACK_CONCRETE.toItemStack();
//		final ItemStack ice      = XMaterial.BLUE_ICE.toItemStack();
//
//		System.out.println(concrete.getItemMeta().getDisplayName());
//		System.out.println(ice.getItemMeta().getDisplayName());

//		final TestMenu menu = new TestMenu();
//
//		Common.runTimer(() -> {
//			Bukkit.getOnlinePlayers().forEach(menu::displayTo);
//		}, 20 * 10);

		registerCommands(new TestCommand());
		registerEvents(new TestListener());
	}
}
