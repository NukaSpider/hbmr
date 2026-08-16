package com.hbmr.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cargo elevator collision matching 1.7.10 {@code BlockCargoElevator#getAABBs}:
 * four 0.25×0.25 corner posts, plus a platform slab when the elevator is idle.
 * <p>
 * While the platform is traveling, the deck collision is omitted — moving VoxelShapes
 * shove players every tick (bounce). Riders are carried in the BE tick instead.
 */
public final class CargoElevatorShapes {
	/** Outer 4px of each corner cell (0.25 block). */
	private static final double POST = 4.0D;

	private CargoElevatorShapes() {
	}

	public static VoxelShape shapeForCell(BlockPos core, BlockPos cell, int layerCount, double extension,
			boolean platformMoving) {
		int dx = cell.getX() - core.getX();
		int dy = cell.getY() - core.getY();
		int dz = cell.getZ() - core.getZ();
		int layers = Math.max(layerCount, 1);
		if (Math.abs(dx) > 1 || Math.abs(dz) > 1 || dy < 0 || dy >= layers) {
			return Shapes.empty();
		}

		boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
		VoxelShape shape = Shapes.empty();

		// Idle only: moving deck collision fights entity carry and causes bounce.
		if (!platformMoving) {
			double platMin = 0.5D + extension - dy;
			double platMax = 1.0D + extension - dy;
			if (platMax > 0.0D && platMin < 1.0D) {
				double minY = Math.max(0.0D, platMin) * 16.0D;
				double maxY = Math.min(1.0D, platMax) * 16.0D;
				if (maxY > minY) {
					shape = Shapes.joinUnoptimized(shape, Block.box(0, minY, 0, 16, maxY, 16), BooleanOp.OR);
				}
			}
		}

		if (corner) {
			double minX = dx < 0 ? 0.0D : 16.0D - POST;
			double maxX = dx < 0 ? POST : 16.0D;
			double minZ = dz < 0 ? 0.0D : 16.0D - POST;
			double maxZ = dz < 0 ? POST : 16.0D;
			double postMaxY = 16.0D;
			if (dy == 0 && extension < 0.05D) {
				postMaxY = 8.0D;
			}
			shape = Shapes.joinUnoptimized(shape, Block.box(minX, 0, minZ, maxX, postMaxY, maxZ), BooleanOp.OR);
		}

		return shape.isEmpty() ? Shapes.empty() : shape.optimize();
	}

	public static boolean isCornerCell(BlockPos core, BlockPos cell) {
		int dx = Math.abs(cell.getX() - core.getX());
		int dz = Math.abs(cell.getZ() - core.getZ());
		return dx == 1 && dz == 1;
	}
}
