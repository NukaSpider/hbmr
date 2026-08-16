package com.hbmr.block.network;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
 * Size-variant box duct (1.7.10 {@code PowerCableBox} / {@code FluidDuctBox}).
 * Both energy and fluid variants use the {@code hbmr:box_cable} baked model (no block entity).
 */
public class BoxDuctBlock extends Block implements EnergyNetworkConnectable, FluidNetworkConnectable {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final BooleanProperty UP = PipeBlock.UP;
	public static final BooleanProperty DOWN = PipeBlock.DOWN;

	public enum NetworkKind {
		ENERGY,
		FLUID,
		/** Thin pneumatic tube (1.7.10 {@code PneumoTube}) — box duct mesh, own network. */
		PNEUMATIC
	}

	private final NetworkKind kind;
	private final int size;
	@Nullable
	private final BoxDuctMaterial material;

	public BoxDuctBlock(Properties properties, NetworkKind kind, int size, @Nullable BoxDuctMaterial material) {
		super(properties);
		if (size < 0 || size > 4) {
			throw new IllegalArgumentException("Box duct size must be 0–4, got " + size);
		}
		this.kind = kind;
		this.size = size;
		this.material = material;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false));
	}

	public NetworkKind getKind() {
		return kind;
	}

	public int getSize() {
		return size;
	}

	@Nullable
	public BoxDuctMaterial getMaterial() {
		return material;
	}

	/** Fluid boxduct with {@code material == null} is exhaust (1.7.10 {@code FluidDuctBoxExhaust}). */
	public boolean isExhaust() {
		return kind == NetworkKind.FLUID && material == null;
	}

	public boolean isPneumatic() {
		return kind == NetworkKind.PNEUMATIC;
	}

	@Override
	public boolean canEnergyConnect(BlockState state, Direction face) {
		return kind == NetworkKind.ENERGY;
	}

	@Override
	public boolean canFluidConnect(BlockState state, Direction face) {
		return kind == NetworkKind.FLUID;
	}

	@Override
	public boolean isExhaustNetwork() {
		return isExhaust();
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

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			BlockState updated = computeConnections(state, level, pos);
			if (!updated.equals(state)) {
				level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
			}
			for (Direction dir : Direction.values()) {
				BlockPos neighborPos = pos.relative(dir);
				BlockState neighbor = level.getBlockState(neighborPos);
				if (neighbor.getBlock() instanceof BoxDuctBlock other && other.kind == this.kind) {
					BlockState neighborUpdated = other.computeConnections(neighbor, level, neighborPos);
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
		BlockState updated = computeConnections(state, level, pos);
		if (!updated.equals(state)) {
			level.setBlock(pos, updated, Block.UPDATE_ALL);
		}
	}

	protected BlockState computeConnections(BlockState base, BlockGetter level, BlockPos pos) {
		return base
				.setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
				.setValue(EAST, canConnectTo(level, pos, Direction.EAST))
				.setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
				.setValue(WEST, canConnectTo(level, pos, Direction.WEST))
				.setValue(UP, canConnectTo(level, pos, Direction.UP))
				.setValue(DOWN, canConnectTo(level, pos, Direction.DOWN));
	}

	private boolean canConnectTo(BlockGetter level, BlockPos pos, Direction dir) {
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighbor = level.getBlockState(neighborPos);
		Block block = neighbor.getBlock();
		if (kind == NetworkKind.ENERGY) {
			if (block instanceof EnergyNetworkConnectable connectable) {
				return connectable.canEnergyConnect(neighbor, dir.getOpposite());
			}
		} else if (kind == NetworkKind.PNEUMATIC) {
			if (block instanceof BoxDuctBlock other && other.kind == NetworkKind.PNEUMATIC) {
				return true;
			}
			if (block instanceof PneumaticNetworkConnectable connectable) {
				return connectable.canPneumaticConnect(neighbor, dir.getOpposite());
			}
			return false;
		} else {
			if (block instanceof FluidNetworkConnectable connectable) {
				if (block instanceof FluidNetworkBlock fluid
						&& fluid.getMaterial() == FluidDuctMaterial.PNEUMATIC) {
					return false;
				}
				return connectable.canFluidConnect(neighbor, dir.getOpposite())
						&& connectable.isExhaustNetwork() == isExhaustNetwork();
			}
		}
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	/** Arm/straight duct inset — same for energy and fluid size tiers. */
	public static double lowerForSize(int size) {
		double lower = 0.125D;
		for (int i = 0; i < 5; i++) {
			if (size > i) {
				lower += 0.0625D;
			}
		}
		return lower;
	}

	/**
	 * Junction/isolated cube inset. Energy uses the duct size; fluid ducts use a slightly
	 * larger hub ({@code jLower} in 1.7.10 {@code RenderBoxDuct}).
	 */
	public static double junctionLowerForSize(int size, NetworkKind kind) {
		if (kind == NetworkKind.ENERGY || kind == NetworkKind.PNEUMATIC) {
			return lowerForSize(size);
		}
		return 0.0625D + size * 0.0625D;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		double lower = lowerForSize(size) * 16.0D;
		double upper = 16.0D - lower;
		double jLower = junctionLowerForSize(size, kind) * 16.0D;
		double jUpper = 16.0D - jLower;
		boolean n = state.getValue(NORTH);
		boolean s = state.getValue(SOUTH);
		boolean w = state.getValue(WEST);
		boolean e = state.getValue(EAST);
		boolean u = state.getValue(UP);
		boolean d = state.getValue(DOWN);
		int mask = (e ? 32 : 0) + (w ? 16 : 0) + (u ? 8 : 0) + (d ? 4 : 0) + (s ? 2 : 0) + (n ? 1 : 0);
		int count = (e ? 1 : 0) + (w ? 1 : 0) + (u ? 1 : 0) + (d ? 1 : 0) + (s ? 1 : 0) + (n ? 1 : 0);

		// 1.7.10 PneumoTube.setBlockBounds — envelope only toward connected sides
		// (dead ends must not span the empty half of the block).
		if (kind == NetworkKind.PNEUMATIC) {
			return Block.box(
					w ? 0.0D : lower,
					d ? 0.0D : lower,
					n ? 0.0D : lower,
					e ? 16.0D : upper,
					u ? 16.0D : upper,
					s ? 16.0D : upper);
		}

		if (mask == 0) {
			return Block.box(jLower, jLower, jLower, jUpper, jUpper, jUpper);
		}
		if (mask == 0b100000 || mask == 0b010000 || mask == 0b110000) {
			return Block.box(0.0D, lower, lower, 16.0D, upper, upper);
		}
		if (mask == 0b001000 || mask == 0b000100 || mask == 0b001100) {
			return Block.box(lower, 0.0D, lower, upper, 16.0D, upper);
		}
		if (mask == 0b000010 || mask == 0b000001 || mask == 0b000011) {
			return Block.box(lower, lower, 0.0D, upper, upper, 16.0D);
		}

		double hubLo = count == 2 ? lower : jLower;
		double hubHi = 16.0D - hubLo;
		VoxelShape shape = Block.box(hubLo, hubLo, hubLo, hubHi, hubHi, hubHi);
		if (e) {
			shape = Shapes.or(shape, Block.box(hubHi, lower, lower, 16.0D, upper, upper));
		}
		if (w) {
			shape = Shapes.or(shape, Block.box(0.0D, lower, lower, hubLo, upper, upper));
		}
		if (u) {
			shape = Shapes.or(shape, Block.box(lower, hubHi, lower, upper, 16.0D, upper));
		}
		if (d) {
			shape = Shapes.or(shape, Block.box(lower, 0.0D, lower, upper, hubLo, upper));
		}
		if (s) {
			shape = Shapes.or(shape, Block.box(lower, lower, hubHi, upper, upper, 16.0D));
		}
		if (n) {
			shape = Shapes.or(shape, Block.box(lower, lower, 0.0D, upper, upper, hubLo));
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
