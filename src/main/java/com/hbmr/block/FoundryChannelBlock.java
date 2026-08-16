package com.hbmr.block;

import com.hbmr.registry.ModBlocks;
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

/**
 * Foundry channel ({@code FoundryChannel} in 1.7.10). Fence-style connections to other
 * channels, shallow basins, outlets, and spill outlets.
 */
public class FoundryChannelBlock extends Block {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;

	/** Center post 5–11 on X/Z, half-block tall (matches 1.7.10 0.3125–0.6875 × 0.5). */
	private static final VoxelShape CENTER = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D);
	private static final VoxelShape ARM_NORTH = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 8.0D, 5.0D);
	private static final VoxelShape ARM_SOUTH = Block.box(5.0D, 0.0D, 11.0D, 11.0D, 8.0D, 16.0D);
	private static final VoxelShape ARM_WEST = Block.box(0.0D, 0.0D, 5.0D, 5.0D, 8.0D, 11.0D);
	private static final VoxelShape ARM_EAST = Block.box(11.0D, 0.0D, 5.0D, 16.0D, 8.0D, 11.0D);

	public FoundryChannelBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false));
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
		return this.defaultBlockState()
				.setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
				.setValue(EAST, canConnectTo(level, pos, Direction.EAST))
				.setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
				.setValue(WEST, canConnectTo(level, pos, Direction.WEST));
	}

	/**
	 * Matches 1.7.10 {@code FoundryChannel#canConnectTo}: channels and shallow basins always;
	 * outlets / spill outlets only when their facing equals the connection direction.
	 */
	public static boolean canConnectTo(BlockGetter level, BlockPos pos, Direction dir) {
		if (!dir.getAxis().isHorizontal()) {
			return false;
		}
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighbor = level.getBlockState(neighborPos);
		Block block = neighbor.getBlock();

		if (block instanceof FoundryChannelBlock) {
			return true;
		}
		if (block == ModBlocks.SHALLOW_FOUNDRY_BASIN.get()) {
			return true;
		}
		if (block instanceof FoundryOutletBlock) {
			return neighbor.getValue(FoundryOutletBlock.FACING) == dir;
		}
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST);
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
		return shape;
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
