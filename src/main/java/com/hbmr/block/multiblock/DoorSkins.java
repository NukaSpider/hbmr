package com.hbmr.block.multiblock;

import net.minecraft.resources.ResourceLocation;

/**
 * Skin variants for fire / vault doors (matches 1.7.10 {@code DoorDecl} / {@code RenderVaultDoor}).
 */
public final class DoorSkins {
	public static final int FIRE_COUNT = 5;
	public static final int VAULT_COUNT = 7;
	public static final int SECURE_COUNT = 4;
	public static final int CONTAINMENT_COUNT = 3;
	public static final int AIRLOCK_COUNT = 3;
	public static final int WATER_COUNT = 2;

	private DoorSkins() {
	}

	public static int skinCount(StructureType type) {
		return switch (type) {
			case FIRE_DOOR -> FIRE_COUNT;
			case VT_BLAST_DOOR -> VAULT_COUNT;
			case SECURE_ACCESS_DOOR -> SECURE_COUNT;
			case QE_CONTAINMENT_DOOR -> CONTAINMENT_COUNT;
			case ROUND_AIRLOCK_DOOR -> AIRLOCK_COUNT;
			case WATER_DOOR -> WATER_COUNT;
			default -> 0;
		};
	}

	public static boolean hasSkins(StructureType type) {
		return skinCount(type) > 0;
	}

	public static ResourceLocation fireFrame(int skin) {
		return ber("fire_door_frame_ber_" + clamp(skin, FIRE_COUNT));
	}

	public static ResourceLocation fireDoor(int skin) {
		return ber("fire_door_door_ber_" + clamp(skin, FIRE_COUNT));
	}

	public static ResourceLocation vaultFrame(int skin) {
		return ber("vt_blast_door_frame_ber_" + clamp(skin, VAULT_COUNT));
	}

	public static ResourceLocation vaultDoor(int skin) {
		return ber("vt_blast_door_door_ber_" + clamp(skin, VAULT_COUNT));
	}

	public static ResourceLocation vaultLabel(int skin) {
		return ber("vt_blast_door_label_ber_" + clamp(skin, VAULT_COUNT));
	}

	public static ResourceLocation secureFrame(int skin) {
		return ber("secure_access_door_frame_ber_" + clamp(skin, SECURE_COUNT));
	}

	public static ResourceLocation secureDoor(int skin) {
		return ber("secure_access_door_door_ber_" + clamp(skin, SECURE_COUNT));
	}

	public static ResourceLocation containmentFrame(int skin) {
		return ber("qe_containment_door_frame_ber_" + clamp(skin, CONTAINMENT_COUNT));
	}

	public static ResourceLocation containmentDoor(int skin) {
		return ber("qe_containment_door_door_ber_" + clamp(skin, CONTAINMENT_COUNT));
	}

	public static ResourceLocation airlockFrame(int skin) {
		return ber("round_airlock_door_frame_ber_" + clamp(skin, AIRLOCK_COUNT));
	}

	public static ResourceLocation airlockLeft(int skin) {
		return ber("round_airlock_door_left_ber_" + clamp(skin, AIRLOCK_COUNT));
	}

	public static ResourceLocation airlockRight(int skin) {
		return ber("round_airlock_door_right_ber_" + clamp(skin, AIRLOCK_COUNT));
	}

	public static ResourceLocation waterFrame(int skin) {
		return ber("water_door_frame_ber_" + clamp(skin, WATER_COUNT));
	}

	public static ResourceLocation waterDoor(int skin) {
		return ber("water_door_door_ber_" + clamp(skin, WATER_COUNT));
	}

	public static ResourceLocation waterBolts(int skin) {
		return ber("water_door_bolts_ber_" + clamp(skin, WATER_COUNT));
	}

	public static ResourceLocation waterTop(int skin) {
		return ber("water_door_top_ber_" + clamp(skin, WATER_COUNT));
	}

	public static ResourceLocation waterBottom(int skin) {
		return ber("water_door_bottom_ber_" + clamp(skin, WATER_COUNT));
	}

	public static ResourceLocation[] allFireBerModels() {
		ResourceLocation[] out = new ResourceLocation[FIRE_COUNT * 2];
		for (int i = 0; i < FIRE_COUNT; i++) {
			out[i * 2] = fireFrame(i);
			out[i * 2 + 1] = fireDoor(i);
		}
		return out;
	}

	public static ResourceLocation[] allVaultBerModels() {
		ResourceLocation[] out = new ResourceLocation[VAULT_COUNT * 3];
		for (int i = 0; i < VAULT_COUNT; i++) {
			out[i * 3] = vaultFrame(i);
			out[i * 3 + 1] = vaultDoor(i);
			out[i * 3 + 2] = vaultLabel(i);
		}
		return out;
	}

	public static ResourceLocation[] allSecureBerModels() {
		ResourceLocation[] out = new ResourceLocation[SECURE_COUNT * 2];
		for (int i = 0; i < SECURE_COUNT; i++) {
			out[i * 2] = secureFrame(i);
			out[i * 2 + 1] = secureDoor(i);
		}
		return out;
	}

	public static ResourceLocation[] allContainmentBerModels() {
		ResourceLocation[] out = new ResourceLocation[CONTAINMENT_COUNT * 2];
		for (int i = 0; i < CONTAINMENT_COUNT; i++) {
			out[i * 2] = containmentFrame(i);
			out[i * 2 + 1] = containmentDoor(i);
		}
		return out;
	}

	public static ResourceLocation[] allAirlockBerModels() {
		ResourceLocation[] out = new ResourceLocation[AIRLOCK_COUNT * 3];
		for (int i = 0; i < AIRLOCK_COUNT; i++) {
			out[i * 3] = airlockFrame(i);
			out[i * 3 + 1] = airlockLeft(i);
			out[i * 3 + 2] = airlockRight(i);
		}
		return out;
	}

	public static ResourceLocation[] allWaterBerModels() {
		ResourceLocation[] out = new ResourceLocation[WATER_COUNT * 5];
		for (int i = 0; i < WATER_COUNT; i++) {
			out[i * 5] = waterFrame(i);
			out[i * 5 + 1] = waterDoor(i);
			out[i * 5 + 2] = waterBolts(i);
			out[i * 5 + 3] = waterTop(i);
			out[i * 5 + 4] = waterBottom(i);
		}
		return out;
	}

	private static int clamp(int skin, int count) {
		int m = Math.floorMod(skin, count);
		return m;
	}

	private static ResourceLocation ber(String path) {
		return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/" + path);
	}
}
