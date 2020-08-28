package com.ruthlessjailer.api.theseus;

import javafx.util.Pair;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author Vadim Hagedorn
 */
@Builder
public class ItemCreator {

	private final ItemStack item;

	private final Material material;

	@Builder.Default
	private final int amount = 1;

	@Builder.Default
	private final int damage = -1;

	private final String name;

	@Singular
	private final List<String> lores;

	@Singular
	private final Map<Enchantment, Pair<Integer, Boolean>> enchantments;

	@Singular
	private final List<ItemFlag> flags;

	private final boolean unbreakable;

}
