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
 * Foundry storage basin ({@code FoundryTank} in 1.7.10). Side faces switch to the
 * outlet texture when a {@code foundry_outlet} (not spill) is attached and facing away.
 */
public class FoundryStorageBasinBlock extends Block {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;

	private static final VoxelShape SHAPE = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
			Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D),
			Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D)
	);

	public FoundryStorageBasinBlock(Properties properties) {
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
				.setValue(NORTH, hasOutlet(level, pos, Direction.NORTH))
				.setValue(EAST, hasOutlet(level, pos, Direction.EAST))
				.setValue(SOUTH, hasOutlet(level, pos, Direction.SOUTH))
				.setValue(WEST, hasOutlet(level, pos, Direction.WEST));
	}

	/** Matches 1.7.10 {@code RenderFoundryTank}: only {@code foundry_outlet}, not slagtap. */
	private static boolean hasOutlet(BlockGetter level, BlockPos pos, Direction dir) {
		BlockState neighbor = level.getBlockState(pos.relative(dir));
		if (neighbor.getBlock() != ModBlocks.FOUNDRY_OUTLET.get()) {
			return false;
		}
		return neighbor.getValue(FoundryOutletBlock.FACING) == dir;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
}
