package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.multiversion.MinecraftVersion;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import com.ruthlessjailer.api.theseus.multiversion.XItemFlag;
import javafx.util.Pair;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * @author RuthlessJailer
 */
@Builder(builderClassName = "ItemStackCreator", builderMethodName = "of")
public class ItemBuilder {


	private final ItemStack                                item;
	private final ItemMeta                                 meta;
	private final Material                                 material;
	private final String                                   name;
	private final OfflinePlayer owner;
	@Builder.Default
	private final int                                      amount          = 1;
	@Builder.Default
	private final int                                      damage          = -1;
	@Singular
	private final List<String>                             lores;
	@Singular
	private final Map<Enchantment, Pair<Integer, Boolean>> enchantments;
	@Singular
	private final List<XItemFlag>                          flags;
	private final XColor                                   color;
	@Builder.Default
	private final boolean                                  unbreakable     = false;
	@Builder.Default
	private final boolean                                  hideAllFlags    = false;
	@Singular
	private final List<Pair<String, Object>>               nbts;
	@Builder.Default
	private final int                                      customModelData = 0;
	private final String                                   localizedName;


	public static ItemStackCreator of(@NonNull final ItemStack item) {
		return new ItemStackCreator().item(item);
	}

	public static ItemStackCreator of(@NonNull final Material material) {
		return new ItemStackCreator().material(material);
	}

	public static ItemStackCreator of(@NonNull final Material material, @NonNull final String name) {
		return of(material).name(name);
	}

	public static ItemStackCreator of(@NonNull final Material material, @NonNull final String name, @NonNull final String... lore) {
		return of(material, name).lores(Chat.colorize(Arrays.asList(lore)));
	}

	public ItemStack create() {
		Checks.verify(this.item != null || this.material != null, "Item or material must be set!");

		assert this.item != null || this.material != null;

		final ItemStack item = this.item != null
							   ? this.item.clone()
							   : new ItemStack(this.material, this.amount);

		if (this.material != null) {
			item.setType(this.material);
		}

		final ItemMeta meta = this.meta != null
							  ? this.meta.clone()
							  : item.getItemMeta() != null
								? item.getItemMeta()
								: Bukkit.getItemFactory().getItemMeta(item.getType());

		if (meta == null) {//no meta
			return item;
		}

		//run all methods that deal with the item before modifying meta (although not needed at the moment, this might prove useful later)

		this.flags.forEach(flag -> flag.applyToItem(item));

		if (this.hideAllFlags) {//apply all flags
			Arrays.stream(XItemFlag.values()).forEach(flag -> flag.applyToItem(item));
		}

		if (this.damage != -1) {
			if (MinecraftVersion.atLeast(MinecraftVersion.v1_13)) {
				if (meta instanceof Damageable) {
					((Damageable) meta).setDamage(this.damage);
				}
			} else {
				item.setDurability((short) this.damage);
			}
		}

		//modify meta

		this.enchantments.forEach((enchantment, pair) -> meta.addEnchant(enchantment, pair.getKey(), pair.getValue()));

		if (MinecraftVersion.atLeast(MinecraftVersion.v1_11)) {
			meta.setUnbreakable(this.unbreakable);
		} else {
			try {
				final Object spigot = ReflectUtil.invokeMethod(ReflectUtil.getMethod(ItemMeta.class, "spigot"), meta);
				ReflectUtil.invokeMethod(ReflectUtil.getMethod("org.bukkit.inventory.meta.ItemMeta$Spigot", "setUnbreakable", boolean.class),
										 spigot, this.unbreakable);
			} catch (final Throwable ignored) {}
		}

		//null checks so as not to modify the meta in case it was passed in
		if (this.name != null) {
			meta.setDisplayName(Chat.colorize(this.name));
		}
		if (!this.lores.isEmpty()) {
			meta.setLore(Chat.colorize(this.lores));
		}
		if (this.localizedName != null) {
			meta.setLocalizedName(Chat.colorize(this.localizedName));
		}
		if (meta instanceof LeatherArmorMeta && this.color != null) {
			final LeatherArmorMeta leather = (LeatherArmorMeta) meta;
			leather.setColor(this.color.getBukkitColor());
		}
		if(meta instanceof SkullMeta && this.owner != null){

				SkullMeta skullMeta = (SkullMeta) meta;
				skullMeta.setOwningPlayer(this.owner);
		}

		//version specific stuff

		if (MinecraftVersion.atLeast(MinecraftVersion.v1_14)) {
			meta.setCustomModelData(this.customModelData);
		}

		//finally, apply the meta and return the item

		item.setItemMeta(meta);

		return item;
	}

	public static final class ItemStackCreator {

		protected ItemStackCreator() {}

		public ItemStackCreator addEnchantment(@NonNull final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
			return this.enchantment(enchantment, new Pair<>(level, ignoreLevelRestriction));
		}

		public ItemStackCreator addEnchantments(@NonNull final Map<Enchantment, Integer> enchantments, final boolean ignoreLevelRestriction) {
			enchantments.forEach((e, i) -> this.enchantment(e, new Pair<>(i, ignoreLevelRestriction)));
			return this;
		}

		public ItemStackCreator addFlag(@NonNull final ItemFlag flag) {
			return this.flag(XItemFlag.fromItemFlag(flag));
		}

		public ItemStackCreator addFlags(@NonNull final Collection<ItemFlag> flags) {
			flags.forEach(f -> this.flag(XItemFlag.fromItemFlag(f)));
			return this;
		}

		public ItemStackCreator owner(final String name){
			if(name == null){
				this.owner = null;
				return this;
			}
			this.owner = Bukkit.getOfflinePlayer(name);
			return this;
		}

		public ItemStackCreator owner(final UUID uuid){
			if(uuid == null){
				this.owner = null;
				return this;
			}

			this.owner = Bukkit.getOfflinePlayer(uuid);
			return this;
		}

	}
}
