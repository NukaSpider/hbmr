package com.hbmr.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Space checks / fills matching 1.7.10 {@code MultiblockHandlerXR} (dims identity when facing SOUTH).
 */
public final class MultiblockHelper {
	private MultiblockHelper() {
	}

	/**
	 * Rotate [U,D,N,S,W,E] for the given horizontal facing.
	 * SOUTH leaves dims unchanged (BlockDummyable / MultiblockHandlerXR convention).
	 * Negative components are preserved (vault door extra footprint).
	 */
	public static int[] rotateDims(int[] dim, Direction dir) {
		if (dim == null || dim.length != 6) {
			return dim;
		}
		return switch (dir) {
			case NORTH -> new int[]{dim[0], dim[1], dim[3], dim[2], dim[5], dim[4]};
			case EAST -> new int[]{dim[0], dim[1], dim[5], dim[4], dim[2], dim[3]};
			case WEST -> new int[]{dim[0], dim[1], dim[4], dim[5], dim[3], dim[2]};
			default -> dim; // SOUTH and vertical fallbacks
		};
	}

	public static BlockPos coreFromClick(BlockPos clickPos, Direction facing, int offset) {
		return clickPos.relative(facing, -offset);
	}

	public static void forEachCell(BlockPos core, int[] dims, Direction facing, Consumer<BlockPos> consumer) {
		int[] rot = rotateDims(dims, facing);
		int u = rot[0], d = rot[1], n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		int minX = core.getX() - w;
		int maxX = core.getX() + e;
		int minY = core.getY() - d;
		int maxY = core.getY() + u;
		int minZ = core.getZ() - n;
		int maxZ = core.getZ() + s;
		// Negative dim components can invert a range; normalize so loops still work.
		if (minX > maxX) {
			int t = minX;
			minX = maxX;
			maxX = t;
		}
		if (minY > maxY) {
			int t = minY;
			minY = maxY;
			maxY = t;
		}
		if (minZ > maxZ) {
			int t = minZ;
			minZ = maxZ;
			maxZ = t;
		}
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					consumer.accept(new BlockPos(x, y, z));
				}
			}
		}
	}

	public static void forEachCellWithExtras(BlockPos core, StructureType type, Direction facing, Consumer<BlockPos> consumer) {
		forEachCell(core, type.getDims(), facing, consumer);
		int[][] extras = type.getExtraDims();
		if (extras != null) {
			for (int[] extra : extras) {
				forEachCell(core, extra, facing, consumer);
			}
		}
	}

	public static List<BlockPos> collectCells(BlockPos core, int[] dims, Direction facing) {
		List<BlockPos> list = new ArrayList<>();
		forEachCell(core, dims, facing, list::add);
		return list;
	}

	public static boolean checkSpace(LevelAccessor level, BlockPos core, StructureType type, Direction facing, BlockPos allowedOccupied) {
		if (!checkSpace(level, core, type.getDims(), facing, allowedOccupied)) {
			return false;
		}
		int[][] extras = type.getExtraDims();
		if (extras != null) {
			for (int[] extra : extras) {
				if (!checkSpace(level, core, extra, facing, allowedOccupied)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean checkSpace(LevelAccessor level, BlockPos core, int[] dims, Direction facing, BlockPos allowedOccupied) {
		int[] rot = rotateDims(dims, facing);
		int u = rot[0], d = rot[1], n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		int minX = core.getX() - w;
		int maxX = core.getX() + e;
		int minY = core.getY() - d;
		int maxY = core.getY() + u;
		int minZ = core.getZ() - n;
		int maxZ = core.getZ() + s;
		if (minX > maxX) {
			int t = minX;
			minX = maxX;
			maxX = t;
		}
		if (minY > maxY) {
			int t = minY;
			minY = maxY;
			maxY = t;
		}
		if (minZ > maxZ) {
			int t = minZ;
			minZ = maxZ;
			maxZ = t;
		}
		int count = 0;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (pos.equals(allowedOccupied)) {
						continue;
					}
					BlockState state = level.getBlockState(pos);
					if (!state.canBeReplaced()) {
						return false;
					}
					if (++count > 2000) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean checkExtraLayer(LevelAccessor level, BlockPos core, int layerY, int[] dims, Direction facing) {
		int[] rot = rotateDims(dims, facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		for (int x = core.getX() - w; x <= core.getX() + e; x++) {
			for (int z = core.getZ() - n; z <= core.getZ() + s; z++) {
				BlockPos pos = new BlockPos(x, layerY, z);
				if (!level.getBlockState(pos).canBeReplaced()) {
					return false;
				}
			}
		}
		return true;
	}
}
