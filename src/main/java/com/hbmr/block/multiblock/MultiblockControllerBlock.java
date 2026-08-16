package com.hbmr.block.multiblock;

import com.hbmr.registry.ModBlocks;
import com.hbmr.block.network.FluidNetworkConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MultiblockControllerBlock extends BaseEntityBlock implements FluidNetworkConnectable {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	private final StructureType structureType;

	public MultiblockControllerBlock(Properties properties, StructureType structureType) {
		super(properties);
		this.structureType = structureType;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public StructureType getStructureType() {
		return structureType;
	}

	/**
	 * Drainage intake cube: ducts on the intake face + left/right
	 * (1.7.10 {@code TileEntityMachineDrain#getConPos} — not world-down / not pipe-back / not top grill).
	 */
	@Override
	public boolean canFluidConnect(BlockState state, Direction face) {
		if (structureType != StructureType.DRAINAGE_PIPE) {
			return false;
		}
		Direction facing = state.getValue(FACING);
		return face == facing
				|| face == facing.getClockWise()
				|| face == facing.getCounterClockWise();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		MultiblockControllerBlockEntity be = new MultiblockControllerBlockEntity(pos, state);
		be.setStructureType(structureType);
		return be;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (structureType != StructureType.SLIDING_BLAST_DOOR_SHORT
				&& structureType != StructureType.SLIDING_BLAST_DOOR
				&& structureType != StructureType.FIRE_DOOR
				&& structureType != StructureType.SECURE_ACCESS_DOOR
				&& structureType != StructureType.LARGE_VEHICLE_DOOR
				&& structureType != StructureType.QE_CONTAINMENT_DOOR
				&& structureType != StructureType.QE_SLIDING_DOOR
				&& structureType != StructureType.ROUND_AIRLOCK_DOOR
				&& structureType != StructureType.SLIDING_SEAL_DOOR
				&& structureType != StructureType.WATER_DOOR
				&& structureType != StructureType.VT_BLAST_DOOR
				&& structureType != StructureType.SILO_HATCH
				&& structureType != StructureType.SILO_HATCH_LARGE
				&& structureType != StructureType.TRANSITION_SEAL
				&& structureType != StructureType.CARGO_ELEVATOR) {
			return null;
		}
		if (level.isClientSide) {
			if (structureType != StructureType.FIRE_DOOR
					&& structureType != StructureType.SECURE_ACCESS_DOOR
					&& structureType != StructureType.LARGE_VEHICLE_DOOR
					&& structureType != StructureType.QE_CONTAINMENT_DOOR
					&& structureType != StructureType.ROUND_AIRLOCK_DOOR
					&& structureType != StructureType.WATER_DOOR
					&& structureType != StructureType.SILO_HATCH
					&& structureType != StructureType.SILO_HATCH_LARGE
					&& structureType != StructureType.CARGO_ELEVATOR) {
				return null;
			}
			return createTickerHelper(type, com.hbmr.registry.ModBlockEntities.MULTIBLOCK_CONTROLLER.get(),
					MultiblockControllerBlockEntity::clientTick);
		}
		return createTickerHelper(type, com.hbmr.registry.ModBlockEntities.MULTIBLOCK_CONTROLLER.get(),
				MultiblockControllerBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (structureType == StructureType.CARGO_ELEVATOR) {
			if (!level.isClientSide) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof MultiblockControllerBlockEntity ctrl) {
					if (!tryUseCargoElevator(level, pos, ctrl, player, hand)) {
						ctrl.toggleCargoElevator();
					}
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		if (structureType != StructureType.SLIDING_BLAST_DOOR_SHORT
				&& structureType != StructureType.SLIDING_BLAST_DOOR
				&& structureType != StructureType.FIRE_DOOR
				&& structureType != StructureType.SECURE_ACCESS_DOOR
				&& structureType != StructureType.LARGE_VEHICLE_DOOR
				&& structureType != StructureType.QE_CONTAINMENT_DOOR
				&& structureType != StructureType.QE_SLIDING_DOOR
				&& structureType != StructureType.ROUND_AIRLOCK_DOOR
				&& structureType != StructureType.SLIDING_SEAL_DOOR
				&& structureType != StructureType.WATER_DOOR
				&& structureType != StructureType.VT_BLAST_DOOR
				&& structureType != StructureType.SILO_HATCH
				&& structureType != StructureType.SILO_HATCH_LARGE
				&& structureType != StructureType.TRANSITION_SEAL) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof MultiblockControllerBlockEntity ctrl) {
				if (player.isShiftKeyDown() && DoorSkins.hasSkins(structureType)) {
					ctrl.cycleSkin();
				} else {
					ctrl.tryToggle();
				}
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	/**
	 * Right-click any elevator cell with an elevator item to extend the shaft upward
	 * (matches 1.7.10 {@code BlockCargoElevator#onBlockActivated}).
	 */
	public static boolean tryUseCargoElevator(Level level, BlockPos clicked, MultiblockControllerBlockEntity ctrl,
			Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (!(held.getItem() instanceof BlockItem blockItem)
				|| !(blockItem.getBlock() instanceof MultiblockControllerBlock heldBlock)
				|| heldBlock.getStructureType() != StructureType.CARGO_ELEVATOR) {
			return false;
		}
		Direction facing = ctrl.getBlockState().hasProperty(FACING)
				? ctrl.getBlockState().getValue(FACING)
				: Direction.NORTH;
		BlockPos core = ctrl.getBlockPos();
		int nextY = core.getY() + ctrl.getLayerCount();
		if (!MultiblockHelper.checkExtraLayer(level, core, nextY, StructureType.CARGO_ELEVATOR.getDims(), facing)) {
			return false;
		}
		fillElevatorLayer(level, core, nextY, facing);
		ctrl.setHeight(ctrl.getHeight() + 1);
		if (!player.getAbilities().instabuild) {
			held.shrink(1);
		}
		return true;
	}

	/** Package helper used by merge-into-lower and click-to-extend. */
	public static void fillElevatorLayer(Level level, BlockPos core, int layerY, Direction facing) {
		fillLayerDummiesStatic(level, core, layerY, StructureType.CARGO_ELEVATOR.getDims(), facing);
	}

	private static void fillLayerDummiesStatic(Level level, BlockPos core, int layerY, int[] dims, Direction facing) {
		int[] rot = MultiblockHelper.rotateDims(dims, facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		// Full 3×3 every layer (matches 1.7.10) so the moving platform has collision in the center.
		// BER still only draws corner guides on upper layers.
		StructureDummyBlock.safeRem = true;
		try {
			for (int x = core.getX() - w; x <= core.getX() + e; x++) {
				for (int z = core.getZ() - n; z <= core.getZ() + s; z++) {
					BlockPos cell = new BlockPos(x, layerY, z);
					if (cell.equals(core)) {
						continue;
					}
					level.setBlock(cell, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
					BlockEntity be = level.getBlockEntity(cell);
					if (be instanceof StructureDummyBlockEntity dummy) {
						dummy.setControllerPos(core);
					}
				}
			}
		} finally {
			StructureDummyBlock.safeRem = false;
		}
	}

	/**
	 * Break all four corner posts from the broken layer upward (cargo elevator only).
	 * Platform / controller stay. Returns true if handled.
	 */
	public boolean destroyCargoPostColumn(Level level, BlockPos core, BlockPos broken, boolean dropItem) {
		if (structureType != StructureType.CARGO_ELEVATOR || StructureDummyBlock.safeRem || level.isClientSide) {
			return false;
		}
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isCargoElevator()) {
			return false;
		}
		if (!CargoElevatorShapes.isCornerCell(core, broken)) {
			return false;
		}
		int fromY = broken.getY();
		int oldHeight = ctrl.getHeight();
		int topY = core.getY() + oldHeight;
		if (fromY < core.getY() || fromY > topY) {
			return false;
		}

		// Breaking posts on the base layer removes the whole elevator (no leftover base plate).
		if (fromY <= core.getY()) {
			destroyStructure(level, core, level.getBlockState(core), dropItem);
			return true;
		}

		StructureDummyBlock.safeRem = true;
		try {
			// From break layer upward: strip posts; above the base also clear shaft fillers
			// so raised-platform collision cells do not linger.
			for (int y = fromY; y <= topY; y++) {
				for (int dx = -1; dx <= 1; dx++) {
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos cell = new BlockPos(core.getX() + dx, y, core.getZ() + dz);
						if (cell.equals(core)) {
							continue;
						}
						boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
						if (y == core.getY() && !corner) {
							continue; // keep base platform cells
						}
						if (level.getBlockState(cell).is(ModBlocks.STRUCTURE_DUMMY.get())) {
							level.removeBlock(cell, false);
						}
					}
				}
			}
			// Remaining shaft ends one layer below the break.
			ctrl.setHeight(Math.max(0, fromY - core.getY() - 1));
		} finally {
			StructureDummyBlock.safeRem = false;
		}

		// One item per removed extra layer (matches click-to-extend cost).
		int toDrop = dropItem ? oldHeight - ctrl.getHeight() : 0;
		if (toDrop > 0) {
			while (toDrop > 0) {
				int stack = Math.min(toDrop, 64);
				toDrop -= stack;
				Block.popResource(level, broken, new ItemStack(this, stack));
			}
		}
		return true;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		// Redstone edge is handled in the BE ticker (matches 1.7.10).
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// Same as other horizontal machines: front faces the player.
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (structureType == StructureType.CARGO_ELEVATOR) {
			return getCollisionShape(state, level, pos, context);
		}
		if (structureType == StructureType.SLIDING_BLAST_DOOR) {
			return MultiblockControllerBlockEntity.wideDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.FIRE_DOOR) {
			return MultiblockControllerBlockEntity.fireDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.SECURE_ACCESS_DOOR) {
			return MultiblockControllerBlockEntity.secureDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.LARGE_VEHICLE_DOOR) {
			return MultiblockControllerBlockEntity.vehicleDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.QE_CONTAINMENT_DOOR) {
			return MultiblockControllerBlockEntity.containmentDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.QE_SLIDING_DOOR) {
			return MultiblockControllerBlockEntity.qeSlidingDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.ROUND_AIRLOCK_DOOR) {
			return MultiblockControllerBlockEntity.airlockDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.SLIDING_SEAL_DOOR) {
			return MultiblockControllerBlockEntity.slidingSealDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.WATER_DOOR) {
			return MultiblockControllerBlockEntity.waterDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.VT_BLAST_DOOR) {
			return MultiblockControllerBlockEntity.vaultDoorShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.SILO_HATCH || structureType == StructureType.SILO_HATCH_LARGE) {
			return MultiblockControllerBlockEntity.siloHatchShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.TRANSITION_SEAL) {
			return MultiblockControllerBlockEntity.transitionSealShapeAt(level, pos, pos, false);
		}
		if (structureType == StructureType.ELECTRIC_PRESS) {
			return ElectricPressShapes.shapeForCell(pos, pos, state.getValue(FACING));
		}
		if (MachineCellShapes.usesFullFootprint(structureType)) {
			return MachineCellShapes.shapeForCell(structureType, pos, pos, state.getValue(FACING));
		}
		return getCollisionShape(state, level, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (structureType == StructureType.CARGO_ELEVATOR) {
			int layers = 1;
			double extension = 0.0D;
			boolean moving = false;
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof MultiblockControllerBlockEntity ctrl) {
				layers = ctrl.getLayerCount();
				extension = ctrl.getCargoExtension();
				moving = ctrl.isCargoMoving();
			}
			return CargoElevatorShapes.shapeForCell(pos, pos, layers, extension, moving);
		}
		if (structureType == StructureType.SLIDING_BLAST_DOOR) {
			return MultiblockControllerBlockEntity.wideDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.FIRE_DOOR) {
			return MultiblockControllerBlockEntity.fireDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.SECURE_ACCESS_DOOR) {
			return MultiblockControllerBlockEntity.secureDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.LARGE_VEHICLE_DOOR) {
			return MultiblockControllerBlockEntity.vehicleDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.QE_CONTAINMENT_DOOR) {
			return MultiblockControllerBlockEntity.containmentDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.QE_SLIDING_DOOR) {
			return MultiblockControllerBlockEntity.qeSlidingDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.ROUND_AIRLOCK_DOOR) {
			return MultiblockControllerBlockEntity.airlockDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.SLIDING_SEAL_DOOR) {
			return MultiblockControllerBlockEntity.slidingSealDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.WATER_DOOR) {
			return MultiblockControllerBlockEntity.waterDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.VT_BLAST_DOOR) {
			return MultiblockControllerBlockEntity.vaultDoorShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.SILO_HATCH || structureType == StructureType.SILO_HATCH_LARGE) {
			return MultiblockControllerBlockEntity.siloHatchShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.TRANSITION_SEAL) {
			return MultiblockControllerBlockEntity.transitionSealShapeAt(level, pos, pos, true);
		}
		if (structureType == StructureType.ELECTRIC_PRESS) {
			return ElectricPressShapes.shapeForCell(pos, pos, state.getValue(FACING));
		}
		if (MachineCellShapes.usesFullFootprint(structureType)) {
			return MachineCellShapes.shapeForCell(structureType, pos, pos, state.getValue(FACING));
		}
		return Shapes.block();
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}


	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.isClientSide) {
			return;
		}

		Direction facing = state.getValue(FACING);

		if (structureType == StructureType.CARGO_ELEVATOR && tryExtendElevatorBelow(level, pos, facing)) {
			return;
		}

		// MultiblockBlockItem already places at the final core for offset structures.
		BlockPos core = pos;

		if (!MultiblockHelper.checkSpace(level, core, structureType, facing, pos)) {
			StructureDummyBlock.safeRem = true;
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			StructureDummyBlock.safeRem = false;
			if (placer instanceof Player player && !player.getAbilities().instabuild) {
				ItemStack drop = new ItemStack(this);
				if (!player.getInventory().add(drop)) {
					player.drop(drop, false);
				}
			}
			return;
		}

		BlockEntity coreBe = level.getBlockEntity(core);
		if (coreBe instanceof MultiblockControllerBlockEntity ctrl) {
			ctrl.setStructureType(structureType);
			ctrl.setHeight(0);
		}

		fillDummies(level, core, structureType, facing);
	}

	private boolean tryExtendElevatorBelow(Level level, BlockPos placedPos, Direction facing) {
		// Resolve lower elevator from any cell under the click (controller or dummy).
		for (int dy = 1; dy <= 64; dy++) {
			BlockPos below = placedPos.below(dy);
			BlockEntity be = level.getBlockEntity(below);
			BlockPos core = null;
			if (be instanceof MultiblockControllerBlockEntity ctrl && ctrl.isCargoElevator()) {
				core = below;
			} else if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
				BlockEntity coreBe = level.getBlockEntity(dummy.getControllerPos());
				if (coreBe instanceof MultiblockControllerBlockEntity ctrl && ctrl.isCargoElevator()) {
					core = dummy.getControllerPos();
				}
			}
			if (core == null) {
				continue;
			}
			BlockState coreState = level.getBlockState(core);
			if (!coreState.hasProperty(FACING) || coreState.getValue(FACING) != facing) {
				continue;
			}
			BlockEntity coreBe = level.getBlockEntity(core);
			if (!(coreBe instanceof MultiblockControllerBlockEntity ctrl)) {
				continue;
			}
			int nextY = core.getY() + ctrl.getLayerCount();
			if (nextY != placedPos.getY()) {
				continue;
			}
			// Click must land somewhere in this elevator's horizontal footprint.
			int[] rot = MultiblockHelper.rotateDims(structureType.getDims(), facing);
			int dx = placedPos.getX() - core.getX();
			int dz = placedPos.getZ() - core.getZ();
			if (dx < -rot[4] || dx > rot[5] || dz < -rot[2] || dz > rot[3]) {
				continue;
			}
			if (!MultiblockHelper.checkExtraLayer(level, core, nextY, structureType.getDims(), facing)) {
				break;
			}
			StructureDummyBlock.safeRem = true;
			level.setBlock(placedPos, Blocks.AIR.defaultBlockState(), 3);
			StructureDummyBlock.safeRem = false;
			fillLayerDummies(level, core, nextY, structureType.getDims(), facing);
			ctrl.setHeight(ctrl.getHeight() + 1);
			return true;
		}
		return false;
	}

	private void fillDummies(Level level, BlockPos core, StructureType type, Direction facing) {
		StructureDummyBlock.safeRem = true;
		try {
			java.util.HashSet<BlockPos> placed = new java.util.HashSet<>();
			MultiblockHelper.forEachCellWithExtras(core, type, facing, cell -> {
				if (cell.equals(core) || !placed.add(cell)) {
					return;
				}
				level.setBlock(cell, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
				BlockEntity be = level.getBlockEntity(cell);
				if (be instanceof StructureDummyBlockEntity dummy) {
					dummy.setControllerPos(core);
				}
			});
		} finally {
			StructureDummyBlock.safeRem = false;
		}
	}

	private void fillLayerDummies(Level level, BlockPos core, int layerY, int[] dims, Direction facing) {
		fillLayerDummiesStatic(level, core, layerY, dims, facing);
	}

	public void destroyStructure(Level level, BlockPos core, BlockState coreState, boolean dropItem) {
		if (StructureDummyBlock.safeRem || level.isClientSide) {
			return;
		}
		Direction facing = coreState.getValue(FACING);
		int[] dims = structureType.getDims();
		int extraLayers = 0;
		BlockEntity be = level.getBlockEntity(core);
		if (be instanceof MultiblockControllerBlockEntity ctrl) {
			extraLayers = ctrl.getHeight();
		}

		StructureDummyBlock.safeRem = true;
		try {
			java.util.HashSet<BlockPos> removed = new java.util.HashSet<>();
			MultiblockHelper.forEachCellWithExtras(core, structureType, facing, cell -> {
				if (removed.add(cell)) {
					level.removeBlock(cell, false);
				}
			});
			for (int layer = 1; layer <= extraLayers; layer++) {
				int y = core.getY() + layer;
				int[] rot = MultiblockHelper.rotateDims(dims, facing);
				for (int x = core.getX() - rot[4]; x <= core.getX() + rot[5]; x++) {
					for (int z = core.getZ() - rot[2]; z <= core.getZ() + rot[3]; z++) {
						level.removeBlock(new BlockPos(x, y, z), false);
					}
				}
			}
		} finally {
			StructureDummyBlock.safeRem = false;
		}

		if (dropItem) {
			int count = structureType == StructureType.CARGO_ELEVATOR ? extraLayers + 1 : 1;
			while (count > 0) {
				int stack = Math.min(count, 64);
				count -= stack;
				Block.popResource(level, core, new ItemStack(this, stack));
			}
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!level.isClientSide && !StructureDummyBlock.safeRem) {
			destroyStructure(level, pos, state, !player.getAbilities().instabuild);
			return true;
		}
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !StructureDummyBlock.safeRem && !level.isClientSide) {
			destroyStructure(level, pos, state, true);
			return;
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	@Deprecated
	public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
		return true;
	}
}
