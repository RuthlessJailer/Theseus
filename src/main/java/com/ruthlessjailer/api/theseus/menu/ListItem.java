package com.ruthlessjailer.api.theseus.menu;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This class can be extended to suit your own purposes.
 * This class was made in place of having a set type of object in the list.
 *
 * @author RuthlessJailer
 */
public class ListItem {

	private Object       value;
	private ItemStack    item;
	private ButtonAction action;

	public ListItem(final Object value, @NonNull final ItemStack item) {
		this(value, item, ButtonAction.EMPTY_ACTION);
	}

	public ListItem(final Object value, @NonNull final ItemStack item, @NonNull final ButtonAction action) {
		this.value  = value;
		this.item   = item;
		this.action = action;
	}

	/**
	 * Get the value of the item.
	 *
	 * @return the stored object
	 */
	public Object value() {
		return this.value;
	}

	/**
	 * Returns the {@link ItemStack} associated with the stored value.
	 *
	 * @return the {@link ItemStack} representation of the value
	 */
	public ItemStack item() {
		return this.item;
	}

	/**
	 * Returns the {@link ButtonAction} to be run when the item is clicked in this menu.
	 *
	 * @return the {@link ButtonAction} to be run on click
	 */
	public ButtonAction action() {
		return this.action;
	}

	/**
	 * Invalidates the stored value.
	 */
	public void clear() {
		this.value = null;
		this.item.setType(Material.AIR);//don't nullify the itemstack
	}

}
