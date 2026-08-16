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
 * Horizontal-facing fluid-network endpoint (gauges, valves, flow pump).
 */
public class FluidFacingBlock extends HorizontalDirectionalBlock implements FluidNetworkConnectable {
	public enum ConnectMode {
		/** All six faces. */
		ALL,
		/** Only the face opposite {@link #FACING} (back). */
		BACK_ONLY,
		/** Only the two horizontal sides of {@link #FACING} (left/right). */
		SIDES
	}

	private final ConnectMode connectMode;

	public FluidFacingBlock(Properties properties, ConnectMode connectMode) {
		super(properties);
		this.connectMode = connectMode;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	/** @deprecated prefer {@link #FluidFacingBlock(Properties, ConnectMode)} */
	@Deprecated
	public FluidFacingBlock(Properties properties, boolean backOnly) {
		this(properties, backOnly ? ConnectMode.BACK_ONLY : ConnectMode.ALL);
	}

	public FluidFacingBlock(Properties properties) {
		this(properties, ConnectMode.ALL);
	}

	@Override
	public boolean canFluidConnect(BlockState state, Direction face) {
		Direction facing = state.getValue(FACING);
		return switch (connectMode) {
			case ALL -> true;
			case BACK_ONLY -> face == facing.getOpposite();
			case SIDES -> face == facing.getClockWise() || face == facing.getCounterClockWise();
		};
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
