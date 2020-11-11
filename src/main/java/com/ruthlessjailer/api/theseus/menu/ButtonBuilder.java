/*
package com.ruthlessjailer.api.theseus.menu;

import lombok.Builder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

*/
/**
 * @author RuthlessJailer
 *//*

@Builder(builderClassName = "ButtonBaseCreator")
public final class ButtonBuilder {

	@Builder.Default
	private final ItemStack item = new ItemStack(Material.STONE);

	@Builder.Default
	private final ButtonType type = ButtonType.INFO;

	@Builder.Default
	private final ButtonAction action = (clicker, clickType, clickedWith) -> {};

	public static ButtonBaseCreator of(@NonNull final ButtonBase button) {
		return builder().type(button.getType()).item(button.getItem()).action(button.getAction());
	}

	public static class ButtonBaseCreator {

		protected ButtonBaseCreator() {}

		public ButtonBase build() {
			return new ButtonBase(this.item, this.type, this.action) {};
		}
	}

}
*/
