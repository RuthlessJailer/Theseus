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

public final class ItemBuilder {

	static final UUID BLANK_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static UnmadeItem create(final Material material) {
		return new UnmadeItem(material);
	}

	public static UnmadeItem edit(final ItemStack item) {
		return new UnmadeItem(item);
	}

	public static ConstructedItem view(final ItemStack item) {
		return new ConstructedItem(item);
	}


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

		/*
		public UnmadeItem amount(final int amount) {
			this.amount = amount;
			return this;
		}

		public UnmadeItem name(final String name) {
			this.name = Chat.colorize(name);
			return this;
		}

		public UnmadeItem lore(final String... lore) {
			this.lore = new ArrayList<>();
			for (final String s : lore) {
				this.lore.add(Chat.colorize(s));
			}
			return this;
		}

		public UnmadeItem appendLore(final String... lore){
			for (final String s : lore) {
				this.lore.add(Chat.colorize(s));
			}
			return this;
		}

		public UnmadeItem addItemFlags(final ItemFlag... flags) {
			this.flags.addAll(Arrays.asList(flags));
			return this;
		}

		public UnmadeItem removeItemFlags(final ItemFlag... flags) {
			this.flags.removeAll(Arrays.asList(flags));
			return this;
		}

		public UnmadeItem setUnbreakable(final boolean unbreakable) {
			this.unbreakable = unbreakable;
			return this;
		}

		public UnmadeItem skullOwner(final UUID uuid) {
			this.skullOwner = uuid;
			return this;
		}

		public UnmadeItem skullOwner(final String owner) {
			CompletableFuture.runAsync(() -> {
				final Player player = Bukkit.getOfflinePlayer(owner).getPlayer();
				this.skullOwner = player != null ? player.getUniqueId() : ItemBuilder.BLANK_UUID;
			});
			return this;
		}

		public UnmadeItem addEnchantment(final Enchantment enchantment) {
			return this.addEnchantment(enchantment, 1);
		}

		public UnmadeItem addEnchantment(final Enchantment enchantment, final int level) {
			return this.addEnchantment(enchantment, level, true);
		}

		public UnmadeItem addEnchantment(final Enchantment enchantment, final int level,
										 final boolean ignoreLevelRestriction) {
			this.enchantments.put(enchantment, new Pair<>(level, ignoreLevelRestriction));
			return this;
		}

		public UnmadeItem removeEnchantment(final Enchantment enchantment) {
			this.enchantments.remove(enchantment);
			return this;
		}*/
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

		public boolean hasEnchantment(final Enchantment enchantment) {
			return this.enchantments.containsKey(
					enchantment);
		}

		public Integer getEnchantmentLevel(final Enchantment enchantment) {
			return this.enchantments.get(enchantment) != null
				   ? this.enchantments.get(enchantment).getKey()
				   : 0;
		}

	}

}
