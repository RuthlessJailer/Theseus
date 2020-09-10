package com.ruthlessjailer.api.theseus.item;

import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.MinecraftVersion;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ruthlessjailer.api.theseus.MinecraftVersion.*;
import static com.ruthlessjailer.api.theseus.ReflectUtil.getEnum;

/**
 * Inspired by and adapted from:
 * https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/XMaterial.java
 * <p>
 * Minecraft Wiki was of significant help:
 * https://minecraft.gamepedia.com/
 *
 * @author Vadim Hagedorn
 */
@Getter
public enum XMaterial {

	//acacia added in 1.7
	ACACIA_BOAT(v1_9, "BOAT_ACACIA", "BOAT"),
	ACACIA_BUTTON(v1_13, "WOOD_BUTTON"),
	ACACIA_DOOR(v1_8, "ACACIA_DOOR", "ACACIA_DOOR_ITEM", "WOOD_DOOR", "WOODEN_DOOR"),
	ACACIA_FENCE(v1_8, "FENCE"),
	ACACIA_FENCE_GATE(v1_8, "FENCE_GATE"),
	ACACIA_LEAVES(v1_7, "LEAVES_2", "LEAVES"),
	ACACIA_LOG(v1_7, "LOG_2", "LOG"),
	ACACIA_PLANKS(v1_7, 4, "WOOD"),
	ACACIA_PRESSURE_PLATE(v1_7, 4, "WOOD_PLATE"),
	ACACIA_SAPLING(v1_7, 4, "SAPLING"),
	ACACIA_SIGN(v1_14, "SIGN_POST", "SIGN", "STANDING_SIGN"),
	ACACIA_SLAB(v1_7, 4, "WOOD_STEP", "WOODEN_SLAB", "WOOD_DOUBLE_STEP"),
	ACACIA_STAIRS(v1_7, "WOOD_STAIRS"),
	ACACIA_TRAPDOOR(v1_7, "TRAP_DOOR"),
	ACACIA_WALL_SIGN(v1_7, 4, "WALL_SIGN"),
	ACACIA_WOOD(v1_7, "LOG_2", "LOG"),
	ACTIVATOR_RAIL(v1_5, "RAIL"),
	/**
	 * @see #CAVE_AIR
	 * @see #VOID_AIR
	 */
	AIR(v1_3_OR_OLDER),
	ALLIUM(v1_7, 2, "RED_ROSE"),
	ANCIENT_DEBRIS(v1_16, "QUARTZ_ORE", "NETHERRACK"),
	ANDESITE(v1_8, 5, "STONE"),
	ANDESITE_SLAB(v1_14, 5, "STEP", "DOUBLE_STEP", "COBBLESTONE_SLAB"),
	ANDESITE_STAIRS(v1_14, "COBBLESTONE_STAIRS"),
	ANDESITE_WALL(v1_14, "COBBLESTONE_WALL"),
	ANVIL(v1_4),
	APPLE(v1_3_OR_OLDER),
	ARMOR_STAND(v1_8),
	ARROW(v1_3_OR_OLDER),
	ATTACHED_MELON_STEM(v1_8, 7, "MELON_STEM"),
	ATTACHED_PUMPKIN_STEM(v1_8, 7, "PUMPKIN_STEM"),
	AZURE_BLUET(v1_7, 7, "RED_ROSE"),
	BAKED_POTATO(v1_4),
	BAMBOO(v1_14, "SUGAR_CANE"),
	BAMBOO_SAPLING(v1_14, "SUGAR_CANE"),
	BARREL(v1_14, "CHEST"),
	BARRIER(v1_8, "GLASS"),


	//-----

	CAVE_AIR(v1_13),
	VOID_AIR(v1_13);


	public static final List<String> COLORABLE = Collections.unmodifiableList(Arrays.asList(
			"BANNER", "BED", "CARPET", "CONCRETE", "GLAZED_TERRACOTTA", "SHULKER_BOX",
			"STAINED_GLASS", "STAINED_GLASS_PANE", "TERRACOTTA", "WALL_BANNER", "WOOL"));

	private final String[]         legacyNames;
	//TODO: add all materials.............
	private final byte             data;
	private final MinecraftVersion added;


	XMaterial(@NonNull final MinecraftVersion added, @NonNull final String... legacyNames) {
		this(added, 0, legacyNames);
	}

	XMaterial(@NonNull final MinecraftVersion added, final int data, @NonNull final String... legacyNames) {
		this.added       = added;
		this.data        = (byte) data;
		this.legacyNames = legacyNames == null || legacyNames.length == 0 ? Common.asArray("STONE") : legacyNames;
	}

	/**
	 * Returns {@code true} if the material is air, i.e. it does not have {@link org.bukkit.inventory.meta.ItemMeta}, or {@code false} if it isn't.
	 *
	 * @param material the material to check
	 *
	 * @return true if the material is one of the following (as of 1.16): AIR, CAVE_AIR, VOID_AIR, LEGACY_AIR
	 */
	public static boolean isAir(@NonNull final Material material) {
		return material.name().equals("AIR") || material.name().endsWith("_AIR");
	}//TODO add all materials and change these methods to be XMaterial instead of Material

	/**
	 * Returns {@code true} if the material is colorable, i.e. if it is included in the COLORABLE list, or {@code false} if it isn't.
	 *
	 * @param material the material to check
	 *
	 * @return true if the material's name is included in the COLORABLE list
	 */
	public static boolean isColorable(@NonNull final Material material) {
		for (final String colorable : COLORABLE) {
			if (material.name().endsWith("_".concat(colorable))) {
				return true;
			}
		}
		return false;
	}

	public String getFallback() {
		return this.legacyNames[0];
	}

	public Material toMaterial() {
		return getEnum(Material.class, this.name(), this.legacyNames);
	}

}
