package com.hbmr.block.network;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * Horizontal-facing energy-network endpoint (connectors, gauges, diodes, switches).
 * When {@code backOnly} is true, cables only connect on the back face (opposite facing).
 */
public class EnergyFacingBlock extends HorizontalDirectionalBlock implements EnergyNetworkConnectable {
	private final boolean backOnly;

	public EnergyFacingBlock(Properties properties, boolean backOnly) {
		super(properties);
		this.backOnly = backOnly;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public EnergyFacingBlock(Properties properties) {
		this(properties, false);
	}

	@Override
	public boolean canEnergyConnect(BlockState state, Direction face) {
		if (!backOnly) {
			return true;
		}
		return face == state.getValue(FACING).getOpposite();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
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
