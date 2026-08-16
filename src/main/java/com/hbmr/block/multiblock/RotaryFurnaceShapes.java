package com.hbmr.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Rotary furnace hitboxes clipped from the OBJ solids (model space = BER rot 0 /
 * {@link Direction#EAST}). Footprint matches {@code StructureType.ROTARY_FURNACE}
 * dims after {@link MultiblockHelper#rotateDims}.
 */
public final class RotaryFurnaceShapes {
	/**
	 * Approximate solid volumes in model space (origin = TE / core center XZ, Y = core
	 * floor). Covers base slab, brick drum, orange hopper, side pipe, top exhausts.
	 */
	private static final AABB[] MODEL_SOLIDS = {
			// Base platform (full footprint ledge)
			new AABB(-1.5D, 0.0D, -2.5D, 1.5D, 0.5D, 2.5D),
			// Brick drum + metal corner posts (expanded to match visible shell)
			new AABB(-0.45D, 0.4D, -0.7D, 1.55D, 4.15D, 2.05D),
			// Orange hopper / front-left mass
			new AABB(-1.5D, 0.4D, 0.15D, 0.35D, 2.0D, 2.45D),
			// Curved side pipe
			new AABB(-1.55D, 0.6D, -1.5D, 0.45D, 3.5D, 0.7D),
			// Top cap + exhaust pipes
			new AABB(-1.35D, 3.6D, -2.4D, 1.4D, 5.05D, 1.0D),
	};

	private RotaryFurnaceShapes() {
	}

	public static VoxelShape shapeForCell(BlockPos core, BlockPos cell, Direction facing) {
		int wx = cell.getX() - core.getX();
		int wy = cell.getY() - core.getY();
		int wz = cell.getZ() - core.getZ();
		int[] m = toModelCell(wx, wy, wz, facing);
		int dx = m[0];
		int dy = m[1];
		int dz = m[2];
		if (dy < 0 || dy > 4 || dx < -1 || dx > 1 || dz < -2 || dz > 2) {
			return Shapes.empty();
		}

		// Cell occupancy in model space (TE at 0.5 XZ → cell [i-0.5, i+0.5]).
		AABB cellModel = new AABB(dx - 0.5D, dy, dz - 0.5D, dx + 0.5D, dy + 1.0D, dz + 0.5D);
		VoxelShape shape = Shapes.empty();
		boolean any = false;
		for (AABB solid : MODEL_SOLIDS) {
			if (!solid.intersects(cellModel)) {
				continue;
			}
			AABB clip = solid.intersect(cellModel);
			if (clip.getXsize() < 1.0E-4D || clip.getYsize() < 1.0E-4D || clip.getZsize() < 1.0E-4D) {
				continue;
			}
			// Cell-local [0,1] for EAST-authored shape.
			double minX = clip.minX - (dx - 0.5D);
			double maxX = clip.maxX - (dx - 0.5D);
			double minY = clip.minY - dy;
			double maxY = clip.maxY - dy;
			double minZ = clip.minZ - (dz - 0.5D);
			double maxZ = clip.maxZ - (dz - 0.5D);
			minX = clamp01(minX);
			maxX = clamp01(maxX);
			minY = clamp01(minY);
			maxY = clamp01(maxY);
			minZ = clamp01(minZ);
			maxZ = clamp01(maxZ);
			if (maxX - minX < 1.0E-4D || maxY - minY < 1.0E-4D || maxZ - minZ < 1.0E-4D) {
				continue;
			}
			shape = Shapes.or(shape, Shapes.box(minX, minY, minZ, maxX, maxY, maxZ));
			any = true;
		}
		if (!any) {
			return Shapes.empty();
		}
		return orient(shape.optimize(), facing);
	}

	/** World cell offset → model cell offset (inverse of {@code facingToYRot}). */
	private static int[] toModelCell(int wx, int wy, int wz, Direction facing) {
		return switch (facing) {
			case NORTH -> new int[]{-wz, wy, wx};
			case WEST -> new int[]{-wx, wy, -wz};
			case SOUTH -> new int[]{wz, wy, -wx};
			default -> new int[]{wx, wy, wz}; // EAST
		};
	}

	private static VoxelShape orient(VoxelShape eastAuthored, Direction facing) {
		int times = switch (facing) {
			case NORTH -> 1;
			case WEST -> 2;
			case SOUTH -> 3;
			default -> 0;
		};
		return rotateY90(eastAuthored, times).optimize();
	}

	/** Same Y-rotation convention as {@link ElectricPressShapes}. */
	private static VoxelShape rotateY90(VoxelShape shape, int times) {
		VoxelShape result = shape;
		for (int n = 0; n < times; n++) {
			VoxelShape next = Shapes.empty();
			for (AABB aabb : result.toAabbs()) {
				double minX = Double.POSITIVE_INFINITY;
				double maxX = Double.NEGATIVE_INFINITY;
				double minZ = Double.POSITIVE_INFINITY;
				double maxZ = Double.NEGATIVE_INFINITY;
				for (double x : new double[]{aabb.minX, aabb.maxX}) {
					for (double z : new double[]{aabb.minZ, aabb.maxZ}) {
						double rx = x - 0.5D;
						double rz = z - 0.5D;
						double nx = 0.5D + rz;
						double nz = 0.5D - rx;
						minX = Math.min(minX, nx);
						maxX = Math.max(maxX, nx);
						minZ = Math.min(minZ, nz);
						maxZ = Math.max(maxZ, nz);
					}
				}
				next = Shapes.or(next, Shapes.box(minX, aabb.minY, minZ, maxX, aabb.maxY, maxZ));
			}
			result = next;
		}
		return result;
	}

	private static double clamp01(double v) {
		if (v < 0.0D) {
			return 0.0D;
		}
		if (v > 1.0D) {
			return 1.0D;
		}
		return v;
	}
}
