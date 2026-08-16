package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.SILO_HATCH} / {@code SILO_HATCH_LARGE}.
 */
public final class SiloHatchAnim {
	public static final int TIME_TO_OPEN = 60;
	public static final float HINGE_Y = 0.875F;
	public static final float HINGE_Z_NORMAL = -1.875F;
	public static final float HINGE_Z_LARGE = -2.875F;
	/** Collision / makeExtra clears once {@code openTicks} reaches this (DoorDecl range time 20/20). */
	public static final int CLEAR_TICKS = 20;

	private SiloHatchAnim() {
	}

	public static float hingeZ(boolean large) {
		return large ? HINGE_Z_LARGE : HINGE_Z_NORMAL;
	}

	/** Hatch lift on Y (0…0.25). */
	public static float liftY(float openTicks, int state) {
		float t = effectiveTicks(openTicks, state);
		return 0.25F * smoothstep(normTime(t, 0.0F, 10.0F));
	}

	/** Hatch pitch degrees about hinge (0 … about −120 at fully open). */
	public static float rotDegrees(float openTicks, int state) {
		float t = effectiveTicks(openTicks, state);
		return smoothstep(normTime(t, 20.0F, 100.0F)) * -240.0F;
	}

	public static boolean doorwayClear(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return true;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return false;
		}
		return effectiveTicks(openTicks, state) >= CLEAR_TICKS;
	}

	/**
	 * Normal hatch: center 3×3 of the 5×5. Large: DoorDecl open-range footprint
	 * (symmetric under 90° — use world |dx|/|dz|).
	 */
	public static boolean isOpenRangeCell(int dx, int dz, boolean large) {
		int ax = Math.abs(dx);
		int az = Math.abs(dz);
		if (!large) {
			return Math.max(ax, az) <= 1;
		}
		return (ax <= 1 && az <= 2) || (ax == 2 && az <= 1);
	}

	private static float effectiveTicks(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return TIME_TO_OPEN;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		return openTicks;
	}

	private static float normTime(float time, float min, float max) {
		if (max == min) {
			return time >= min ? 1.0F : 0.0F;
		}
		return clamp01((time - min) / (max - min));
	}

	private static float smoothstep(float t) {
		t = clamp01(t);
		return t * t * (3.0F - 2.0F * t);
	}

	private static float clamp01(float t) {
		return Math.max(0.0F, Math.min(1.0F, t));
	}
}
