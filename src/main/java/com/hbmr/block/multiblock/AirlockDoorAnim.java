package com.hbmr.block.multiblock;

/**
 * Matches 1.7.10 {@code DoorDecl.ROUND_AIRLOCK_DOOR} / {@code RenderAirlockDoor}.
 * Left/Right panels slide on ±Z up to {@link #MAX_OPEN} over {@link #TIME_TO_OPEN} ticks.
 */
public final class AirlockDoorAnim {
	public static final int TIME_TO_OPEN = 60;
	public static final double MAX_OPEN = 1.5D;

	private AirlockDoorAnim() {
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

	public static double slide(float openTicks, int state) {
		return doorAmount(openTicks, state) * MAX_OPEN;
	}
}
