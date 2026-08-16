package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.WATER_DOOR} / {@code RenderWaterDoor} bus timing.
 * Bolts retract first, then the door swings up to {@link #MAX_ROT} degrees.
 */
public final class WaterDoorAnim {
	public static final int TIME_TO_OPEN = 60;
	public static final double MAX_ROT = 120.0D;
	private static final int PHASE = 30; // 1500 ms at 20 tps
	private static final int BOLT_HOLD_CLOSE = 24; // 1200 ms

	private WaterDoorAnim() {
	}

	/** Hermite / HBM {@code SIN_FULL}: 0→1 ease. */
	public static float sinFull(float t) {
		t = Math.max(0.0F, Math.min(1.0F, t));
		return (float) ((1.0D - Math.cos(Math.PI * t)) * 0.5D);
	}

	/** Door swing amount 0…1. */
	public static float doorAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			if (openTicks <= PHASE) {
				return 0.0F;
			}
			return sinFull((openTicks - PHASE) / (float) PHASE);
		}
		// CLOSING: swing shut over the first 30 ticks of the close
		float elapsed = TIME_TO_OPEN - openTicks;
		if (elapsed >= PHASE) {
			return 0.0F;
		}
		return 1.0F - sinFull(elapsed / (float) PHASE);
	}

	/** Bolt retract 0…1. */
	public static float boltAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_OPENING) {
			if (openTicks >= PHASE) {
				return 1.0F;
			}
			return sinFull(openTicks / (float) PHASE);
		}
		// CLOSING: hold fully retracted, then extend again
		float elapsed = TIME_TO_OPEN - openTicks;
		if (elapsed < BOLT_HOLD_CLOSE) {
			return 1.0F;
		}
		float u = (elapsed - BOLT_HOLD_CLOSE) / (float) PHASE;
		if (u >= 1.0F) {
			return 0.0F;
		}
		return 1.0F - sinFull(u);
	}

	public static double doorDegrees(float openTicks, int state) {
		return doorAmount(openTicks, state) * MAX_ROT;
	}
}
