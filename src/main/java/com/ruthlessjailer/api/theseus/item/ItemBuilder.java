/*
package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.MinecraftVersion;
import javafx.util.Pair;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

*/
/**
 * @author Vadim Hagedorn
 *//*

@Builder(builderClassName = "ItemStackCreator", buildMethodName = "of")
public class ItemBuilder {

	private final Material                                 material;
	private final String                                   name;
	@Builder.Default
	private final int                                      amount       = 1;
	@Builder.Default
	private final int                                      damage       = -1;
	@Singular
	private final List<String>                             lores;
	@Singular
	private final Map<Enchantment, Pair<Integer, Boolean>> enchantments;
	@Singular
	private final List<XItemFlag>                          flags;
	private final XColor                                   color;
	@Builder.Default
	private final boolean                                  unbreakable  = false;
	@Builder.Default
	private final boolean                                  hideAllFlags = false;
	private final List<Pair<String, Object>>               nbt;


	public static ItemStackCreator of(@NonNull final ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		final ItemStackCreator builder = of(item.getType())
				.addEnchantments(item.getEnchantments(), true)
				.amount(item.getAmount())
				.damage(item.getDurability());//for older versions
		return item.hasItemMeta()
			   ? builder
					   .lores(Chat.colorize(meta.getLore()))
					   .name(Chat.colorize(meta.getDisplayName()))
					   .damage(meta instanceof Damageable ? ((Damageable) meta).getDamage() : -1)
					   .addFlags(meta.getItemFlags())
			   : builder;
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

		public ItemStack build() {

			Checks.nullCheck(this.material, "Material must be set!");

			final ItemStack item = new ItemStack(this.material, this.amount);
			final ItemMeta  meta = item.getItemMeta();

			if (XMaterial.isAir(this.material)) {//no meta
				return item;
			}

			assert meta != null;//not air; all else has meta

			if (MinecraftVersion.atLeast(MinecraftVersion.v1_13)) {

				if (this.color != null && !this.material.name().startsWith("LEATHER") && XMaterial.isColorable(this.material)) {
					final String color = this.color.getDyeColor().name();

					for (final String colorable : XMaterial.COLORABLE) {
						if (this.material.name().endsWith("_".concat(colorable))) {
							item.setType(Material.getMaterial(color + "_".concat(colorable)));
						}
					}

				}


			} else {//legacy

			}


			return null;
		}

	}

}
*/
