package com.ruthlessjailer.api.theseus.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.MinecraftVersion;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ruthlessjailer.api.theseus.MinecraftVersion.*;

/**
 * Inspired by and adapted from:
 * https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/XMaterial.java
 * <p>
 * Minecraft Wiki was of significant help:
 * https://minecraft.gamepedia.com/
 *
 * @author RuthlessJailer
 */
@Getter
public enum XMaterial {

	//<editor-fold desc="Materials" defaultstate="collapsed">
	ACACIA_BOAT(v1_9, "BOAT_ACACIA", "BOAT"),//acacia added in 1.7
	ACACIA_BUTTON(v1_13, "WOOD_BUTTON"),
	ACACIA_DOOR(v1_8, "ACACIA_DOOR", "ACACIA_DOOR_ITEM", "WOOD_DOOR", "WOODEN_DOOR"),
	ACACIA_FENCE(v1_8, "FENCE"),//fence variants added in 1.8
	ACACIA_FENCE_GATE(v1_8, "FENCE_GATE"),
	ACACIA_LEAVES(v1_7, "LEAVES_2", "LEAVES"),
	ACACIA_LOG(v1_7, "LOG_2", "LOG"),
	ACACIA_PLANKS(v1_7, 4, "WOOD"),
	ACACIA_PRESSURE_PLATE(v1_13, "WOOD_PLATE"),
	ACACIA_SAPLING(v1_7, 4, "SAPLING"),
	ACACIA_SIGN(v1_14, "SIGN_POST", "SIGN", "STANDING_SIGN"),
	ACACIA_SLAB(v1_7, 4, "WOOD_STEP", "WOODEN_SLAB", "WOOD_DOUBLE_STEP"),
	ACACIA_STAIRS(v1_7, "WOOD_STAIRS"),
	ACACIA_TRAPDOOR(v1_13, "TRAP_DOOR"),//trapdoor types added in 1.13
	ACACIA_WALL_SIGN(v1_7, 4, "WALL_SIGN"),
	ACACIA_WOOD(v1_7, "LOG_2", "LOG"),
	ACTIVATOR_RAIL(v1_5, "RAIL"),
	/**
	 * https://minecraft.gamepedia.com/Air
	 *
	 * @see #CAVE_AIR
	 * @see #VOID_AIR
	 */
	AIR(v1_3_OR_OLDER),
	ALLIUM(v1_7, 2, "RED_ROSE"),
	ANCIENT_DEBRIS(v1_16, "QUARTZ_ORE", "NETHERRACK"),
	ANDESITE(v1_8, 5),
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
	BASALT(v1_16, "COBBLESTONE"),
	BAT_SPAWN_EGG(v1_4, 65, "MONSTER_EGG"),
	BEACON(v1_4, "GLASS"),
	BEDROCK(v1_3_OR_OLDER),
	BEEF(v1_3_OR_OLDER, "RAW_BEEF"),
	BEEHIVE(v1_15),
	/**
	 * The block variant.
	 *
	 * @see #BEETROOTS for item variant
	 */
	BEETROOT(v1_9, "BEETROOT_BLOCK"),//TODO: unsure
	/**
	 * The item variant.
	 *
	 * @see #BEETROOT for block variant
	 */
	BEETROOTS(v1_9, "BEETROOT"),//TODO: unsure
	BEETROOT_SEEDS(v1_9, "SEEDS", "WHEAT_SEEDS"),
	BEETROOT_SOUP(v1_9, "MUSHROOM_STEW"),
	BEE_NEST(v1_15, 4, "YELLOW_WOOL", "WOOL"),//yellow wool
	BEE_SPAWN_EGG(v1_15, "SPAWN_EGG"),
	BELL(v1_14),
	BIRCH_BOAT(v1_9, 2, "BOAT_BIRCH", "BOAT"),
	BIRCH_BUTTON(v1_13, "WOOD_BUTTON"),
	BIRCH_DOOR(v1_8, "BIRCH_DOOR", "BIRCH_DOOR_ITEM", "WOOD_DOOR", "WOODEN_DOOR"),
	BIRCH_FENCE(v1_8, "FENCE"),
	BIRCH_FENCE_GATE(v1_8, "FENCE_GATE"),
	BIRCH_LEAVES(v1_3_OR_OLDER, 2, "LEAVES"),
	BIRCH_LOG(v1_3_OR_OLDER, 2, "LOG"),//birch added very early
	BIRCH_PLANKS(v1_3_OR_OLDER, 2, "WOOD"),
	BIRCH_PRESSURE_PLATE(v1_13, "WOOD_PLATE"),//pressure plate types added in 1.13
	BIRCH_SAPLING(v1_5, 2, "SAPLING"),
	BIRCH_SIGN(v1_14, "SIGN_POST", "SIGN"),
	BIRCH_SLAB(v1_3_OR_OLDER, 2, "WOOD_STEP", "WOODEN_SLAB", "WOOD_DOUBLE_STEP"),
	BIRCH_STAIRS(v1_3_OR_OLDER, "BIRCH_WOOD_STAIRS"),
	BIRCH_TRAPDOOR(v1_13, "TRAP_DOOR"),
	BIRCH_WALL_SIGN(v1_13, "WALL_SIGN"),
	BIRCH_WOOD(v1_3_OR_OLDER, 2, "LOG"),
	BLACKSTONE(v1_16),
	BLACKSTONE_SLAB(v1_16, "STEP", "DOUBLE_STEP", "COBBLESTONE_SLAB"),
	BLACKSTONE_STAIRS(v1_16, "COBBLESTONE_STAIRS"),
	BLACKSTONE_WALL(v1_16, "COBBLESTONE_WALL", "COBBLE_WALL"),
	BLACK_BANNER(v1_8, 0, "STANDING_BANNER", "BANNER"),//banners added in 1.8, 0 is black for banners
	BLACK_BED(v1_12, 15, "BED_BLOCK", "BED"),//dyed beds added in 1.12, beds added very long ago
	BLACK_CARPET(v1_6, 15, "CARPET"),
	BLACK_CONCRETE(v1_12, 15, "CONCRETE"),
	BLACK_CONCRETE_POWDER(v1_12, 15, "CONCRETE_POWDER"),
	BLACK_DYE(v1_14, "INK_SAC", "INK_SACK"),
	BLACK_GLAZED_TERRACOTTA(v1_12, 15, "STAINED_CLAY", "HARD_CLAY", "BLACK_TERRACOTTA"),//TODO: implement cross-referencing (legacy name sharing)?
	BLACK_SHULKER_BOX(v1_11, "CHEST"),
	BLACK_STAINED_GLASS(v1_7, 15, "STAINED_GLASS", "GLASS"),
	BLACK_STAINED_GLASS_PANE(v1_7, 15, "STAINED_GLASS_PANE", "THIN_GLASS"),
	BLACK_TERRACOTTA(v1_12, 15, "HARD_CLAY", "STAINED_CLAY"),
	BLACK_WALL_BANNER(v1_8, 0, "WALL_BANNER"),
	BLACK_WOOL(v1_3_OR_OLDER, 15, "WOOL"),
	BLAST_FURNACE(v1_14, "FURNACE"),
	BLAZE_POWDER(v1_3_OR_OLDER),
	BLAZE_ROD(v1_3_OR_OLDER, "STICK"),
	BLAZE_SPAWN_EGG(v1_3_OR_OLDER, 61, "SPAWN_EGG", "MONSTER_EGG"),
	BLUE_BANNER(v1_8, 4, "STANDING_BANNER", "BANNER"),
	BLUE_BED(v1_12, 11, "BED_BLOCK", "BED"),
	BLUE_CARPET(v1_6, 11, "CARPET"),
	BLUE_CONCRETE(v1_12, 11, "CONCRETE"),
	BLUE_CONCRETE_POWDER(v1_12, 11, "CONCRETE_POWDER"),
	BLUE_DYE(v1_13, 4, "INK_SACK", "INK_SAC"),
	BLUE_GLAZED_TERRACOTTA(v1_12, 11, "HARD_CLAY", "STAINED_CLAY", "BLUE_TERRACOTTA"),
	BLUE_ICE(v1_13, 0, "PACKED_ICE", "ICE"),

	/**
	 * @deprecated here solely for purpose of categorizing; has no functionality
	 */
	@Deprecated
	BANNER(v1_16),
	CAVE_AIR(v1_13),
	VOID_AIR(v1_13);
	//</editor-fold>

	public static final  List<String>               COLORABLE      = Collections.unmodifiableList(Arrays.asList(//TODO lame fix pls
																												"BANNER", "BED", "CARPET", "CONCRETE", "GLAZED_TERRACOTTA", "SHULKER_BOX",
																												"STAINED_GLASS", "STAINED_GLASS_PANE", "TERRACOTTA", "WALL_BANNER", "WOOL"));
	private static final Cache<String, XMaterial>   NAME_CACHE     = CacheBuilder.newBuilder().build();
	private static final Cache<XMaterial, Material> MATERIAL_CACHE = CacheBuilder.newBuilder().build();
	private static final boolean                    ISFLAT         = MinecraftVersion.atLeast(v1_13);

	static {//populate name cache
		for (final XMaterial xMaterial : values()) { NAME_CACHE.put(xMaterial.name(), xMaterial); }
	}

	private final String[]         legacyNames;
	private final byte             data;
	private final MinecraftVersion added;

	//<editor-fold desc="Constructors" defaultstate="collapsed">
	XMaterial(@NonNull final MinecraftVersion added, @NonNull final String... legacyNames) {
		this(added, 0, legacyNames);
	}

	XMaterial(@NonNull final MinecraftVersion added, final int data, @NonNull final String... legacyNames) {
		this.added       = added;
		this.data        = (byte) data;
		this.legacyNames = legacyNames == null || legacyNames.length == 0 ? Common.asArray("STONE") : Common.append(legacyNames, "STONE");
	}
	//</editor-fold>

	//<editor-fold desc="Static Methods" defaultstate="collapsed">

	/**
	 * Returns {@code true} if the material is air, i.e. it does not have {@link org.bukkit.inventory.meta.ItemMeta}, or {@code false} if it isn't.
	 *
	 * @param material the material to check
	 *
	 * @return true if the material is one of the following: AIR, CAVE_AIR, VOID_AIR, LEGACY_AIR
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
	//</editor-fold>

	public static XMaterial getXMaterial(@NonNull final String name) {
		return NAME_CACHE.getIfPresent(name);
	}

	//<editor-fold desc="Getters" defaultstate="collapsed">
	public String getFallback() {
		return this.legacyNames[0];
	}

	public Material toMaterial() {
		Material cached = MATERIAL_CACHE.getIfPresent(this);

		if (cached == null) {//first call; populate
			cached = ReflectUtil.getEnum(Material.class, this.name(), this.legacyNames);

			MATERIAL_CACHE.put(this, cached);//save found material
		}

		return cached;
	}

	public ItemStack toItemStack() {
		final Material material = toMaterial();
		return ISFLAT
			   ? new ItemStack(material)
			   : new ItemStack(material, 1, this.data);
	}
	//</editor-fold>

}