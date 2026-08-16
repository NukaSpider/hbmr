package com.hbmr.block.network;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Pipe anchor (1.7.10 {@code FluidPipeAnchor}). Placement face = attachment direction;
 * mesh drawn by {@link com.hbmr.client.PipeAnchorBER} from {@code pipe_anchor.obj} Anchor part.
 */
public class PipeAnchorBlock extends Block implements EntityBlock, FluidNetworkConnectable {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	private static final float MIN = 4.0F;
	private static final float MAX = 12.0F;

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static {
		// 1.7.10 setBlockBounds: dir = meta.opposite(); extend to 0/1 on that axis.
		SHAPES[Direction.DOWN.get3DDataValue()] = Block.box(MIN, 0.0D, MIN, MAX, MAX, MAX);
		SHAPES[Direction.UP.get3DDataValue()] = Block.box(MIN, MIN, MIN, MAX, 16.0D, MAX);
		SHAPES[Direction.NORTH.get3DDataValue()] = Block.box(MIN, MIN, 0.0D, MAX, MAX, MAX);
		SHAPES[Direction.SOUTH.get3DDataValue()] = Block.box(MIN, MIN, MIN, MAX, MAX, 16.0D);
		SHAPES[Direction.WEST.get3DDataValue()] = Block.box(0.0D, MIN, MIN, MAX, MAX, MAX);
		SHAPES[Direction.EAST.get3DDataValue()] = Block.box(MIN, MIN, MIN, 16.0D, MAX, MAX);
	}

	public PipeAnchorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PipeAnchorBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// 1.7.10 onBlockPlaced returns the clicked face (side).
		return this.defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	/**
	 * Fluid ducts attach on every face except into the attachment surface
	 * (opposite of placement face), matching the free sides of the anchor mesh.
	 */
	@Override
	public boolean canFluidConnect(BlockState state, Direction face) {
		return face != state.getValue(FACING).getOpposite();
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
		Direction attach = state.getValue(FACING);
		Direction dir = attach.getOpposite();
		return SHAPES[dir.get3DDataValue()];
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
