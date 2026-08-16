package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.VAULT_DOOR} / {@code RenderVaultDoor}.
 */
public final class VaultDoorAnim {
	public static final int TIME_TO_OPEN = 120;
	public static final double DOOR_DIAMETER = 4.25D;
	public static final double SLIDE_DISTANCE = 5.0D;

	private VaultDoorAnim() {
	}

	/** PULL bus Z: 0 closed … 1 pulled out. */
	public static float pullAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			float ms = openTicks * 50.0F;
			return sinFull(clamp01(ms / 2000.0F));
		}
		float elapsed = (TIME_TO_OPEN - openTicks) * 50.0F;
		if (elapsed < 4000.0F) {
			return 1.0F;
		}
		return 1.0F - sinFull(clamp01((elapsed - 4000.0F) / 2000.0F));
	}

	/** SLIDE bus X: 0 closed … 1 fully slid (×{@link #SLIDE_DISTANCE} in renderer). */
	public static float slideAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			float ms = openTicks * 50.0F;
			if (ms < 2000.0F) {
				return 0.0F;
			}
			return clamp01((ms - 2000.0F) / 4000.0F);
		}
		float elapsed = (TIME_TO_OPEN - openTicks) * 50.0F;
		if (elapsed < 4000.0F) {
			return 1.0F - clamp01(elapsed / 4000.0F);
		}
		return 0.0F;
	}

	/** HBM {@code IType.SIN_FULL}: ease in-out. */
	private static float sinFull(float t) {
		return (float) ((-Math.cos(t * Math.PI) + 1.0D) * 0.5D);
	}

	private static float clamp01(float t) {
		return Math.max(0.0F, Math.min(1.0F, t));
	}
}
