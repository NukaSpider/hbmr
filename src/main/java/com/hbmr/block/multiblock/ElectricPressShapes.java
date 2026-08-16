package com.hbmr.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Electric press hitboxes matching {@code electric_press_body/head} (C-shape).
 * <p>
 * Model space (BER rot 0 / {@link Direction#EAST}): mid stem is at −X
 * ({@code X[-0.5…-1/3]}), open throat toward +X, top arm overhangs +X.
 * Rotated with the same Y steps as {@code MultiblockBER#facingToYRot}.
 */
public final class ElectricPressShapes {
	/** Mid stem in model space (−0.5 → −1/3) → block X 0…⅓. */
	private static final double STEM_MAX_X = 1.0D / 3.0D;

	/** Idle head at body Y +1.875 (see MultiblockBER); model X −0.1…0.4 → 0.4…0.9. */
	private static final VoxelShape HEAD_MID = Shapes.box(0.4D, 0.875D, 0.125D, 0.9D, 1.0D, 0.875D);
	private static final VoxelShape HEAD_TOP = Shapes.box(0.4D, 0.0D, 0.125D, 0.9D, 0.875D, 0.875D);
	/** Vertical mid stem (only solid in the open C throat layer). */
	private static final VoxelShape STEM = Shapes.box(0.0D, 0.0D, 0.0D, STEM_MAX_X, 1.0D, 1.0D);
	/** Top arm / overhang toward +X, plus stem continuation. */
	private static final VoxelShape TOP = Shapes.or(
			STEM,
			Shapes.box(STEM_MAX_X, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D));

	private static final VoxelShape LAYER0_EAST = Shapes.block();
	private static final VoxelShape LAYER1_EAST = Shapes.or(STEM, HEAD_MID);
	private static final VoxelShape LAYER2_EAST = Shapes.or(TOP, HEAD_TOP);

	private static final VoxelShape[] LAYER0 = orientAll(LAYER0_EAST);
	private static final VoxelShape[] LAYER1 = orientAll(LAYER1_EAST);
	private static final VoxelShape[] LAYER2 = orientAll(LAYER2_EAST);

	private ElectricPressShapes() {
	}

	public static VoxelShape shapeForCell(BlockPos core, BlockPos cell, Direction facing) {
		if (cell.getX() != core.getX() || cell.getZ() != core.getZ()) {
			return Shapes.block();
		}
		int dy = cell.getY() - core.getY();
		int fi = facingIndex(facing);
		return switch (dy) {
			case 0 -> LAYER0[fi];
			case 1 -> LAYER1[fi];
			case 2 -> LAYER2[fi];
			default -> Shapes.block();
		};
	}

	private static int facingIndex(Direction facing) {
		return switch (facing) {
			case NORTH -> 1;
			case WEST -> 2;
			case SOUTH -> 3;
			default -> 0; // EAST
		};
	}

	private static VoxelShape[] orientAll(VoxelShape eastAuthored) {
		return new VoxelShape[]{
				eastAuthored.optimize(),
				rotateY90(eastAuthored, 1).optimize(),
				rotateY90(eastAuthored, 2).optimize(),
				rotateY90(eastAuthored, 3).optimize()
		};
	}

	/** Rotate around block center XZ, matching Mojang {@code Axis.YP} (CCW when looking down +Y). */
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
}
