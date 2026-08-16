package com.hbmr.block.multiblock;

import com.hbmr.block.network.NetworkPylonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class StructureDummyBlock extends BaseEntityBlock {
	/** Prevents recursive structure teardown. */
	public static boolean safeRem;

	public StructureDummyBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StructureDummyBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof StructureDummyBlockEntity dummy) || dummy.getControllerPos() == null) {
			return InteractionResult.PASS;
		}
		BlockEntity core = level.getBlockEntity(dummy.getControllerPos());
		if (core instanceof MultiblockControllerBlockEntity ctrl) {
			if (ctrl.isCargoElevator()) {
				if (!level.isClientSide) {
					if (!MultiblockControllerBlock.tryUseCargoElevator(level, pos, ctrl, player, hand)) {
						ctrl.toggleCargoElevator();
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			if (ctrl.isAnyToggleDoor()) {
				if (!level.isClientSide) {
					if (player.isShiftKeyDown() && DoorSkins.hasSkins(ctrl.getStructureType())) {
						ctrl.cycleSkin();
					} else {
						ctrl.tryToggle();
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
			BlockState coreState = level.getBlockState(dummy.getControllerPos());
			if (coreState.getBlock() instanceof NetworkPylonBlock pylon) {
				return pylon.kind().columnShape();
			}
			BlockEntity controller = level.getBlockEntity(dummy.getControllerPos());
			if (controller instanceof MultiblockControllerBlockEntity ctrl) {
				if (ctrl.getStructureType() == StructureType.CARGO_ELEVATOR) {
					return CargoElevatorShapes.shapeForCell(dummy.getControllerPos(), pos, ctrl.getLayerCount(),
							ctrl.getCargoExtension(), ctrl.isCargoMoving());
				}
				if (ctrl.isWideSlidingDoor()) {
					return MultiblockControllerBlockEntity.wideDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isFireDoor()) {
					return MultiblockControllerBlockEntity.fireDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isSecureAccessDoor()) {
					return MultiblockControllerBlockEntity.secureDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isLargeVehicleDoor()) {
					return MultiblockControllerBlockEntity.vehicleDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isQeContainmentDoor()) {
					return MultiblockControllerBlockEntity.containmentDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isQeSlidingDoor()) {
					return MultiblockControllerBlockEntity.qeSlidingDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isRoundAirlockDoor()) {
					return MultiblockControllerBlockEntity.airlockDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isSlidingSealDoor()) {
					return MultiblockControllerBlockEntity.slidingSealDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isWaterDoor()) {
					return MultiblockControllerBlockEntity.waterDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isVaultDoor()) {
					return MultiblockControllerBlockEntity.vaultDoorShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isSiloHatch()) {
					return MultiblockControllerBlockEntity.siloHatchShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.isTransitionSeal()) {
					return MultiblockControllerBlockEntity.transitionSealShapeAt(level, dummy.getControllerPos(), pos, false);
				}
				if (ctrl.getStructureType() == StructureType.ELECTRIC_PRESS) {
					BlockState pressState = level.getBlockState(dummy.getControllerPos());
					Direction facing = pressState.hasProperty(MultiblockControllerBlock.FACING)
							? pressState.getValue(MultiblockControllerBlock.FACING)
							: Direction.NORTH;
					return ElectricPressShapes.shapeForCell(dummy.getControllerPos(), pos, facing);
				}
				if (MachineCellShapes.usesFullFootprint(ctrl.getStructureType())) {
					BlockState machineState = level.getBlockState(dummy.getControllerPos());
					Direction facing = machineState.hasProperty(MultiblockControllerBlock.FACING)
							? machineState.getValue(MultiblockControllerBlock.FACING)
							: Direction.NORTH;
					return MachineCellShapes.shapeForCell(ctrl.getStructureType(), dummy.getControllerPos(), pos, facing);
				}
			}
		}
		return getCollisionShape(state, level, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
			BlockState coreState = level.getBlockState(dummy.getControllerPos());
			if (coreState.getBlock() instanceof NetworkPylonBlock pylon) {
				return pylon.kind().columnShape();
			}
			BlockEntity controller = level.getBlockEntity(dummy.getControllerPos());
			if (controller instanceof MultiblockControllerBlockEntity ctrl) {
				if (ctrl.getStructureType() == StructureType.CARGO_ELEVATOR) {
					return CargoElevatorShapes.shapeForCell(dummy.getControllerPos(), pos, ctrl.getLayerCount(),
							ctrl.getCargoExtension(), ctrl.isCargoMoving());
				}
				if (ctrl.isWideSlidingDoor()) {
					return MultiblockControllerBlockEntity.wideDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isFireDoor()) {
					return MultiblockControllerBlockEntity.fireDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isSecureAccessDoor()) {
					return MultiblockControllerBlockEntity.secureDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isLargeVehicleDoor()) {
					return MultiblockControllerBlockEntity.vehicleDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isQeContainmentDoor()) {
					return MultiblockControllerBlockEntity.containmentDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isQeSlidingDoor()) {
					return MultiblockControllerBlockEntity.qeSlidingDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isRoundAirlockDoor()) {
					return MultiblockControllerBlockEntity.airlockDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isSlidingSealDoor()) {
					return MultiblockControllerBlockEntity.slidingSealDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isWaterDoor()) {
					return MultiblockControllerBlockEntity.waterDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isVaultDoor()) {
					return MultiblockControllerBlockEntity.vaultDoorShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isSiloHatch()) {
					return MultiblockControllerBlockEntity.siloHatchShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.isTransitionSeal()) {
					return MultiblockControllerBlockEntity.transitionSealShapeAt(level, dummy.getControllerPos(), pos, true);
				}
				if (ctrl.getStructureType() == StructureType.ELECTRIC_PRESS) {
					BlockState pressState = level.getBlockState(dummy.getControllerPos());
					Direction facing = pressState.hasProperty(MultiblockControllerBlock.FACING)
							? pressState.getValue(MultiblockControllerBlock.FACING)
							: Direction.NORTH;
					return ElectricPressShapes.shapeForCell(dummy.getControllerPos(), pos, facing);
				}
				if (MachineCellShapes.usesFullFootprint(ctrl.getStructureType())) {
					BlockState machineState = level.getBlockState(dummy.getControllerPos());
					Direction facing = machineState.hasProperty(MultiblockControllerBlock.FACING)
							? machineState.getValue(MultiblockControllerBlock.FACING)
							: Direction.NORTH;
					return MachineCellShapes.shapeForCell(ctrl.getStructureType(), dummy.getControllerPos(), pos, facing);
				}
			}
		}
		// Never fall back to a full cube — that causes suffocation / blocked movement.
		return Shapes.empty();
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	/**
	 * Sliding / fire doors: only frame cells support wall attachments.
	 */
	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (isCargoElevatorCell(level, pos)) {
			return getCollisionShape(state, level, pos, CollisionContext.empty());
		}
		if (isSlidingDoorNonFrameCell(level, pos)) {
			return Shapes.empty();
		}
		return Shapes.block();
	}

	private static boolean isCargoElevatorCell(BlockGetter level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof StructureDummyBlockEntity dummy) || dummy.getControllerPos() == null) {
			return false;
		}
		BlockEntity controller = level.getBlockEntity(dummy.getControllerPos());
		return controller instanceof MultiblockControllerBlockEntity ctrl
				&& ctrl.getStructureType() == StructureType.CARGO_ELEVATOR;
	}

	private static boolean isSlidingDoorNonFrameCell(BlockGetter level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof StructureDummyBlockEntity dummy) || dummy.getControllerPos() == null) {
			return false;
		}
		BlockPos core = dummy.getControllerPos();
		BlockEntity controller = level.getBlockEntity(core);
		if (!(controller instanceof MultiblockControllerBlockEntity ctrl)) {
			return false;
		}
		if (ctrl.isShortBlastDoor()) {
			int topY = core.getY() + StructureType.SLIDING_BLAST_DOOR_SHORT.getDims()[0];
			return pos.getY() > core.getY() && pos.getY() < topY;
		}
		BlockState coreState = level.getBlockState(core);
		Direction facing = coreState.hasProperty(MultiblockControllerBlock.FACING)
				? coreState.getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		if (ctrl.isWideSlidingDoor()) {
			if (MultiblockControllerBlockEntity.isWideDoorSideFrame(core, pos, facing)) {
				return false;
			}
			int localY = pos.getY() - core.getY();
			return localY != 0 && localY != 3;
		}
		if (ctrl.isFireDoor()) {
			if (MultiblockControllerBlockEntity.isStructureSideFrame(core, pos, facing, StructureType.FIRE_DOOR)) {
				return false;
			}
			int localY = pos.getY() - core.getY();
			return localY != 0 && localY != 2;
		}
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
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!level.isClientSide && !safeRem) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
				BlockPos core = dummy.getControllerPos();
				BlockState coreState = level.getBlockState(core);
				boolean drop = !player.getAbilities().instabuild;
				if (coreState.getBlock() instanceof NetworkPylonBlock pylon) {
					pylon.destroyFromDummy(level, core, drop);
					return true;
				}
				if (coreState.getBlock() instanceof MultiblockControllerBlock controller) {
					if (controller.getStructureType() == StructureType.CARGO_ELEVATOR
							&& controller.destroyCargoPostColumn(level, core, pos, drop)) {
						return true;
					}
					controller.destroyStructure(level, core, coreState, drop);
					return true;
				}
			}
		}
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !safeRem && !level.isClientSide) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
				BlockPos core = dummy.getControllerPos();
				BlockState coreState = level.getBlockState(core);
				if (coreState.getBlock() instanceof NetworkPylonBlock pylon) {
					pylon.destroyFromDummy(level, core, true);
					return;
				}
				if (coreState.getBlock() instanceof MultiblockControllerBlock controller) {
					if (controller.getStructureType() == StructureType.CARGO_ELEVATOR
							&& controller.destroyCargoPostColumn(level, core, pos, true)) {
						return;
					}
					controller.destroyStructure(level, core, coreState, true);
					return;
				}
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
			BlockState core = level.getBlockState(dummy.getControllerPos());
			if (core.getBlock() instanceof MultiblockControllerBlock || core.getBlock() instanceof NetworkPylonBlock) {
				return new ItemStack(core.getBlock());
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
		return true;
	}
}
