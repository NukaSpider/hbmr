package com.hbmr.block.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Thin wall/floor-mounted RTTY torch (1.7.10 {@code RadioTorchBase}).
 * Mesh/UV detail via JSON/forge OBJ; attachment face = {@link #FACING}.
 */
public class RttyBlock extends Block {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	private static final VoxelShape FLOOR = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private static final VoxelShape CEILING = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 16.0D, 11.0D);
	private static final VoxelShape NORTH = Block.box(5.0D, 3.0D, 11.0D, 11.0D, 13.0D, 16.0D);
	private static final VoxelShape SOUTH = Block.box(5.0D, 3.0D, 0.0D, 11.0D, 13.0D, 5.0D);
	private static final VoxelShape WEST = Block.box(11.0D, 3.0D, 5.0D, 16.0D, 13.0D, 11.0D);
	private static final VoxelShape EAST = Block.box(0.0D, 3.0D, 5.0D, 5.0D, 13.0D, 11.0D);

	public RttyBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace());
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
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case DOWN -> CEILING;
			case UP -> FLOOR;
			case NORTH -> NORTH;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			case EAST -> EAST;
		};
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
}
