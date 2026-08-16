package com.hbmr.block;

import com.hbmr.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Legacy cube blast furnace ({@code MachineDiFurnace} / {@code machine_difurnace_*} in 1.7.10).
 * When a blast furnace extension sits above, switches to tall side/front textures.
 */
public class BlastFurnaceBlock extends HorizontalDirectionalBlock {
	public static final BooleanProperty COVERED = BooleanProperty.create("covered");

	public BlastFurnaceBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(COVERED, false));
	}

	private static boolean hasExtensionAbove(LevelAccessor level, BlockPos pos) {
		return level.getBlockState(pos.above()).is(ModBlocks.BLAST_FURNACE_EXTENSION.get());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(COVERED, hasExtensionAbove(context.getLevel(), context.getClickedPos()));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP) {
			return state.setValue(COVERED, neighborState.is(ModBlocks.BLAST_FURNACE_EXTENSION.get()));
		}
		return state;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
			BlockPos neighborPos, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
		if (level.isClientSide) {
			return;
		}
		if (!neighborPos.equals(pos.above())) {
			return;
		}
		boolean covered = hasExtensionAbove(level, pos);
		if (state.getValue(COVERED) != covered) {
			level.setBlock(pos, state.setValue(COVERED, covered), Block.UPDATE_ALL);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, COVERED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
