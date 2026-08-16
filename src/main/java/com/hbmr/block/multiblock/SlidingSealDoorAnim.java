package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.SLIDING_SEAL_DOOR} / {@code RenderSealDoor}.
 * Door slides on +Z up to {@link #MAX_SLIDE} over {@link #TIME_TO_OPEN} ticks (smoothstep).
 */
public final class SlidingSealDoorAnim {
	public static final int TIME_TO_OPEN = 20;
	public static final double MAX_SLIDE = 0.9D;

	private SlidingSealDoorAnim() {
	}

	/** 0 closed … 1 fully open. */
	public static float doorAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		return Math.max(0.0F, Math.min(1.0F, openTicks / (float) TIME_TO_OPEN));
	}

	/** Smoothstep easing matching 1.7.10 {@code Library.smoothstep}. */
	public static double slide(float openTicks, int state) {
		float a = doorAmount(openTicks, state);
		float s = a * a * (3.0F - 2.0F * a);
		return s * MAX_SLIDE;
	}
}
