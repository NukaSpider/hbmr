package com.hbmr.block.multiblock;

import net.minecraft.core.Direction;

/**
 * Matches 1.7.10 {@code DoorDecl.TRANSITION_SEAL}.
 */
public final class TransitionSealAnim {
	public static final int TIME_TO_OPEN = 480;
	public static final int ANIM_LENGTH_MS = 24040;
	/** Doorway collision clears after this many ticks (~13s at 20 TPS). */
	public static final int CLEAR_TICKS = 260;
	/** Open-range: start (-9, 2, 0), axis Y, length 20, width 20 (DoorDecl). */
	public static final int RANGE_START_X = -9;
	public static final int RANGE_START_Y = 2;
	public static final int RANGE_START_Z = 0;
	public static final int RANGE_LENGTH = 20;
	public static final int RANGE_WIDTH = 20;

	private TransitionSealAnim() {
	}

	/** Normalized open progress 0…1. */
	public static float normTime(float openTicks) {
		return Math.max(0.0F, Math.min(1.0F, openTicks / TIME_TO_OPEN));
	}

	/**
	 * Whether a local (south-authored) offset cell is in the circular doorway and
	 * should clear at the current open progress.
	 * <p>
	 * DoorDecl open-range is a 20×20 square ({@code {-9,2,0,20,20,1}}); collision uses the
	 * inscribed circle (same margins) so the passage matches the round seal.
	 * The hole only opens after {@link #CLEAR_TICKS} (~13s), matching the visual clearance.
	 */
	public static boolean isClearedOpenRangeCell(int localX, int localY, int localZ, float openTicks) {
		if (localZ != RANGE_START_Z) {
			return false;
		}
		if (openTicks < CLEAR_TICKS) {
			return false;
		}
		int j = localY - RANGE_START_Y;
		int k = localX - RANGE_START_X;
		if (j < 0 || j >= RANGE_LENGTH || k < 0 || k >= RANGE_WIDTH) {
			return false;
		}
		// Inscribed circle in the open-range square (cell centers).
		double cx = RANGE_START_X + RANGE_WIDTH * 0.5D;
		double cy = RANGE_START_Y + RANGE_LENGTH * 0.5D;
		double radius = Math.min(RANGE_WIDTH, RANGE_LENGTH) * 0.5D;
		double dx = (localX + 0.5D) - cx;
		double dy = (localY + 0.5D) - cy;
		return dx * dx + dy * dy <= radius * radius;
	}

	/**
	 * World offset → south-authored local offset (inverse of 1.7.10 door-range Rotation).
	 */
	public static int[] worldToLocalOffset(int wx, int wy, int wz, Direction facing) {
		return switch (facing) {
			case NORTH -> new int[]{wx, wy, wz};
			case SOUTH -> new int[]{-wx, wy, -wz};
			case EAST -> new int[]{wz, wy, -wx}; // inv of (-z,y,x)
			case WEST -> new int[]{-wz, wy, wx}; // inv of (z,y,-x)
			default -> new int[]{wx, wy, wz};
		};
	}
}
