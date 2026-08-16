package com.hbmr.block.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Six-way thin fluid duct (1.7.10 {@code FluidDuctStandard} / {@code fluid_duct_neo}).
 * Mesh is drawn by {@link com.hbmr.client.PipeNeoBER} from {@code pipe_neo.obj}.
 * Material is baked into the block instance (separate registry IDs).
 */
public class FluidNetworkBlock extends Block implements EntityBlock, FluidNetworkConnectable {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final BooleanProperty UP = PipeBlock.UP;
	public static final BooleanProperty DOWN = PipeBlock.DOWN;

	/** Matches pipe_neo cross-section (±0.188) and 1.7.10 {@code 0.3125…0.6875}. */
	private static final double MIN = 5.0D;
	private static final double MAX = 11.0D;

	private static final VoxelShape CORE = Block.box(MIN, MIN, MIN, MAX, MAX, MAX);
	private static final VoxelShape ARM_EAST = Block.box(MAX, MIN, MIN, 16.0D, MAX, MAX);
	private static final VoxelShape ARM_WEST = Block.box(0.0D, MIN, MIN, MIN, MAX, MAX);
	private static final VoxelShape ARM_UP = Block.box(MIN, MAX, MIN, MAX, 16.0D, MAX);
	private static final VoxelShape ARM_DOWN = Block.box(MIN, 0.0D, MIN, MAX, MIN, MAX);
	private static final VoxelShape ARM_SOUTH = Block.box(MIN, MIN, MAX, MAX, MAX, 16.0D);
	private static final VoxelShape ARM_NORTH = Block.box(MIN, MIN, 0.0D, MAX, MAX, MIN);

	private static final VoxelShape STRAIGHT_X = Block.box(0.0D, MIN, MIN, 16.0D, MAX, MAX);
	private static final VoxelShape STRAIGHT_Y = Block.box(MIN, 0.0D, MIN, MAX, 16.0D, MAX);
	private static final VoxelShape STRAIGHT_Z = Block.box(MIN, MIN, 0.0D, MAX, MAX, 16.0D);

	private final FluidDuctMaterial material;

	public FluidNetworkBlock(Properties properties, FluidDuctMaterial material) {
		super(properties);
		this.material = material;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false));
	}

	public FluidDuctMaterial getMaterial() {
		return material;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FluidNetworkBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return computeConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return computeConnections(state, level, pos);
	}

	private BlockState computeConnections(BlockState base, BlockGetter level, BlockPos pos) {
		return base
				.setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
				.setValue(EAST, canConnectTo(level, pos, Direction.EAST))
				.setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
				.setValue(WEST, canConnectTo(level, pos, Direction.WEST))
				.setValue(UP, canConnectTo(level, pos, Direction.UP))
				.setValue(DOWN, canConnectTo(level, pos, Direction.DOWN));
	}

	public static boolean canConnectTo(BlockGetter level, BlockPos pos, Direction dir) {
		BlockState self = level.getBlockState(pos);
		Block selfBlock = self.getBlock();
		boolean selfExhaust = selfBlock instanceof FluidNetworkConnectable c && c.isExhaustNetwork();
		boolean selfPneumatic = selfBlock instanceof FluidNetworkBlock selfFluid
				&& selfFluid.getMaterial() == FluidDuctMaterial.PNEUMATIC;

		BlockPos neighborPos = pos.relative(dir);
		BlockState neighbor = level.getBlockState(neighborPos);
		Block block = neighbor.getBlock();
		if (block instanceof FluidNetworkConnectable connectable) {
			if (!connectable.canFluidConnect(neighbor, dir.getOpposite())
					|| connectable.isExhaustNetwork() != selfExhaust) {
				return false;
			}
			if (selfBlock instanceof FluidNetworkBlock selfFluid && block instanceof FluidNetworkBlock otherFluid) {
				boolean selfP = selfFluid.getMaterial() == FluidDuctMaterial.PNEUMATIC;
				boolean otherP = otherFluid.getMaterial() == FluidDuctMaterial.PNEUMATIC;
				if (selfP != otherP) {
					return false;
				}
			} else if (selfPneumatic) {
				// Pneumatic tubes only mesh with other pneumatic FluidNetworkBlocks.
				return false;
			} else if (block instanceof FluidNetworkBlock otherFluid
					&& otherFluid.getMaterial() == FluidDuctMaterial.PNEUMATIC) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	private static int connectionMask(BlockState state) {
		return (state.getValue(EAST) ? 32 : 0)
				| (state.getValue(WEST) ? 16 : 0)
				| (state.getValue(UP) ? 8 : 0)
				| (state.getValue(DOWN) ? 4 : 0)
				| (state.getValue(SOUTH) ? 2 : 0)
				| (state.getValue(NORTH) ? 1 : 0);
	}

	/**
	 * Selection outline — 1.7.10 {@code setBlockBoundsBasedOnState}
	 * (single AABB; disconnected = full block).
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int mask = connectionMask(state);
		if (mask == 0) {
			return Shapes.block();
		}
		if (mask == 0b100000 || mask == 0b010000 || mask == 0b110000) {
			return STRAIGHT_X;
		}
		if (mask == 0b001000 || mask == 0b000100 || mask == 0b001100) {
			return STRAIGHT_Y;
		}
		if (mask == 0b000010 || mask == 0b000001 || mask == 0b000011) {
			return STRAIGHT_Z;
		}
		double x0 = state.getValue(WEST) ? 0.0D : MIN;
		double y0 = state.getValue(DOWN) ? 0.0D : MIN;
		double z0 = state.getValue(NORTH) ? 0.0D : MIN;
		double x1 = state.getValue(EAST) ? 16.0D : MAX;
		double y1 = state.getValue(UP) ? 16.0D : MAX;
		double z1 = state.getValue(SOUTH) ? 16.0D : MAX;
		return Block.box(x0, y0, z0, x1, y1, z1);
	}

	/**
	 * Collision — 1.7.10 {@code addCollisionBoxesToList} (per-arm stubs / straights).
	 */
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
			CollisionContext context) {
		int mask = connectionMask(state);
		if (mask == 0) {
			return Shapes.or(ARM_EAST, ARM_WEST, ARM_UP, ARM_DOWN, ARM_SOUTH, ARM_NORTH);
		}
		if (mask == 0b100000 || mask == 0b010000 || mask == 0b110000) {
			return STRAIGHT_X;
		}
		if (mask == 0b001000 || mask == 0b000100 || mask == 0b001100) {
			return STRAIGHT_Y;
		}
		if (mask == 0b000010 || mask == 0b000001 || mask == 0b000011) {
			return STRAIGHT_Z;
		}
		VoxelShape shape = CORE;
		if (state.getValue(EAST)) {
			shape = Shapes.or(shape, ARM_EAST);
		}
		if (state.getValue(WEST)) {
			shape = Shapes.or(shape, ARM_WEST);
		}
		if (state.getValue(UP)) {
			shape = Shapes.or(shape, ARM_UP);
		}
		if (state.getValue(DOWN)) {
			shape = Shapes.or(shape, ARM_DOWN);
		}
		if (state.getValue(SOUTH)) {
			shape = Shapes.or(shape, ARM_SOUTH);
		}
		if (state.getValue(NORTH)) {
			shape = Shapes.or(shape, ARM_NORTH);
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
