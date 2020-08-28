package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.Checks;
import javafx.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * @author Vadim Hagedorn
 * @see com.ruthlessjailer.api.theseus.ItemCreator
 * @deprecated see {@link com.ruthlessjailer.api.theseus.ItemCreator}
 */
@Deprecated
public final class ItemBuilder {

	public static final UUID BLANK_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Deprecated
	public static UnmadeItem create(final Material material) {
		return new UnmadeItem(material);
	}

	@Deprecated
	public static UnmadeItem edit(final ItemStack item) {
		return new UnmadeItem(item);
	}

	@Deprecated
	public static ConstructedItem view(final ItemStack item) {
		return new ConstructedItem(item);
	}


	@Deprecated
	public static final class UnmadeItem {

		private final Material                                 material;
		private final Map<Enchantment, Pair<Integer, Boolean>> enchantments = new HashMap<>();
		private       int                                      amount;
		private       String                                   name;
		private       List<String>                             lore;
		private       Set<ItemFlag>                            flags;
		private       boolean                                  unbreakable;
		private       UUID                                     skullOwner;

		UnmadeItem(final Material material) {
			this.material = material;
		}

		UnmadeItem(final ItemStack item) {
			final ItemMeta meta = item.getItemMeta();
			this.material = item.getType();
			this.amount   = item.getAmount();
			this.name     = meta.getDisplayName();
			this.lore     = meta.getLore();

			for (final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
				final Enchantment            key      = entry.getKey();
				final Integer                value    = entry.getValue();
				final Pair<Integer, Boolean> newValue = new Pair<>(value, true);
				this.enchantments.put(key, newValue);
			}

			this.flags = meta.getItemFlags();
		}

		@Deprecated
		public ItemStack make() {

			final ItemStack item = new ItemStack(Checks.nullCheck(this.material, "Material must be set."));
			final ItemMeta  meta = item.getItemMeta();

			meta.setDisplayName(this.name == null
								? Bukkit.getItemFactory().getItemMeta(this.material).getDisplayName()
								: this.name);

			if (this.lore != null) {
				meta.setLore(this.lore);
			}

			meta.setUnbreakable(this.unbreakable);

			for (final ItemFlag flag : this.flags) {
				meta.addItemFlags(flag);
			}

			for (final Map.Entry<Enchantment, Pair<Integer, Boolean>> entry : this.enchantments.entrySet()) {
				final Enchantment            key   = entry.getKey();
				final Pair<Integer, Boolean> value = entry.getValue();
				meta.addEnchant(key, value.getKey(), value.getValue());
			}

			item.setAmount(this.amount);
			item.setItemMeta(meta);

			return item;
		}
	}

	@Getter
	@Deprecated
	public static final class ConstructedItem {

		private final Material                                 material;
		private final int                                      amount;
		private final String                                   name;
		private final List<String>                             lore;
		private final Map<Enchantment, Pair<Integer, Boolean>> enchantments = new HashMap<>();
		private final Set<ItemFlag>                            flags;
		private final UUID                                     skullOwner;
		private final boolean                                  unbreakable;

		ConstructedItem(final ItemStack item) {
			final ItemMeta meta = item.getItemMeta();

			if (item.hasItemMeta()) {
				assert meta != null;

				this.name        = meta.getDisplayName();
				this.lore        = meta.getLore();
				this.flags       = meta.getItemFlags();
				this.unbreakable = meta.isUnbreakable();

				for (final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
					final Enchantment            key      = entry.getKey();
					final Integer                value    = entry.getValue();
					final Pair<Integer, Boolean> newValue = new Pair<>(value, true);
					this.enchantments.put(key, newValue);
				}

				if (meta instanceof SkullMeta) {
					final SkullMeta skullMeta = (SkullMeta) meta;
					if (skullMeta.hasOwner()) {
						this.skullOwner = skullMeta.getOwningPlayer().getUniqueId();
					} else {
						this.skullOwner = ItemBuilder.BLANK_UUID;
					}
				} else {
					this.skullOwner = ItemBuilder.BLANK_UUID;
				}
			} else {
				this.name        = null;
				this.lore        = new ArrayList<>();
				this.flags       = new HashSet<>();
				this.unbreakable = false;
				this.skullOwner  = ItemBuilder.BLANK_UUID;
			}

			this.amount   = item.getAmount();
			this.material = item.getType();

		}

		@Deprecated
		public boolean hasEnchantment(final Enchantment enchantment) {
			return this.enchantments.containsKey(
					enchantment);
		}

		@Deprecated
		public Integer getEnchantmentLevel(final Enchantment enchantment) {
			return this.enchantments.get(enchantment) != null
				   ? this.enchantments.get(enchantment).getKey()
				   : 0;
		}

	}

}
