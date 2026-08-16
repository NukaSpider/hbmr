package com.hbmr.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Full-cell hitboxes for heater/furnace/boiler-style multiblocks, plus crucible (1.5 tall)
 * and strand caster (2×7×1 base + 2×2×3 tower matching 1.7.10 fillSpace).
 */
public final class MachineCellShapes {
	/** Crucible mesh is Y 0…1.5 over a single layer of cells. */
	private static final VoxelShape CRUCIBLE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

	/**
	 * Decorative 1×1 IGen (OBJ ×0.18, BER facingToYRot+90). Fits scaled mesh AABB;
	 * long axis along X for EAST, rotated with facing.
	 */
	private static final VoxelShape IGEN_EAST = Block.box(1.0D, 0.0D, 4.0D, 16.0D, 9.0D, 12.0D);
	private static final VoxelShape IGEN_NORTH = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 9.0D, 15.0D);
	private static final VoxelShape IGEN_WEST = Block.box(0.0D, 0.0D, 4.0D, 15.0D, 9.0D, 12.0D);
	private static final VoxelShape IGEN_SOUTH = Block.box(4.0D, 0.0D, 1.0D, 12.0D, 9.0D, 16.0D);

	/**
	 * Microwave OBJ after BER (−0.5, −0.785, +0.65), −90° Y, then facingToYRot.
	 * Clamped to the cell; mesh is ~0.75×0.4×0.61 sitting on the floor.
	 */
	private static final VoxelShape MICROWAVE_EAST = Block.box(0.0D, 0.0D, 1.0D, 10.0D, 7.0D, 14.0D);
	private static final VoxelShape MICROWAVE_NORTH = Block.box(1.0D, 0.0D, 6.0D, 14.0D, 7.0D, 16.0D);
	private static final VoxelShape MICROWAVE_WEST = Block.box(6.0D, 0.0D, 2.0D, 16.0D, 7.0D, 15.0D);
	private static final VoxelShape MICROWAVE_SOUTH = Block.box(2.0D, 0.0D, 0.0D, 15.0D, 7.0D, 10.0D);

	private MachineCellShapes() {
	}

	public static boolean usesFullFootprint(StructureType type) {
		return type == StructureType.HEAT_EXCHANGING_HEATER
				|| type == StructureType.IRON_FURNACE
				|| type == StructureType.STEEL_FURNACE
				|| type == StructureType.COMBINATION_OVEN
				|| type == StructureType.STIRLING_ENGINE
				|| type == StructureType.HEAVY_STIRLING_ENGINE
				|| type == StructureType.CREATIVE_STIRLING_ENGINE
				|| type == StructureType.STIRLING_SAWMILL
				|| type == StructureType.CRUCIBLE
				|| type == StructureType.STRAND_CASTER
				|| type == StructureType.BOILER
				|| type == StructureType.INDUSTRIAL_BOILER
				|| type == StructureType.BURNER_PRESS
				|| type == StructureType.CENTRIFUGE
				|| type == StructureType.GAS_CENTRIFUGE
				|| type == StructureType.FEL
				|| type == StructureType.SILEX
				|| type == StructureType.ROTARY_FURNACE
				|| type == StructureType.ORE_ACIDIZER
				|| type == StructureType.BREEDING_REACTOR
				|| type == StructureType.WOOD_BURNING_GENERATOR
				|| type == StructureType.DIESEL_GENERATOR
				|| type == StructureType.INDUSTRIAL_COMBUSTION_ENGINE
				|| type == StructureType.RESEARCH_REACTOR
				|| type == StructureType.ZIRNOX_NUCLEAR_REACTOR
				|| type == StructureType.INDUSTRIAL_GENERATOR
				|| type == StructureType.RADIATION_POWERED_ENGINE
				|| type == StructureType.CYCLOTRON
				|| type == StructureType.EXPOSURE_CHAMBER
				|| type == StructureType.RT_GENERATOR
				|| type == StructureType.RTGRC
				|| type == StructureType.GEOTHERMAL_HEAT_EXCHANGER
				|| type == StructureType.PARTICLE_SOURCE
				|| type == StructureType.BEAMLINE
				|| type == StructureType.DRAINAGE_PIPE
				|| type == StructureType.TELEX_MACHINE
				|| type == StructureType.CONVEYOR_SPLITTER
				|| type == StructureType.FAN
				|| type == StructureType.INSERTER
				|| type == StructureType.SAFE_BARREL
				|| type == StructureType.STEEL_BARREL
				|| type == StructureType.TECHNETIUM_STEEL_BARREL
				|| type == StructureType.MAGNETIC_ANTIMATTER_CONTAINER
				|| type == StructureType.BATTERY_SOCKET
				|| type == StructureType.FENSU
				|| type == StructureType.OLD_CAPACITOR
				|| type == StructureType.MICROWAVE
				|| type == StructureType.ASSEMBLY_MACHINE
				|| type == StructureType.ASSEMBLY_FACTORY
				|| type == StructureType.PRECASS
				|| type == StructureType.CHEMICAL_PLANT
				|| type == StructureType.CHEMICAL_FACTORY
				|| type == StructureType.RF_CAVITY
				|| type == StructureType.QUADRUPOLE_MAGNETS
				|| type == StructureType.DIPOLE_MAGNETS
				|| type == StructureType.PARTICLE_DETECTOR
				|| type == StructureType.RBMK_CONSOLE
				|| type == StructureType.RBMK_CRANE_CONSOLE
				|| type == StructureType.RBMK_AUTOLOADER
				|| type == StructureType.RBMK_DEBRIS
				|| type == StructureType.RBMK_DEBRIS_FLAMING
				|| type == StructureType.RBMK_DEBRIS_SMOLDERING
				|| type == StructureType.RBMK_DEBRIS_BLACKENED
				|| type.isRbmkColumn();
	}

	public static VoxelShape shapeForCell(StructureType type, BlockPos core, BlockPos cell, Direction facing) {
		if (type == StructureType.CRUCIBLE) {
			return CRUCIBLE;
		}
		if (type == StructureType.STRAND_CASTER) {
			return strandCasterShape(core, cell, facing);
		}
		if (type == StructureType.EXPOSURE_CHAMBER) {
			return exposureChamberShape(core, cell, facing);
		}
		if (type == StructureType.INDUSTRIAL_GENERATOR) {
			return industrialGeneratorShape(facing);
		}
		if (type == StructureType.MICROWAVE) {
			return microwaveShape(facing);
		}
		if (type == StructureType.ROTARY_FURNACE) {
			return RotaryFurnaceShapes.shapeForCell(core, cell, facing);
		}
		return Shapes.block();
	}

	private static VoxelShape industrialGeneratorShape(Direction facing) {
		return switch (facing) {
			case NORTH -> IGEN_NORTH;
			case WEST -> IGEN_WEST;
			case SOUTH -> IGEN_SOUTH;
			default -> IGEN_EAST;
		};
	}

	private static VoxelShape microwaveShape(Direction facing) {
		return switch (facing) {
			case NORTH -> MICROWAVE_NORTH;
			case WEST -> MICROWAVE_WEST;
			case SOUTH -> MICROWAVE_SOUTH;
			default -> MICROWAVE_EAST;
		};
	}

	/**
	 * Main tank: full cells. Coil arm: walk under (empty dy≤0, top-slab dy==1).
	 * Green end base: solid 2×3×1 on the ground (SOUTH-authored X+7…+8, Z±1).
	 */
	private static final int[] EXPOSURE_GREEN_BASE = {0, 0, 1, 1, -7, 8};

	private static VoxelShape exposureChamberShape(BlockPos core, BlockPos cell, Direction facing) {
		int[] main = MultiblockHelper.rotateDims(StructureType.EXPOSURE_CHAMBER.getDims(), facing);
		if (inFootprint(core, cell, main)) {
			return Shapes.block();
		}
		int[] greenBase = MultiblockHelper.rotateDims(EXPOSURE_GREEN_BASE, facing);
		boolean onGreenBase = inFootprint(core, cell, greenBase);
		int dy = cell.getY() - core.getY();
		if (dy <= 0) {
			return onGreenBase ? Shapes.block() : Shapes.empty();
		}
		if (dy == 1 && !onGreenBase) {
			return Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		}
		return Shapes.block();
	}

	/**
	 * Primary dims {0,0,6,0,1,0}: 2×7×1 run. Extra {2,0,1,0,1,0}: 2×2×3 tower at the rear
	 * (core end of the line). Matches 1.7.10 {@code MachineStrandCaster#fillSpace}.
	 */
	private static VoxelShape strandCasterShape(BlockPos core, BlockPos cell, Direction facing) {
		int dy = cell.getY() - core.getY();
		if (dy < 0 || dy > 2) {
			return Shapes.empty();
		}
		int[] base = MultiblockHelper.rotateDims(StructureType.STRAND_CASTER.getDims(), facing);
		int[] tower = MultiblockHelper.rotateDims(new int[]{2, 0, 1, 0, 1, 0}, facing);
		if (dy == 0) {
			return inFootprint(core, cell, base) ? Shapes.block() : Shapes.empty();
		}
		// Upper layers only on the tall rear 2×2.
		return inFootprint(core, cell, tower) ? Shapes.block() : Shapes.empty();
	}

	private static boolean inFootprint(BlockPos core, BlockPos cell, int[] rot) {
		int u = rot[0], d = rot[1], n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		int minX = Math.min(core.getX() - w, core.getX() + e);
		int maxX = Math.max(core.getX() - w, core.getX() + e);
		int minY = Math.min(core.getY() - d, core.getY() + u);
		int maxY = Math.max(core.getY() - d, core.getY() + u);
		int minZ = Math.min(core.getZ() - n, core.getZ() + s);
		int maxZ = Math.max(core.getZ() - n, core.getZ() + s);
		int x = cell.getX(), y = cell.getY(), z = cell.getZ();
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}
}
