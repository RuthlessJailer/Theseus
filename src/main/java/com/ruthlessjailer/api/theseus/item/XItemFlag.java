package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.MinecraftVersion;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.NonNull;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Vadim Hagedorn
 * @see ItemFlag
 */
public enum XItemFlag {

	HIDE_ENCHANTS,//enchantments
	HIDE_ATTRIBUTES,//attributes like damage
	HIDE_UNBREAKABLE,//unbreakable
	HIDE_DESTROYS,//can destroy
	HIDE_PLACED_ON,//can place on
	HIDE_POTION_EFFECTS,//potion effects
	HIDE_DYE;//leather color

	public static XItemFlag fromItemFlag(@NonNull final ItemFlag flag) { return valueOf(flag.name()); }

	public ItemFlag getItemFlag()                                      { return ReflectUtil.getEnum(ItemFlag.class, this.toString()); }

	public ItemStack applyToItem(@NonNull final ItemStack item) {

		if (!item.hasItemMeta()) {
			return item;
		}

		try {
			final ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ReflectUtil.getEnum(ItemFlag.class, this.toString()));
			item.setItemMeta(meta);
			return item;
		} catch (final Throwable t) {
			throw new MinecraftVersion.UnsupportedServerVersionException("Cannot set ItemFlag " + this.toString() + " to item " + item.toString() + ".", t);
		}
	}
}