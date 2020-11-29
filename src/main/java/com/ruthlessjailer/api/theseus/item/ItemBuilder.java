package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.multiversion.XColor;
import com.ruthlessjailer.api.theseus.multiversion.XItemFlag;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author RuthlessJailer
 */
@Builder(builderClassName = "ItemStackCreator", builderMethodName = "of")
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

	public ItemStack create() {
		Checks.nullCheck(this.material, "Material must be set!");

		final ItemStack item = new ItemStack(this.material, this.amount);

		if (!item.hasItemMeta()) {//no meta
			return item;
		}

		final ItemMeta meta = item.getItemMeta();

		assert meta != null;//not air; all else has meta

		if (!this.flags.isEmpty()) {//item flags
			this.flags.forEach(flag -> flag.applyToItem(item));
		}

		if (this.hideAllFlags) {//apply all flags
			Arrays.stream(XItemFlag.values()).forEach(flag -> flag.applyToItem(item));
		}

		if (!this.enchantments.isEmpty()) {//enchantments
			this.enchantments.forEach((enchantment, pair) -> meta.addEnchant(enchantment, pair.getKey(), pair.getValue()));
		}

		meta.setUnbreakable(this.unbreakable);

		if (item.getType().name().contains("LEATHER")) {
			if (meta instanceof LeatherArmorMeta) {
				final LeatherArmorMeta leather = (LeatherArmorMeta) meta;
				leather.setColor(this.color.getDyeColor().getColor());
			}
		}

		return null;

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

	}
}
