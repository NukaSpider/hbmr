package com.hbmr.block.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Six-way thin cable (1.7.10 {@code BlockCable} / {@code red_cable}).
 * Mesh is drawn by {@link com.hbmr.client.CableNeoBER} from {@code cable_neo.obj}.
 */
public class CableNetworkBlock extends Block implements EntityBlock, EnergyNetworkConnectable {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

	/** 5.5–10.5 px square — matches 1.7.10 {@code BlockCable#setBlockBounds}. */
	private static final double MIN = 5.5D;
	private static final double MAX = 10.5D;
	private static final VoxelShape CENTER = Block.box(MIN, MIN, MIN, MAX, MAX, MAX);
	private static final VoxelShape ARM_NORTH = Block.box(MIN, MIN, 0.0D, MAX, MAX, MIN);
	private static final VoxelShape ARM_SOUTH = Block.box(MIN, MIN, MAX, MAX, MAX, 16.0D);
	private static final VoxelShape ARM_WEST = Block.box(0.0D, MIN, MIN, MIN, MAX, MAX);
	private static final VoxelShape ARM_EAST = Block.box(MAX, MIN, MIN, 16.0D, MAX, MAX);
	private static final VoxelShape ARM_DOWN = Block.box(MIN, 0.0D, MIN, MAX, MIN, MAX);
	private static final VoxelShape ARM_UP = Block.box(MIN, MAX, MIN, MAX, 16.0D, MAX);

	public CableNetworkBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CableNetworkBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return withConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return withConnections(state, level, pos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			BlockState updated = withConnections(state, level, pos);
			if (!updated.equals(state)) {
				level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
			}
			for (Direction dir : Direction.values()) {
				BlockPos neighborPos = pos.relative(dir);
				BlockState neighbor = level.getBlockState(neighborPos);
				if (neighbor.getBlock() instanceof CableNetworkBlock) {
					BlockState neighborUpdated = withConnections(neighbor, level, neighborPos);
					if (!neighborUpdated.equals(neighbor)) {
						level.setBlock(neighborPos, neighborUpdated, Block.UPDATE_ALL);
					}
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
			BlockPos neighborPos, boolean movedByPiston) {
		BlockState updated = withConnections(state, level, pos);
		if (!updated.equals(state)) {
			level.setBlock(pos, updated, Block.UPDATE_ALL);
		}
	}

	public static BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos) {
		return state
				.setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
				.setValue(EAST, canConnectTo(level, pos, Direction.EAST))
				.setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
				.setValue(WEST, canConnectTo(level, pos, Direction.WEST))
				.setValue(UP, canConnectTo(level, pos, Direction.UP))
				.setValue(DOWN, canConnectTo(level, pos, Direction.DOWN));
	}

	public static boolean canConnectTo(BlockGetter level, BlockPos pos, Direction dir) {
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighbor = level.getBlockState(neighborPos);
		Block block = neighbor.getBlock();
		if (block instanceof CableNetworkBlock) {
			return true;
		}
		if (block instanceof EnergyNetworkConnectable connectable) {
			return connectable.canEnergyConnect(neighbor, dir.getOpposite());
		}
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = CENTER;
		if (state.getValue(NORTH)) {
			shape = Shapes.or(shape, ARM_NORTH);
		}
		if (state.getValue(SOUTH)) {
			shape = Shapes.or(shape, ARM_SOUTH);
		}
		if (state.getValue(WEST)) {
			shape = Shapes.or(shape, ARM_WEST);
		}
		if (state.getValue(EAST)) {
			shape = Shapes.or(shape, ARM_EAST);
		}
		if (state.getValue(UP)) {
			shape = Shapes.or(shape, ARM_UP);
		}
		if (state.getValue(DOWN)) {
			shape = Shapes.or(shape, ARM_DOWN);
		}
		return shape;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
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
