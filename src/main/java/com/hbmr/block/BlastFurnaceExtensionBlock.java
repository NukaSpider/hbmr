package com.hbmr.block;

import com.hbmr.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * DiFurnace chimney extension ({@code MachineDiFurnaceExtension} in 1.7.10).
 * Simple 1×1 block (not a multiblock); sits on the blast furnace and flips its covered textures.
 */
public class BlastFurnaceExtensionBlock extends Block {
	/** Truncated pyramid: full base to y=12, half-size cap to y=16. */
	private static final VoxelShape SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
			Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D)
	);

	public BlastFurnaceExtensionBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		syncFurnaceBelow(level, pos);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
		if (!state.is(newState.getBlock())) {
			syncFurnaceBelow(level, pos);
		}
	}

	private static void syncFurnaceBelow(Level level, BlockPos extensionPos) {
		if (level.isClientSide) {
			return;
		}
		BlockPos below = extensionPos.below();
		BlockState furnace = level.getBlockState(below);
		if (!furnace.is(ModBlocks.BLAST_FURNACE.get()) || !furnace.hasProperty(BlastFurnaceBlock.COVERED)) {
			return;
		}
		boolean covered = level.getBlockState(extensionPos).is(ModBlocks.BLAST_FURNACE_EXTENSION.get());
		if (furnace.getValue(BlastFurnaceBlock.COVERED) != covered) {
			level.setBlock(below, furnace.setValue(BlastFurnaceBlock.COVERED, covered), Block.UPDATE_ALL);
		}
	}
}
