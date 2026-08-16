package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.QE_CONTAINMENT} / {@code RenderContainmentDoor}.
 * Door raises up to {@link #MAX_RAISE} over {@link #TIME_TO_OPEN} ticks.
 */
public final class ContainmentDoorAnim {
	public static final int TIME_TO_OPEN = 160;
	public static final double MAX_RAISE = 2.25D;

	private ContainmentDoorAnim() {
	}

	/** 0 closed … 1 fully raised. */
	public static float doorAmount(float openTicks, int state) {
		if (state == MultiblockControllerBlockEntity.WIDE_OPEN) {
			return 1.0F;
		}
		if (state == MultiblockControllerBlockEntity.WIDE_CLOSED) {
			return 0.0F;
		}
		return Math.max(0.0F, Math.min(1.0F, openTicks / (float) TIME_TO_OPEN));
	}

	public static double raise(float openTicks, int state) {
		return doorAmount(openTicks, state) * MAX_RAISE;
	}
}
