package com.ruthlessjailer.api.theseus.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * @author Vadim Hagedorn
 */
public abstract class EnchantmentBase extends Enchantment {
	public EnchantmentBase(final NamespacedKey key) {
		super(key);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 0;
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(final Enchantment other) {
		return false;
	}

	@Override
	public boolean canEnchantItem(final ItemStack item) {
		return false;
	}
}
