package com.ruthlessjailer.api.theseus.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vadim Hagedorn
 */
@AllArgsConstructor
@Getter
public enum XMaterial {

	;//TODO: add all materials.............


	public static final List<String> COLORABLE = Collections.unmodifiableList(Arrays.asList(
			"BANNER", "BED", "CARPET", "CONCRETE", "GLAZED_TERRACOTTA", "SHULKER_BOX",
			"STAINED_GLASS", "STAINED_GLASS_PANE", "TERRACOTTA", "WALL_BANNER", "WOOL"));

	private final Material material;

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

}
