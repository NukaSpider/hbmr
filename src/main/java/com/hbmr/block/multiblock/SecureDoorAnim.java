package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.SECURE_ACCESS_DOOR} / {@code RenderSecureDoor}.
 * Linear raise over {@link #TIME_TO_OPEN} ticks; max raise {@link #MAX_RAISE}.
 */
public final class SecureDoorAnim {
	public static final int TIME_TO_OPEN = 120;
	public static final double MAX_RAISE = 3.5D;

	private SecureDoorAnim() {
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
