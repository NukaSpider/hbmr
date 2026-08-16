package com.hbmr.block.multiblock;

/**
 * Keyframe amounts matching 1.7.10 {@code DoorDecl.SLIDE_DOOR} BusAnimation /
 * {@code RenderSlidingBlastDoor}.
 * <p>
 * {@code SIN_UP} matches HBM {@code BusAnimationKeyframe}: {@code 1 - cos(t * π/2)}
 * (ease-in: slow start, fast end) — not {@code sin(t * π/2)}.
 */
public final class SlidingBlastDoorAnim {
	public static final int TIME_TO_OPEN = 24;
	public static final double MAX_OPEN = 2.125D;

	private SlidingBlastDoorAnim() {
	}

	/** LOCK bus X: 0 closed … 1 unlocked (×90° in renderer). */
	public static float lockAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			float ms = openTicks * 50.0F;
			return clamp01(ms / 200.0F);
		}
		float elapsed = (TIME_TO_OPEN - openTicks) * 50.0F;
		if (elapsed < 1000.0F) {
			return 1.0F;
		}
		return 1.0F - clamp01((elapsed - 1000.0F) / 200.0F);
	}

	/** DOOR bus Y: 0 closed … 1 fully slid (×{@link #MAX_OPEN} in renderer). */
	public static float doorAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			float ms = openTicks * 50.0F;
			if (ms < 350.0F) {
				return 0.0F;
			}
			if (ms < 550.0F) {
				return lerp(0.0F, 0.05F, (ms - 350.0F) / 200.0F);
			}
			return lerp(0.05F, 1.0F, sinUp((ms - 550.0F) / 650.0F));
		}
		float elapsed = (TIME_TO_OPEN - openTicks) * 50.0F;
		if (elapsed < 650.0F) {
			return lerp(1.0F, 0.05F, sinUp(elapsed / 650.0F));
		}
		if (elapsed < 850.0F) {
			return lerp(0.05F, 0.0F, (elapsed - 650.0F) / 200.0F);
		}
		return 0.0F;
	}

	/** HBM {@code IType.SIN_UP}: ease-in (slow start, accelerates). */
	private static float sinUp(float t) {
		t = clamp01(t);
		return (float) (1.0D - Math.cos(t * Math.PI * 0.5));
	}

	private static float lerp(float a, float b, float t) {
		return a + (b - a) * clamp01(t);
	}

	private static float clamp01(float t) {
		return Math.max(0.0F, Math.min(1.0F, t));
	}
}
