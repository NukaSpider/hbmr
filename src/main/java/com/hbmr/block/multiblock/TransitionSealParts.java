package com.hbmr.block.multiblock;

import net.minecraft.resources.ResourceLocation;

/**
 * Transition seal part BER model locations (local-space OBJs driven by Collada anim).
 * Paths are lowercase — ResourceLocation forbids uppercase.
 */
public final class TransitionSealParts {
	private static final String[] SAFE_NAMES = {
			"door_006", "ring_002", "door_004", "door_003", "ring_001", "door_008", "door_002", "door_005",
			"cylinder_011", "cylinder_010", "cylinder_009", "circle", "cylinder_008", "cylinder_007",
			"cube_006", "cylinder_005", "cylinder_003", "cylinder_001", "door", "frame"
	};

	private TransitionSealParts() {
	}

	public static ResourceLocation[] allBerModels() {
		ResourceLocation[] out = new ResourceLocation[SAFE_NAMES.length];
		for (int i = 0; i < SAFE_NAMES.length; i++) {
			out[i] = ResourceLocation.fromNamespaceAndPath("hbmr",
					"block/multiblock/transition_seal_" + SAFE_NAMES[i] + "_ber");
		}
		return out;
	}
}
