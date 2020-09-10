package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.MinecraftVersion;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Multi-version {@link DyeColor} and {@link ChatColor} link.
 *
 * @author Vadim Hagedorn
 * @see ChatColor
 * @see DyeColor
 */
public enum XColor {


	BLACK(DyeColor.BLACK),
	DARK_BLUE(DyeColor.BLUE),
	DARK_GREEN(DyeColor.GREEN),
	DARK_AQUA(DyeColor.CYAN),
	DARK_PURPLE(DyeColor.PURPLE),
	GOLD(DyeColor.ORANGE),
	GRAY(ReflectUtil.getEnum(DyeColor.class, "LIGHT_GRAY", "SILVER"), ChatColor.GRAY, "SILVER"),
	DARK_GRAY(DyeColor.GRAY),
	BLUE(DyeColor.BLUE),
	GREEN(DyeColor.LIME),
	AQUA(DyeColor.LIGHT_BLUE),
	RED(DyeColor.RED),
	LIGHT_PURPLE(DyeColor.MAGENTA),
	YELLOW(DyeColor.YELLOW),
	WHITE(DyeColor.WHITE),

	BROWN(DyeColor.BROWN, ChatColor.GOLD),
	PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE);

	@Getter
	private final ChatColor chatColor;

	@Getter
	private final DyeColor dyeColor;

	private final String legacyName;

	XColor(final DyeColor dyeColor) {
		this(dyeColor, null);
	}

	XColor(final DyeColor dyeColor, final ChatColor chatColor) {
		this(dyeColor, chatColor, null);
	}

	XColor(final DyeColor dyeColor, final ChatColor chatColor, final String legacyName) {
		this.dyeColor   = dyeColor;
		this.chatColor  = chatColor != null ? chatColor : ChatColor.valueOf(this.name());
		this.legacyName = legacyName == null ? "" : legacyName;
	}

	public static XColor fromChatColor(final ChatColor chatColor) {
		for (final XColor x : values()) {
			if (x.chatColor == chatColor || x.legacyName.equals(chatColor.name())) {
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color " + chatColor.name() + ".");
	}

	public static XColor fromBungee(final net.md_5.bungee.api.ChatColor chatColor) {
		for (final XColor x : values()) {
			if (x.getBungee() == chatColor || x.legacyName.equals(chatColor.name())) {
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color " + chatColor.name() + ".");
	}

	public static XColor fromDyeColor(final DyeColor dyeColor) {
		for (final XColor x : values()) {
			if (x.dyeColor == dyeColor || x.legacyName.equals(dyeColor.name())) {
				return x;
			}
		}
		throw new IllegalStateException("Error parsing color " + dyeColor.name() + ".");
	}

	public static XColor fromName(final String name) {
		final String caps = name.toUpperCase();
		for (final XColor x : values()) {
			if (x.name().equals(caps) ||
				x.chatColor.name().equals(caps) ||
				x.dyeColor.name().equals(caps) ||
				x.legacyName
						.equals(name)) {
				return x;
			}
		}
		throw new IllegalArgumentException("No XColor found for " + caps + " .");
	}

	public static ChatColor toChatColor(final DyeColor dyeColor) { return fromDyeColor(dyeColor).chatColor; }

	public static DyeColor toDyeColor(final ChatColor chatColor) { return fromChatColor(chatColor).dyeColor; }

	/**
	 * Converts to a material. 1.13 and above only.
	 *
	 * @param color the {@link XColor} for which conversion in needed
	 * @param name  the white variant of the material, e.g. WHITE_WOOL (1.13+)
	 *
	 * @return the found {@link Material} or {@code null}
	 *
	 * @see XColor#toItem(XColor, String) for 1.12-
	 */
	public static Material toMaterial(final XColor color, final String name) {

		if (color == null || name == null) {
			return null;
		}

		if (MinecraftVersion.lessThan(MinecraftVersion.v1_13)) {
			return null;
		}

		final String xname     = color.name();
		final String dyeName   = color.dyeColor.name();
		final String colorName = color.chatColor.name();

		final String parsed       = name.toUpperCase();
		String       materialName = parsed.replace("WHITE", xname);//try the xcolor name
		Material     material     = Material.getMaterial(materialName);

		if (material == null) {//try dye color name
			materialName = parsed.replace("WHITE", dyeName);
			material     = Material.getMaterial(materialName);
			if (material == null) {
				material = Material.getMaterial(materialName, true);
			}
		}

		if (material == null) {//try chat color name (last-ditch effort)
			materialName = parsed.replace("WHITE", colorName);
			material     = Material.getMaterial(materialName);
			if (material == null) {
				material = Material.getMaterial(materialName, true);
			}
		}

		return material;

	}

	/**
	 * Converts to an {@link com.ruthlessjailer.api.theseus.item.ItemBuilder.ItemStackCreator}.
	 *
	 * @param color the {@link XColor} for which conversion in needed
	 * @param name  the white variant of the material, e.g. WHITE_WOOL (1.13+), or the name of the base material, e.g. WOOL (1.12-)
	 *
	 * @return the created {@link com.ruthlessjailer.api.theseus.item.ItemBuilder.ItemStackCreator} with the found material or {@link Material#WHITE_WOOL} or
	 *        {@link Material#LEGACY_WOOL} (Material.WOOL)
	 *
	 * @see XColor#toMaterial(XColor, String) 1.13+ exclusive
	 */
	public static void applyTo(final ItemStack item, final XColor color, final String name) {
		final Material def = ReflectUtil.getEnum(Material.class, "WHITE_WOOL", "WOOL");

		if (color == null || name == null || item == null || !item.hasItemMeta()) {
			return;
		}

		if (MinecraftVersion.atLeast(MinecraftVersion.v1_13)) {
			final Material material = toMaterial(color, name);
			item.setType(material == null ? def : material);
			return;
		}

		if (MinecraftVersion.atMost(MinecraftVersion.v1_12)) {
			final Material material = ReflectUtil.getEnum(Material.class, name);
			return material == null ? def : ItemBuilder.of(material);
		}


	}

	public net.md_5.bungee.api.ChatColor getBungee() { return this.chatColor.asBungee(); }

}
