package com.hbmr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChainlinkFenceBlock extends Block {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final BooleanProperty POST = BooleanProperty.create("post");

	private static final VoxelShape POST_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	private static final VoxelShape NORTH_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 10.0D);
	private static final VoxelShape SOUTH_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 16.0D);
	private static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	private static final VoxelShape EAST_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);

	private final boolean alwaysPost;

	public ChainlinkFenceBlock(Properties properties, boolean alwaysPost) {
		super(properties);
		this.alwaysPost = alwaysPost;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(POST, true));
	}

	public boolean alwaysShowsPost() {
		return this.alwaysPost;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return computeState(context.getLevel(), context.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return computeState(level, pos);
	}

	private BlockState computeState(BlockGetter level, BlockPos pos) {
		boolean north = connectsTo(level, pos, Direction.NORTH);
		boolean east = connectsTo(level, pos, Direction.EAST);
		boolean south = connectsTo(level, pos, Direction.SOUTH);
		boolean west = connectsTo(level, pos, Direction.WEST);

		boolean hasX = east || west;
		boolean hasZ = north || south;
		boolean straightX = !hasZ && west && east;
		boolean straightZ = !hasX && north && south;
		boolean showPost = this.alwaysPost || (!straightX && !straightZ);

		return this.defaultBlockState()
				.setValue(NORTH, north)
				.setValue(EAST, east)
				.setValue(SOUTH, south)
				.setValue(WEST, west)
				.setValue(POST, showPost);
	}

	/**
	 * Connects to other chainlink fences/posts, and to sturdy full faces (like vanilla fences/bars).
	 */
	public static boolean connectsTo(BlockGetter level, BlockPos fencePos, Direction direction) {
		BlockPos neighborPos = fencePos.relative(direction);
		BlockState neighbor = level.getBlockState(neighborPos);

		if (neighbor.getBlock() instanceof ChainlinkFenceBlock) {
			return true;
		}

		if (Block.isExceptionForConnection(neighbor)) {
			return false;
		}

		return neighbor.isFaceSturdy(level, neighborPos, direction.getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, POST);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = Shapes.empty();
		boolean north = state.getValue(NORTH);
		boolean east = state.getValue(EAST);
		boolean south = state.getValue(SOUTH);
		boolean west = state.getValue(WEST);

		if (north) {
			shape = Shapes.or(shape, NORTH_SHAPE);
		}
		if (south) {
			shape = Shapes.or(shape, SOUTH_SHAPE);
		}
		if (west) {
			shape = Shapes.or(shape, WEST_SHAPE);
		}
		if (east) {
			shape = Shapes.or(shape, EAST_SHAPE);
		}
		if (state.getValue(POST) || (!north && !east && !south && !west)) {
			shape = Shapes.or(shape, POST_SHAPE);
		}
		return shape.isEmpty() ? POST_SHAPE : shape;
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
