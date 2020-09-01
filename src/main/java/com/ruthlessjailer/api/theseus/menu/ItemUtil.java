package com.ruthlessjailer.api.theseus.menu;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Vadim Hagedorn
 */
public final class ItemUtil {

	/**
	 * Compares two {@link ItemStack}.
	 *
	 * @param item  one item
	 * @param other the other item
	 *
	 * @return true if they are the same, false if they differ
	 */
	public static final boolean compare(final ItemStack item, final ItemStack other) {
		return compare(item, other, true);
	}


	/**
	 * Compares two {@link ItemStack}.
	 *
	 * @param item        one item
	 * @param other       the other item
	 * @param compareMeta whether or not to compare item metas
	 *
	 * @return true if they are the same, false if they differ
	 */
	public static final boolean compare(final ItemStack item, final ItemStack other, final boolean compareMeta) {
		if (item == null && other == null) {//both null
			return true;
		}
		if (item == null ^ other == null) {//one is null
			return false;
		}

		if ((!item.hasItemMeta() && !other.hasItemMeta()) && (item.getType() == other.getType())) {//air; no item meta
			return true;
		}

		final ItemMeta itemMeta  = item.getItemMeta();
		final ItemMeta otherMeta = other.getItemMeta();

		return (item.equals(other) && itemMeta.equals(otherMeta)) ||
			   (item.getType() == other.getType() &&
				(!compareMeta || (itemMeta.getDisplayName().equals(otherMeta.getDisplayName()) &&
								  itemMeta.getLocalizedName().equals(otherMeta.getLocalizedName()) &&
								  itemMeta.getEnchants().equals(otherMeta.getEnchants()) &&
								  itemMeta.getItemFlags().equals(otherMeta.getItemFlags()) &&
								  itemMeta.getCustomModelData() == otherMeta.getCustomModelData() &&
								  (!itemMeta.hasLore() || !otherMeta.hasLore() || itemMeta.getLore().equals(otherMeta.getLore())))));
	}

}
