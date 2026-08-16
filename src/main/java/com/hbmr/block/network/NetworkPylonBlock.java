package com.hbmr.block.network;

import com.hbmr.block.multiblock.MultiblockHelper;
import com.hbmr.block.multiblock.StructureDummyBlock;
import com.hbmr.block.multiblock.StructureDummyBlockEntity;
import com.hbmr.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Electricity pylon / connector with TESR-style OBJ mesh
 * ({@link com.hbmr.client.NetworkPylonBER}), matching 1.7.10 {@code Pylon*} / {@code Connector*}.
 * Tall pylons place structure-dummy cells (1.7 Dummyable column / footprint).
 */
public class NetworkPylonBlock extends Block implements EntityBlock, EnergyNetworkConnectable {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	/** Prevents recursive teardown when clearing the dummy column. */
	public static boolean safeRem;

	private final NetworkPylonKind kind;

	public NetworkPylonBlock(Properties properties, NetworkPylonKind kind) {
		super(properties);
		this.kind = kind;
		Direction defaultFacing = kind.isConnector() ? Direction.UP : Direction.NORTH;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, defaultFacing));
	}

	public NetworkPylonKind kind() {
		return kind;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NetworkPylonBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (kind.isConnector()) {
			// 1.7.10 ConnectorRedWire#onBlockPlaced: meta = clicked side.
			return this.defaultBlockState().setValue(FACING, context.getClickedFace());
		}
		Level level = context.getLevel();
		BlockPos click = context.getClickedPos();
		Direction facing = context.getHorizontalDirection().getOpposite();
		BlockPos core = kind.placementOffset() != 0
				? MultiblockHelper.coreFromClick(click, facing, kind.placementOffset())
				: click;
		if (kind.usesColumnDummies() && !canPlaceStructure(level, core, facing)) {
			return null;
		}
		return this.defaultBlockState().setValue(FACING, facing);
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
		if (!kind.isConnector()) {
			return kind.shape();
		}
		// Heavy-duty spans the full block on the facing axis (ConnectorRedWireSuper).
		boolean full = kind == NetworkPylonKind.CONNECTOR_SUPER;
		return connectorShape(state.getValue(FACING), full);
	}

	/** Matches 1.7.10 ConnectorRedWire / ConnectorRedWireSuper bounds. */
	private static VoxelShape connectorShape(Direction facing, boolean fullLength) {
		double min = 5.0D / 16.0D;
		double max = 11.0D / 16.0D;
		double end = fullLength ? 1.0D : max;
		double start = fullLength ? 0.0D : min;
		return switch (facing) {
			case UP -> Shapes.box(min, 0.0D, min, max, end, max);
			case DOWN -> Shapes.box(min, start, min, max, 1.0D, max);
			case NORTH -> Shapes.box(min, min, start, max, max, 1.0D);
			case SOUTH -> Shapes.box(min, min, 0.0D, max, max, end);
			case WEST -> Shapes.box(start, min, min, 1.0D, max, max);
			case EAST -> Shapes.box(0.0D, min, min, end, max, max);
		};
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide && kind.usesColumnDummies()) {
			placeStructure(level, pos, state.getValue(FACING));
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
			FluidState fluid) {
		if (!level.isClientSide && !safeRem && kind.usesColumnDummies()) {
			clearStructure(level, pos, state.getValue(FACING));
		}
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !safeRem && !level.isClientSide && kind.usesColumnDummies()) {
			clearStructure(level, pos, state.getValue(FACING));
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	/** Called when a shaft dummy is broken — remove core + remaining structure. */
	public void destroyFromDummy(Level level, BlockPos core, boolean drop) {
		if (safeRem) {
			return;
		}
		safeRem = true;
		try {
			BlockState coreState = level.getBlockState(core);
			Direction facing = coreState.hasProperty(FACING) ? coreState.getValue(FACING) : Direction.NORTH;
			clearStructure(level, core, facing);
			if (coreState.getBlock() instanceof NetworkPylonBlock) {
				level.destroyBlock(core, drop);
			}
		} finally {
			safeRem = false;
		}
	}

	private boolean canPlaceStructure(Level level, BlockPos core, Direction facing) {
		int[] dims = kind.footprintDims();
		if (dims != null) {
			return MultiblockHelper.checkSpace(level, core, dims, facing, core);
		}
		for (int dy = 1; dy <= kind.columnAbove(); dy++) {
			if (!level.getBlockState(core.above(dy)).canBeReplaced()) {
				return false;
			}
		}
		return true;
	}

	private void placeStructure(Level level, BlockPos core, Direction facing) {
		int[] dims = kind.footprintDims();
		if (dims != null) {
			MultiblockHelper.forEachCell(core, dims, facing, cell -> {
				if (cell.equals(core)) {
					return;
				}
				level.setBlock(cell, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
				BlockEntity be = level.getBlockEntity(cell);
				if (be instanceof StructureDummyBlockEntity dummy) {
					dummy.setControllerPos(core);
				}
			});
			return;
		}
		for (int dy = 1; dy <= kind.columnAbove(); dy++) {
			BlockPos cell = core.above(dy);
			level.setBlock(cell, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
			BlockEntity be = level.getBlockEntity(cell);
			if (be instanceof StructureDummyBlockEntity dummy) {
				dummy.setControllerPos(core);
			}
		}
	}

	private void clearStructure(Level level, BlockPos core, Direction facing) {
		boolean prev = StructureDummyBlock.safeRem;
		StructureDummyBlock.safeRem = true;
		try {
			int[] dims = kind.footprintDims();
			if (dims != null) {
				MultiblockHelper.forEachCell(core, dims, facing, cell -> {
					if (!cell.equals(core) && level.getBlockState(cell).is(ModBlocks.STRUCTURE_DUMMY.get())) {
						level.removeBlock(cell, false);
					}
				});
				return;
			}
			for (int dy = 1; dy <= kind.columnAbove(); dy++) {
				BlockPos cell = core.above(dy);
				if (level.getBlockState(cell).is(ModBlocks.STRUCTURE_DUMMY.get())) {
					level.removeBlock(cell, false);
				}
			}
		} finally {
			StructureDummyBlock.safeRem = prev;
		}
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
