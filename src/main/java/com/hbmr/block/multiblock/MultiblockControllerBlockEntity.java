package com.hbmr.block.multiblock;

import com.hbmr.registry.ModBlockEntities;
import com.hbmr.registry.ModBlocks;
import com.hbmr.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Multiblock controller.
 * Short sliding blast door open/close matches 1.7.10 {@code TileEntityBlastDoor}.
 * Wide sliding blast door matches 1.7.10 {@code TileEntityDoorGeneric} + {@code DoorDecl.SLIDE_DOOR}.
 */
public class MultiblockControllerBlockEntity extends BlockEntity {
	public static final String TAG_HEIGHT = "Height";
	public static final String TAG_TYPE = "StructureType";
	public static final String TAG_DOOR_STATE = "DoorState";
	public static final String TAG_IS_OPENING = "IsOpening";
	public static final String TAG_ANIM_TICK = "AnimTick";
	public static final String TAG_ANIM_START = "AnimStart";
	public static final String TAG_REDSTONED = "Redstoned";
	public static final String TAG_OPEN_TICKS = "OpenTicks";
	public static final String TAG_SKIN = "Skin";
	public static final String TAG_EXTENSION = "Extension";
	public static final String TAG_TARGET_EXTENSION = "TargetExtension";

	/** Matches 1.7.10 {@code TileEntityCargoElevator#speed}: 2 blocks/second. */
	public static final double CARGO_ELEVATOR_SPEED = 2.0D / 20.0D;

	/** Short door: 0 closed, 1 moving, 2 open — same as 1.7.10 BlastDoor. */
	public static final int DOOR_CLOSED = 0;
	public static final int DOOR_MOVING = 1;
	public static final int DOOR_OPEN = 2;

	/** Wide door: matches TileEntityDoorGeneric. */
	public static final int WIDE_CLOSED = 0;
	public static final int WIDE_OPEN = 1;
	public static final int WIDE_CLOSING = 2;
	public static final int WIDE_OPENING = 3;

	private static final VoxelShape OPEN_TOP = Shapes.box(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final VoxelShape OPEN_BOTTOM_SELECT = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.08D, 1.0D);
	private static final VoxelShape FIRE_OPEN_TOP = Shapes.box(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final VoxelShape FIRE_OPEN_BOTTOM_SELECT = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.1D, 1.0D);
	/** Secure door open y=1 selection (1.7.10 forCollision ? 0 : 0.0625). */
	private static final VoxelShape SECURE_OPEN_Y1_SELECT = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
	/** Large vehicle door open frame: bottom 1/8, top 1/2. */
	private static final VoxelShape VEHICLE_OPEN_BOTTOM = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
	private static final VoxelShape VEHICLE_OPEN_TOP = Shapes.box(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final VoxelShape CONTAINMENT_OPEN_BOTTOM = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
	private static final VoxelShape AIRLOCK_OPEN_TOP = Shapes.box(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final VoxelShape AIRLOCK_OPEN_BOTTOM_SELECT = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

	/** Extra layers above the base footprint (0 = single layer). Used by cargo elevator. */
	private int height;
	private StructureType structureType = StructureType.FIRE_DOOR;

	/** Cargo elevator platform travel (blocks above rest). Matches 1.7.10. */
	private int targetExtension;
	private double extension;
	private double prevExtension;
	private double syncExtension;

	private int doorState = DOOR_CLOSED;
	private boolean isOpening;
	private int animTick;
	private long animStartGameTime;
	private boolean redstoned;
	/** Wide door: close once opening finishes if redstone dropped mid-open. */
	private boolean deferWideClose;
	/** Wide door open progress 0…{@link SlidingBlastDoorAnim#TIME_TO_OPEN}. */
	private int openTicks;
	/** Fire / vault door texture variant (matches 1.7.10 {@code skinIndex}). */
	private int skinIndex;

	public MultiblockControllerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MULTIBLOCK_CONTROLLER.get(), pos, state);
		if (state.getBlock() instanceof MultiblockControllerBlock controller) {
			this.structureType = controller.getStructureType();
		}
	}

	public StructureType getStructureType() {
		return structureType;
	}

	public void setStructureType(StructureType structureType) {
		this.structureType = structureType;
		setChanged();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		int newHeight = Math.max(0, height);
		boolean shortened = newHeight < this.height;
		this.height = newHeight;
		if (targetExtension > this.height) {
			targetExtension = this.height;
		}
		// When the shaft is shortened, ride the platform down — do not leave it in the sky.
		if (shortened && extension > this.height) {
			targetExtension = this.height;
		}
		setChanged();
		syncToClient();
	}

	public int getLayerCount() {
		return height + 1;
	}

	public double getCargoExtension() {
		return extension;
	}

	/** Smoothed platform height for BER. */
	public double getCargoExtension(float partialTick) {
		return prevExtension + (extension - prevExtension) * partialTick;
	}

	/** True while the platform is traveling between rest positions. */
	public boolean isCargoMoving() {
		return Math.abs(extension - targetExtension) > 1.0E-3D;
	}

	/**
	 * Right-click toggle: go to top if heading/at bottom, otherwise go to bottom.
	 * Mid-travel reverse uses current travel direction (matches 1.7.10).
	 */
	public void toggleCargoElevator() {
		if (level == null || level.isClientSide || !isCargoElevator()) {
			return;
		}
		if (targetExtension == 0) {
			targetExtension = height;
		} else {
			targetExtension = 0;
		}
		setChanged();
		syncToClient();
	}

	public int getDoorState() {
		return doorState;
	}

	public boolean isDoorOpening() {
		return isOpening;
	}

	public int getOpenTicks() {
		return openTicks;
	}

	public int getSkinIndex() {
		return skinIndex;
	}

	/** Cycle fire/vault skin; returns true if a skin was applied. */
	public boolean cycleSkin() {
		if (level == null || level.isClientSide || !DoorSkins.hasSkins(structureType)) {
			return false;
		}
		skinIndex = (skinIndex + 1) % DoorSkins.skinCount(structureType);
		setChanged();
		syncToClient();
		return true;
	}

	public boolean isShortBlastDoor() {
		return structureType == StructureType.SLIDING_BLAST_DOOR_SHORT;
	}

	public boolean isWideSlidingDoor() {
		return structureType == StructureType.SLIDING_BLAST_DOOR;
	}

	public boolean isFireDoor() {
		return structureType == StructureType.FIRE_DOOR;
	}

	public boolean isSecureAccessDoor() {
		return structureType == StructureType.SECURE_ACCESS_DOOR;
	}

	public boolean isLargeVehicleDoor() {
		return structureType == StructureType.LARGE_VEHICLE_DOOR;
	}

	public boolean isQeContainmentDoor() {
		return structureType == StructureType.QE_CONTAINMENT_DOOR;
	}

	public boolean isQeSlidingDoor() {
		return structureType == StructureType.QE_SLIDING_DOOR;
	}

	public boolean isRoundAirlockDoor() {
		return structureType == StructureType.ROUND_AIRLOCK_DOOR;
	}

	public boolean isSlidingSealDoor() {
		return structureType == StructureType.SLIDING_SEAL_DOOR;
	}

	public boolean isWaterDoor() {
		return structureType == StructureType.WATER_DOOR;
	}

	public boolean isVaultDoor() {
		return structureType == StructureType.VT_BLAST_DOOR;
	}

	public boolean isSiloHatch() {
		return structureType == StructureType.SILO_HATCH || structureType == StructureType.SILO_HATCH_LARGE;
	}

	public boolean isSiloHatchLarge() {
		return structureType == StructureType.SILO_HATCH_LARGE;
	}

	public boolean isTransitionSeal() {
		return structureType == StructureType.TRANSITION_SEAL;
	}

	public boolean isCargoElevator() {
		return structureType == StructureType.CARGO_ELEVATOR;
	}

	/** Generic DoorDecl-style doors (wide sliding + fire + vault + silo hatches + transition seal + secure). */
	public boolean isGenericAnimatedDoor() {
		return isWideSlidingDoor() || isFireDoor() || isVaultDoor() || isSiloHatch() || isTransitionSeal()
				|| isSecureAccessDoor() || isLargeVehicleDoor() || isQeContainmentDoor() || isQeSlidingDoor()
				|| isRoundAirlockDoor() || isSlidingSealDoor() || isWaterDoor();
	}

	public boolean isAnyToggleDoor() {
		return isShortBlastDoor() || isGenericAnimatedDoor();
	}

	public boolean isAnySlidingDoor() {
		return isShortBlastDoor() || isWideSlidingDoor();
	}

	/**
	 * Visual door extension 0 (fully open) … 5 (fully closed), matching {@code RenderBlastDoor} timer.
	 */
	public double getDoorAnimTimer(float partialTick) {
		if (!isShortBlastDoor() || level == null) {
			return 5.0D;
		}
		if (doorState == DOOR_CLOSED) {
			return 5.0D;
		}
		if (doorState == DOOR_OPEN) {
			return 0.0D;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		double progress = Math.max(0.0D, Math.min(1.0D, elapsed / 100.0D));
		return isOpening ? 5.0D * (1.0D - progress) : 5.0D * progress;
	}

	/** Smooth openTicks for wide-door BER (0 closed … 24 open). */
	public float getWideOpenTicks(float partialTick) {
		if (!isWideSlidingDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return SlidingBlastDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(SlidingBlastDoorAnim.TIME_TO_OPEN, elapsed));
		}
		// CLOSING: started fully open, counts down
		return (float) Math.max(0.0D, Math.min(SlidingBlastDoorAnim.TIME_TO_OPEN,
				SlidingBlastDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getWideDoorSlide(float partialTick) {
		float ticks = getWideOpenTicks(partialTick);
		return SlidingBlastDoorAnim.doorAmount(ticks, doorState) * SlidingBlastDoorAnim.MAX_OPEN;
	}

	public double getWideDoorLockDegrees(float partialTick) {
		float ticks = getWideOpenTicks(partialTick);
		return SlidingBlastDoorAnim.lockAmount(ticks, doorState) * 90.0D;
	}

	/** Smooth openTicks for fire-door BER (0 closed … 160 open). */
	public float getFireOpenTicks(float partialTick) {
		if (!isFireDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return FireDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(FireDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(FireDoorAnim.TIME_TO_OPEN,
				FireDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getFireDoorRaise(float partialTick) {
		return FireDoorAnim.raise(getFireOpenTicks(partialTick), doorState);
	}

	/** Smooth openTicks for secure-access BER (0 closed … 120 open). */
	public float getSecureOpenTicks(float partialTick) {
		if (!isSecureAccessDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return SecureDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(SecureDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(SecureDoorAnim.TIME_TO_OPEN,
				SecureDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getSecureDoorRaise(float partialTick) {
		return SecureDoorAnim.raise(getSecureOpenTicks(partialTick), doorState);
	}

	/** Smooth openTicks for large vehicle door BER (0 closed … 60 open). */
	public float getVehicleOpenTicks(float partialTick) {
		if (!isLargeVehicleDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return VehicleDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(VehicleDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(VehicleDoorAnim.TIME_TO_OPEN,
				VehicleDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getVehicleDoorSlide(float partialTick) {
		return VehicleDoorAnim.slide(getVehicleOpenTicks(partialTick), doorState);
	}

	public float getContainmentOpenTicks(float partialTick) {
		if (!isQeContainmentDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return ContainmentDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(ContainmentDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(ContainmentDoorAnim.TIME_TO_OPEN,
				ContainmentDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getContainmentDoorRaise(float partialTick) {
		return ContainmentDoorAnim.raise(getContainmentOpenTicks(partialTick), doorState);
	}

	public float getQeSlidingOpenTicks(float partialTick) {
		if (!isQeSlidingDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return QeSlidingDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(QeSlidingDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(QeSlidingDoorAnim.TIME_TO_OPEN,
				QeSlidingDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getQeSlidingDoorSlide(float partialTick) {
		return QeSlidingDoorAnim.slide(getQeSlidingOpenTicks(partialTick), doorState);
	}

	public float getAirlockOpenTicks(float partialTick) {
		if (!isRoundAirlockDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return AirlockDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(AirlockDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(AirlockDoorAnim.TIME_TO_OPEN,
				AirlockDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getAirlockDoorSlide(float partialTick) {
		return AirlockDoorAnim.slide(getAirlockOpenTicks(partialTick), doorState);
	}

	public float getSlidingSealOpenTicks(float partialTick) {
		if (!isSlidingSealDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return SlidingSealDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(SlidingSealDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(SlidingSealDoorAnim.TIME_TO_OPEN,
				SlidingSealDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getSlidingSealDoorSlide(float partialTick) {
		return SlidingSealDoorAnim.slide(getSlidingSealOpenTicks(partialTick), doorState);
	}

	public float getWaterOpenTicks(float partialTick) {
		if (!isWaterDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return WaterDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(WaterDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(WaterDoorAnim.TIME_TO_OPEN,
				WaterDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getWaterDoorDegrees(float partialTick) {
		return WaterDoorAnim.doorDegrees(getWaterOpenTicks(partialTick), doorState);
	}

	public double getWaterDoorBolt(float partialTick) {
		return WaterDoorAnim.boltAmount(getWaterOpenTicks(partialTick), doorState);
	}

	public float getVaultOpenTicks(float partialTick) {
		if (!isVaultDoor() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return VaultDoorAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(VaultDoorAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(VaultDoorAnim.TIME_TO_OPEN,
				VaultDoorAnim.TIME_TO_OPEN - elapsed));
	}

	public double getVaultPull(float partialTick) {
		return VaultDoorAnim.pullAmount(getVaultOpenTicks(partialTick), doorState);
	}

	public double getVaultSlide(float partialTick) {
		return VaultDoorAnim.slideAmount(getVaultOpenTicks(partialTick), doorState) * VaultDoorAnim.SLIDE_DISTANCE;
	}

	public double getVaultRollDegrees(float partialTick) {
		double slide = getVaultSlide(partialTick);
		double circumference = VaultDoorAnim.DOOR_DIAMETER * Math.PI;
		return 360.0D * slide / circumference;
	}

	public float getSiloHatchOpenTicks(float partialTick) {
		if (!isSiloHatch() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return SiloHatchAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(SiloHatchAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(SiloHatchAnim.TIME_TO_OPEN,
				SiloHatchAnim.TIME_TO_OPEN - elapsed));
	}

	public float getSiloHatchLift(float partialTick) {
		return SiloHatchAnim.liftY(getSiloHatchOpenTicks(partialTick), doorState);
	}

	public float getSiloHatchRotDegrees(float partialTick) {
		return SiloHatchAnim.rotDegrees(getSiloHatchOpenTicks(partialTick), doorState);
	}

	/** Smooth openTicks for transition seal BER (0 closed … 480 open). */
	public float getTransitionSealOpenTicks(float partialTick) {
		if (!isTransitionSeal() || level == null) {
			return 0.0F;
		}
		if (doorState == WIDE_OPEN) {
			return TransitionSealAnim.TIME_TO_OPEN;
		}
		if (doorState == WIDE_CLOSED) {
			return 0.0F;
		}
		double elapsed = (level.getGameTime() - animStartGameTime) + partialTick;
		if (doorState == WIDE_OPENING) {
			return (float) Math.max(0.0D, Math.min(TransitionSealAnim.TIME_TO_OPEN, elapsed));
		}
		return (float) Math.max(0.0D, Math.min(TransitionSealAnim.TIME_TO_OPEN,
				TransitionSealAnim.TIME_TO_OPEN - elapsed));
	}

	/**
	 * Collision / selection for a cell of the wide sliding door.
	 * Side frame columns always stay solid. Interior columns clear from the center
	 * outward as the panels slide (same {@link SlidingBlastDoorAnim#doorAmount} curve
	 * as the visual), matching the short door’s hitboxes-follow-motion behavior.
	 */
	public VoxelShape shapeForWideDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		if (isWideDoorSideFrame(core, cell, facing)) {
			return Shapes.block();
		}
		int dist = widthDistFromCenter(core, cell, facing);
		if (!isWideDoorColumnPassable(dist)) {
			return Shapes.block();
		}
		int localY = cell.getY() - core.getY();
		if (localY == 3) {
			return OPEN_TOP;
		}
		if (localY == 0) {
			return forCollision ? Shapes.empty() : OPEN_BOTTOM_SELECT;
		}
		return Shapes.empty();
	}

	/**
	 * Left/right posts: extreme cells along the door's width axis.
	 */
	public static boolean isWideDoorSideFrame(BlockPos core, BlockPos cell, Direction facing) {
		return isStructureSideFrame(core, cell, facing, StructureType.SLIDING_BLAST_DOOR);
	}

	public static boolean isStructureSideFrame(BlockPos core, BlockPos cell, Direction facing, StructureType type) {
		int[] rot = MultiblockHelper.rotateDims(type.getDims(), facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		if (w + e > 0) {
			return cell.getX() == core.getX() - w || cell.getX() == core.getX() + e;
		}
		if (n + s > 0) {
			return cell.getZ() == core.getZ() - n || cell.getZ() == core.getZ() + s;
		}
		return false;
	}

	/**
	 * Outer half of each side post — matches 1.7.10 {@code DoorDecl.FIRE_DOOR} open bounds
	 * for {@code z == -2} / {@code z == 1}, and the visual pillar width.
	 */
	public static VoxelShape fireDoorSideShape(BlockPos core, BlockPos cell, Direction facing) {
		int[] rot = MultiblockHelper.rotateDims(StructureType.FIRE_DOOR.getDims(), facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		if (w + e > 0) {
			if (cell.getX() == core.getX() - w) {
				return Shapes.box(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
			}
			if (cell.getX() == core.getX() + e) {
				return Shapes.box(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			}
		}
		if (n + s > 0) {
			if (cell.getZ() == core.getZ() - n) {
				return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
			}
			if (cell.getZ() == core.getZ() + s) {
				return Shapes.box(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
			}
		}
		return Shapes.block();
	}

	/** Blocks from door center along the width axis (0 = core column). */
	public static int widthDistFromCenter(BlockPos core, BlockPos cell, Direction facing) {
		int[] rot = MultiblockHelper.rotateDims(StructureType.SLIDING_BLAST_DOOR.getDims(), facing);
		if (rot[4] + rot[5] > 0) {
			return Math.abs(cell.getX() - core.getX());
		}
		return Math.abs(cell.getZ() - core.getZ());
	}

	public static int widthDistFromCenter(BlockPos core, BlockPos cell, Direction facing, StructureType type) {
		int[] rot = MultiblockHelper.rotateDims(type.getDims(), facing);
		if (rot[4] + rot[5] > 0) {
			return Math.abs(cell.getX() - core.getX());
		}
		return Math.abs(cell.getZ() - core.getZ());
	}

	/**
	 * Interior column cleared when the sliding panels have moved past it.
	 * Uses the same doorAmount 0…1 as the BER so hitboxes track the mesh.
	 * Reach maps to the 5-block doorway ({@code dist} 0…2); side posts ({@code dist} 3) never clear.
	 */
	private boolean isWideDoorColumnPassable(int distFromCenter) {
		if (distFromCenter >= 3) {
			return false;
		}
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			// Match BER timing (game-time interpolation), not just integer openTicks.
			ticks = getWideOpenTicks(1.0F);
		}
		float open = SlidingBlastDoorAnim.doorAmount(ticks, doorState);
		// Center clears first; ±2 clear as open → 1 (reach 2.5).
		float reach = open * 2.5F;
		return distFromCenter + 0.5F <= reach;
	}

	public static VoxelShape wideDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isWideSlidingDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForWideDoorCell(core, cell, forCollision);
	}

	/**
	 * Fire door collision tracks the rising panel: gap from the bottom grows with raise
	 * (0…{@link FireDoorAnim#MAX_RAISE}). Side posts stay solid.
	 */
	public VoxelShape shapeForFireDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		if (isStructureSideFrame(core, cell, facing, StructureType.FIRE_DOOR)) {
			return fireDoorSideShape(core, cell, facing);
		}
		int localY = cell.getY() - core.getY();
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getFireOpenTicks(1.0F);
		}
		double raise = FireDoorAnim.raise(ticks, doorState);
		if (raise >= localY + 1.0D) {
			if (localY == 0) {
				return forCollision ? Shapes.empty() : FIRE_OPEN_BOTTOM_SELECT;
			}
			return Shapes.empty();
		}
		if (raise <= localY) {
			return Shapes.block();
		}
		// Partially uncovered: solid remaining door in the upper part of this cell
		double cut = raise - localY;
		if (localY >= 2 && cut >= 0.75D) {
			return FIRE_OPEN_TOP;
		}
		return Shapes.box(0.0D, cut, 0.0D, 1.0D, 1.0D, 1.0D);
	}

	public static VoxelShape fireDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isFireDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForFireDoorCell(core, cell, forCollision);
	}

	/**
	 * Secure access: y0 floor stays solid; thin door slab tracks the rising panel
	 * (same raise math as the BER / fire door). Top lintel remains at y=4 when open.
	 */
	public VoxelShape shapeForSecureDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();
		if (localY == 0) {
			return Shapes.block();
		}
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getSecureOpenTicks(1.0F);
		}
		// Door leaf is authored at +1 Y (see MultiblockBER / RenderSecureDoor); raise is added on top.
		double doorBottom = 1.0D + SecureDoorAnim.raise(ticks, doorState);
		if (doorBottom >= localY + 1.0D) {
			if (localY == 4) {
				return secureDoorSlab(facing, 0.5D, 1.0D, 0.15D, 0.85D);
			}
			if (localY == 1) {
				return forCollision ? Shapes.empty() : SECURE_OPEN_Y1_SELECT;
			}
			return Shapes.empty();
		}
		if (doorBottom <= localY) {
			if (localY == 4 && doorBottom >= 4.0D) {
				return secureDoorSlab(facing, 0.5D, 1.0D, 0.15D, 0.85D);
			}
			return secureDoorSlab(facing, 0.0D, 1.0D, 0.375D, 0.625D);
		}
		double cut = doorBottom - localY;
		if (localY == 4 && cut >= 0.5D) {
			return secureDoorSlab(facing, 0.5D, 1.0D, 0.15D, 0.85D);
		}
		return secureDoorSlab(facing, cut, 1.0D, 0.375D, 0.625D);
	}

	/** Thin door slab in the depth axis for the given facing (authored as Z for SOUTH). */
	private static VoxelShape secureDoorSlab(Direction facing, double minY, double maxY, double minD, double maxD) {
		return switch (facing) {
			case EAST, WEST -> Shapes.box(minD, minY, 0.0D, maxD, maxY, 1.0D);
			default -> Shapes.box(0.0D, minY, minD, 1.0D, maxY, maxD);
		};
	}

	public static VoxelShape secureDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isSecureAccessDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForSecureDoorCell(core, cell, forCollision);
	}

	/**
	 * Large vehicle door (7×1×6): closed = full cubes. Open frame keeps ½-block sides + top,
	 * ⅛ bottom, and 3 full cubes at each rounded top corner. Interior clears as panels slide.
	 */
	public VoxelShape shapeForVehicleDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();
		int dist = widthDistFromCenter(core, cell, facing, StructureType.LARGE_VEHICLE_DOOR);
		boolean side = isStructureSideFrame(core, cell, facing, StructureType.LARGE_VEHICLE_DOOR);

		if (doorState == WIDE_CLOSED) {
			return Shapes.block();
		}

		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getVehicleOpenTicks(1.0F);
		}
		float amount = VehicleDoorAnim.doorAmount(ticks, doorState);
		boolean cleared = !side && dist + 0.5F <= amount * (float) VehicleDoorAnim.MAX_OPEN;

		if (!cleared && !side) {
			return Shapes.block();
		}

		// Open frame / cleared doorway
		if (isVehicleTopCornerFull(dist, localY)) {
			return Shapes.block();
		}
		if (localY == 0) {
			return forCollision ? VEHICLE_OPEN_BOTTOM : VEHICLE_OPEN_BOTTOM;
		}
		if (localY == 5) {
			return VEHICLE_OPEN_TOP;
		}
		if (side) {
			return vehicleDoorSideHalf(core, cell, facing);
		}
		return Shapes.empty();
	}

	/** Outer half of each ±3 side post (0.5 block). */
	public static VoxelShape vehicleDoorSideHalf(BlockPos core, BlockPos cell, Direction facing) {
		int[] rot = MultiblockHelper.rotateDims(StructureType.LARGE_VEHICLE_DOOR.getDims(), facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		if (w + e > 0) {
			if (cell.getX() == core.getX() - w) {
				return Shapes.box(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
			}
			if (cell.getX() == core.getX() + e) {
				return Shapes.box(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			}
		}
		if (n + s > 0) {
			if (cell.getZ() == core.getZ() - n) {
				return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
			}
			if (cell.getZ() == core.getZ() + s) {
				return Shapes.box(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
			}
		}
		return Shapes.block();
	}

	/** Rounded top: 3 full blocks per corner — (±3,y4), (±3,y5), (±2,y5). */
	private static boolean isVehicleTopCornerFull(int widthDist, int localY) {
		if (widthDist == 3 && localY >= 4) {
			return true;
		}
		return widthDist == 2 && localY == 5;
	}

	public static VoxelShape vehicleDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isLargeVehicleDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForVehicleDoorCell(core, cell, forCollision);
	}

	/**
	 * QE containment (3×1×3): closed = half-depth door slab. Hitbox tracks the rising panel
	 * (same raise as the BER) until fully clear — no side/top frame strips when open.
	 * Bottom keeps a thin select box so the open door stays clickable.
	 */
	public VoxelShape shapeForContainmentDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();

		if (doorState == WIDE_CLOSED) {
			return containmentDoorSlab(facing, 0.0D, 1.0D);
		}

		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getContainmentOpenTicks(1.0F);
		}
		double raise = ContainmentDoorAnim.raise(ticks, doorState);

		if (raise >= localY + 1.0D) {
			if (localY == 0) {
				return forCollision ? Shapes.empty() : CONTAINMENT_OPEN_BOTTOM;
			}
			return Shapes.empty();
		}
		if (raise <= localY) {
			return containmentDoorSlab(facing, 0.0D, 1.0D);
		}
		return containmentDoorSlab(facing, raise - localY, 1.0D);
	}

	/** Half-depth door slab (authored Z 0.5…1 for SOUTH). */
	private static VoxelShape containmentDoorSlab(Direction facing, double minY, double maxY) {
		return switch (facing) {
			case NORTH -> Shapes.box(0.0D, minY, 0.0D, 1.0D, maxY, 0.5D);
			case SOUTH -> Shapes.box(0.0D, minY, 0.5D, 1.0D, maxY, 1.0D);
			case WEST -> Shapes.box(0.0D, minY, 0.0D, 0.5D, maxY, 1.0D);
			case EAST -> Shapes.box(0.5D, minY, 0.0D, 1.0D, maxY, 1.0D);
			default -> Shapes.box(0.0D, minY, 0.5D, 1.0D, maxY, 1.0D);
		};
	}

	public static VoxelShape containmentDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isQeContainmentDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForContainmentDoorCell(core, cell, forCollision);
	}

	/**
	 * QE sliding door (2×1×2): closed = thin depth slab. As soon as opening starts, collision
	 * clears instantly so you can walk through; selection box stays so you can click to close.
	 */
	public VoxelShape shapeForQeSlidingDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		VoxelShape select = qeSlidingDoorSlab(facing);
		if (doorState == WIDE_CLOSED) {
			return select;
		}
		// Open / opening / closing: no collision, keep select hitbox for toggle.
		return forCollision ? Shapes.empty() : select;
	}

	/** Thin door slab — matches 1.7.10 {@code z = 1-0.1875 … 1} for SOUTH. */
	private static VoxelShape qeSlidingDoorSlab(Direction facing) {
		double d = 0.1875D;
		return switch (facing) {
			case NORTH -> Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, d);
			case SOUTH -> Shapes.box(0.0D, 0.0D, 1.0D - d, 1.0D, 1.0D, 1.0D);
			case WEST -> Shapes.box(0.0D, 0.0D, 0.0D, d, 1.0D, 1.0D);
			case EAST -> Shapes.box(1.0D - d, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			default -> Shapes.box(0.0D, 0.0D, 1.0D - d, 1.0D, 1.0D, 1.0D);
		};
	}

	public static VoxelShape qeSlidingDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isQeSlidingDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForQeSlidingDoorCell(core, cell, forCollision);
	}

	/**
	 * Sliding seal door (1×1×2): same open behavior as QE sliding — collision clears instantly,
	 * selection slab stays for click-to-close. Closed slab depth matches 1.7.10 (0.25).
	 */
	public VoxelShape shapeForSlidingSealDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		VoxelShape select = slidingSealDoorSlab(facing);
		if (doorState == WIDE_CLOSED) {
			return select;
		}
		return forCollision ? Shapes.empty() : select;
	}

	/** Thin door slab — matches 1.7.10 {@code z = 1-0.25 … 1} for SOUTH. */
	private static VoxelShape slidingSealDoorSlab(Direction facing) {
		double d = 0.25D;
		return switch (facing) {
			case NORTH -> Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, d);
			case SOUTH -> Shapes.box(0.0D, 0.0D, 1.0D - d, 1.0D, 1.0D, 1.0D);
			case WEST -> Shapes.box(0.0D, 0.0D, 0.0D, d, 1.0D, 1.0D);
			case EAST -> Shapes.box(1.0D - d, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			default -> Shapes.box(0.0D, 0.0D, 1.0D - d, 1.0D, 1.0D, 1.0D);
		};
	}

	public static VoxelShape slidingSealDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isSlidingSealDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForSlidingSealDoorCell(core, cell, forCollision);
	}

	/**
	 * Water door (3×1×3): closed = thin depth slab. Open: slight top/bottom collision;
	 * left/right sides keep a small select hitbox only (no collision); center clears.
	 */
	public VoxelShape shapeForWaterDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();
		boolean side = isStructureSideFrame(core, cell, facing, StructureType.WATER_DOOR);

		if (doorState == WIDE_CLOSED) {
			return waterDoorSlab(facing, 0.0D, 1.0D);
		}

		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getWaterOpenTicks(1.0F);
		}
		float doorAmt = WaterDoorAnim.doorAmount(ticks, doorState);
		boolean cleared = doorAmt >= 0.05F || doorState == WIDE_OPEN;

		if (side) {
			// Small select strip only — walkable, still clickable
			VoxelShape select = waterDoorSideSelect(core, cell, facing);
			return forCollision ? Shapes.empty() : select;
		}
		if (!cleared) {
			return waterDoorSlab(facing, 0.0D, 1.0D);
		}
		if (localY == 0) {
			return waterDoorSlab(facing, 0.0D, 0.15D);
		}
		if (localY == 2) {
			return waterDoorSlab(facing, 0.85D, 1.0D);
		}
		return Shapes.empty();
	}

	/** Depth slab authored Z 0.75…1 for SOUTH. */
	private static VoxelShape waterDoorSlab(Direction facing, double minY, double maxY) {
		return switch (facing) {
			case NORTH -> Shapes.box(0.0D, minY, 0.0D, 1.0D, maxY, 0.25D);
			case SOUTH -> Shapes.box(0.0D, minY, 0.75D, 1.0D, maxY, 1.0D);
			case WEST -> Shapes.box(0.0D, minY, 0.0D, 0.25D, maxY, 1.0D);
			case EAST -> Shapes.box(0.75D, minY, 0.0D, 1.0D, maxY, 1.0D);
			default -> Shapes.box(0.0D, minY, 0.75D, 1.0D, maxY, 1.0D);
		};
	}

	/** Outer eighth of each side post (select only). */
	private static VoxelShape waterDoorSideSelect(BlockPos core, BlockPos cell, Direction facing) {
		int[] rot = MultiblockHelper.rotateDims(StructureType.WATER_DOOR.getDims(), facing);
		int n = rot[2], s = rot[3], w = rot[4], e = rot[5];
		VoxelShape depth = waterDoorSlab(facing, 0.0D, 1.0D);
		VoxelShape strip;
		if (w + e > 0) {
			if (cell.getX() == core.getX() - w) {
				strip = Shapes.box(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
			} else if (cell.getX() == core.getX() + e) {
				strip = Shapes.box(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			} else {
				return depth;
			}
		} else if (n + s > 0) {
			if (cell.getZ() == core.getZ() - n) {
				strip = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
			} else if (cell.getZ() == core.getZ() + s) {
				strip = Shapes.box(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
			} else {
				return depth;
			}
		} else {
			return depth;
		}
		return Shapes.join(depth, strip, net.minecraft.world.phys.shapes.BooleanOp.AND);
	}

	public static VoxelShape waterDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isWaterDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForWaterDoorCell(core, cell, forCollision);
	}

	/**
	 * Round airlock (4×1×4): closed = full cubes. Open: y0–2 clears smoothly with the slide;
	 * top corners (±sides at y=3) stay full; middle top cells are half-block.
	 */
	public VoxelShape shapeForAirlockDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();
		int dist = widthDistFromCenter(core, cell, facing, StructureType.ROUND_AIRLOCK_DOOR);
		boolean side = isStructureSideFrame(core, cell, facing, StructureType.ROUND_AIRLOCK_DOOR);

		if (doorState == WIDE_CLOSED) {
			return Shapes.block();
		}

		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getAirlockOpenTicks(1.0F);
		}
		float amount = AirlockDoorAnim.doorAmount(ticks, doorState);
		// Width spans ±2; clear from center outward as panels slide (fully open clears all).
		boolean cleared = dist <= amount * 2.0F + 1.0e-3F;

		if (localY == 3) {
			if (side) {
				return Shapes.block();
			}
			return cleared ? AIRLOCK_OPEN_TOP : Shapes.block();
		}

		// Bottom 4×1×3 — clear smoothly
		if (cleared) {
			if (localY == 0) {
				return forCollision ? Shapes.empty() : AIRLOCK_OPEN_BOTTOM_SELECT;
			}
			return Shapes.empty();
		}
		return Shapes.block();
	}

	public static VoxelShape airlockDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isRoundAirlockDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForAirlockDoorCell(core, cell, forCollision);
	}

	/**
	 * Vault door: floor always solid; side posts + top row (y=4) always solid;
	 * only the doorway 3×3 (y 1–3, width ±1 on the door plane) clears — matching
	 * {@code DoorDecl.VAULT_DOOR} {@code getDoorOpenRanges() {{-1,1,0,3,3,2}}} /
	 * {@code getBlockBound} (y==0 stays solid; open range never includes y=4).
	 */
	public VoxelShape shapeForVaultDoorCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int localY = cell.getY() - core.getY();
		if (localY == 0) {
			return Shapes.block();
		}
		// Top row + anything above open-range stay solid (was incorrectly clearing with slide).
		if (localY < 1 || localY > 3) {
			return Shapes.block();
		}
		if (isStructureSideFrame(core, cell, facing, StructureType.VT_BLAST_DOOR)) {
			return Shapes.block();
		}
		int dist = widthDistFromCenter(core, cell, facing, StructureType.VT_BLAST_DOOR);
		if (dist > 1) {
			return Shapes.block();
		}
		// Depth-slab extras are outside the open range — keep collision.
		if (!isOnVaultDoorPlane(core, cell, facing)) {
			return Shapes.block();
		}
		if (isVaultDoorwayClear()) {
			return Shapes.empty();
		}
		return Shapes.block();
	}

	/** Main door slab only (extra depth footprint is not in {@code getDoorOpenRanges}). */
	private static boolean isOnVaultDoorPlane(BlockPos core, BlockPos cell, Direction facing) {
		return switch (facing) {
			case NORTH, SOUTH -> cell.getZ() == core.getZ();
			case EAST, WEST -> cell.getX() == core.getX();
			default -> true;
		};
	}

	/**
	 * 1.7.10 clears open-range cells from {@code openTicks/timeToOpen} (during pull),
	 * not from slide. Whole 3×3 is clear by tick 40 (end of pull) — sooner than slide-based.
	 */
	private boolean isVaultDoorwayClear() {
		if (doorState == WIDE_OPEN) {
			return true;
		}
		if (doorState == WIDE_CLOSED) {
			return false;
		}
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getVaultOpenTicks(1.0F);
		}
		// Full doorway passable once pull finishes (~40 ticks); sooner than waiting on slide.
		return ticks >= 40.0F;
	}

	public static VoxelShape vaultDoorShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isVaultDoor()) {
			return Shapes.block();
		}
		return ctrl.shapeForVaultDoorCell(core, cell, forCollision);
	}

	/**
	 * Silo hatch: rim stays solid; DoorDecl open-range cells clear after {@link SiloHatchAnim#CLEAR_TICKS}.
	 */
	public VoxelShape shapeForSiloHatchCell(BlockPos core, BlockPos cell, boolean forCollision) {
		int dx = cell.getX() - core.getX();
		int dz = cell.getZ() - core.getZ();
		if (!SiloHatchAnim.isOpenRangeCell(dx, dz, isSiloHatchLarge())) {
			return Shapes.block();
		}
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getSiloHatchOpenTicks(1.0F);
		}
		if (SiloHatchAnim.doorwayClear(ticks, doorState)) {
			return Shapes.empty();
		}
		return Shapes.block();
	}

	public static VoxelShape siloHatchShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isSiloHatch()) {
			return Shapes.block();
		}
		return ctrl.shapeForSiloHatchCell(core, cell, forCollision);
	}

	/**
	 * Transition seal: DoorDecl open-range cells clear progressively over {@link TransitionSealAnim#TIME_TO_OPEN}.
	 */
	public VoxelShape shapeForTransitionSealCell(BlockPos core, BlockPos cell, boolean forCollision) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int[] local = TransitionSealAnim.worldToLocalOffset(
				cell.getX() - core.getX(),
				cell.getY() - core.getY(),
				cell.getZ() - core.getZ(),
				facing);
		float ticks = openTicks;
		if (level != null && (doorState == WIDE_OPENING || doorState == WIDE_CLOSING)) {
			ticks = getTransitionSealOpenTicks(1.0F);
		} else if (doorState == WIDE_OPEN) {
			ticks = TransitionSealAnim.TIME_TO_OPEN;
		} else if (doorState == WIDE_CLOSED) {
			ticks = 0.0F;
		}
		if (TransitionSealAnim.isClearedOpenRangeCell(local[0], local[1], local[2], ticks)) {
			return forCollision ? Shapes.empty() : OPEN_BOTTOM_SELECT;
		}
		return Shapes.block();
	}

	public static VoxelShape transitionSealShapeAt(BlockGetter level, BlockPos core, BlockPos cell, boolean forCollision) {
		BlockEntity be = level.getBlockEntity(core);
		if (!(be instanceof MultiblockControllerBlockEntity ctrl) || !ctrl.isTransitionSeal()) {
			return Shapes.block();
		}
		return ctrl.shapeForTransitionSealCell(core, cell, forCollision);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MultiblockControllerBlockEntity be) {
		if (be.isCargoElevator()) {
			be.tickCargoElevator(level, pos);
		} else if (be.isShortBlastDoor()) {
			be.tickShortDoor(level, pos);
		} else if (be.isWideSlidingDoor()) {
			be.tickWideDoor(level, pos);
		} else if (be.isFireDoor()) {
			be.tickFireDoor(level, pos);
		} else if (be.isSecureAccessDoor()) {
			be.tickSecureDoor(level, pos);
		} else if (be.isLargeVehicleDoor()) {
			be.tickVehicleDoor(level, pos);
		} else if (be.isQeContainmentDoor()) {
			be.tickContainmentDoor(level, pos);
		} else if (be.isQeSlidingDoor()) {
			be.tickQeSlidingDoor(level, pos);
		} else if (be.isRoundAirlockDoor()) {
			be.tickAirlockDoor(level, pos);
		} else if (be.isSlidingSealDoor()) {
			be.tickSlidingSealDoor(level, pos);
		} else if (be.isWaterDoor()) {
			be.tickWaterDoor(level, pos);
		} else if (be.isVaultDoor()) {
			be.tickVaultDoor(level, pos);
		} else if (be.isSiloHatch()) {
			be.tickSiloHatch(level, pos);
		} else if (be.isTransitionSeal()) {
			be.tickTransitionSeal(level, pos);
		}
	}

	/**
	 * Merge into a lower elevator if one sits directly under this controller, otherwise
	 * move the platform (matches 1.7.10 {@code TileEntityCargoElevator}).
	 */
	private void tickCargoElevator(Level level, BlockPos pos) {
		prevExtension = extension;

		if (tryMergeCargoIntoLower(level, pos)) {
			return;
		}

		if (validateCargoShaft(level, pos)) {
			setChanged();
			syncToClient();
		}

		stepCargoExtension();
		stickCargoRiders(level, pos);

		if (extension != prevExtension) {
			setChanged();
			// Lightweight BE sync only — sendBlockUpdated every tick causes visible stutter.
			syncCargoMotionToClient();
		}
	}

	/**
	 * If corner posts were removed, shrink {@link #height} to match the remaining shaft.
	 * With no poles left at all, snap the platform away (no floating deck).
	 */
	private boolean validateCargoShaft(Level level, BlockPos pos) {
		int oldHeight = height;
		double oldExtension = extension;
		int oldTarget = targetExtension;

		while (height > 0) {
			if (hasCargoCornerAt(level, pos, height)) {
				break;
			}
			height--;
		}

		boolean anyPole = hasAnyCargoPole(level, pos);
		if (!anyPole) {
			// Every guide post is gone — remove the whole elevator (no leftover base/deck).
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof MultiblockControllerBlock block) {
				block.destroyStructure(level, pos, state, true);
			}
			return true;
		}

		if (height == oldHeight && targetExtension == oldTarget && extension == oldExtension) {
			return false;
		}

		if (targetExtension > height) {
			targetExtension = height;
		}

		if (height < oldHeight) {
			StructureDummyBlock.safeRem = true;
			try {
				for (int y = pos.getY() + height + 1; y <= pos.getY() + oldHeight; y++) {
					for (int dx = -1; dx <= 1; dx++) {
						for (int dz = -1; dz <= 1; dz++) {
							BlockPos cell = new BlockPos(pos.getX() + dx, y, pos.getZ() + dz);
							if (level.getBlockState(cell).is(ModBlocks.STRUCTURE_DUMMY.get())) {
								level.removeBlock(cell, false);
							}
						}
					}
				}
			} finally {
				StructureDummyBlock.safeRem = false;
			}
		}
		return true;
	}

	private static boolean hasCargoCornerAt(Level level, BlockPos core, int layer) {
		for (int cx = -1; cx <= 1; cx += 2) {
			for (int cz = -1; cz <= 1; cz += 2) {
				BlockPos corner = new BlockPos(core.getX() + cx, core.getY() + layer, core.getZ() + cz);
				if (level.getBlockState(corner).is(ModBlocks.STRUCTURE_DUMMY.get())) {
					return true;
				}
			}
		}
		return false;
	}

	/** True if any guide post cell still exists in the shaft footprint. */
	public boolean hasAnyCargoPole(Level level, BlockPos pos) {
		int layers = Math.max(height + 1, 1);
		for (int i = 0; i < layers; i++) {
			if (hasCargoCornerAt(level, pos, i)) {
				return true;
			}
		}
		// Also scan one extra in case height is already 0 but base corners linger / are gone.
		return hasCargoCornerAt(level, pos, 0);
	}

	/** Advance {@link #extension} toward {@link #targetExtension} at cargo elevator speed. */
	private void stepCargoExtension() {
		if (targetExtension > height) {
			targetExtension = height;
		}
		if (extension < targetExtension) {
			extension = Math.min(extension + CARGO_ELEVATOR_SPEED, targetExtension);
		} else if (extension > targetExtension) {
			// Allow extension > height while descending after a shaft shorten.
			extension = Math.max(extension - CARGO_ELEVATOR_SPEED, targetExtension);
		}
		if (extension < 0.0D) {
			extension = 0.0D;
		}
	}

	/** @return true if this controller was absorbed into a lower shaft */
	private boolean tryMergeCargoIntoLower(Level level, BlockPos pos) {
		BlockPos below = pos.below();
		BlockEntity belowBe = level.getBlockEntity(below);
		BlockPos lowerCore = null;
		if (belowBe instanceof MultiblockControllerBlockEntity lower && lower.isCargoElevator()) {
			lowerCore = below;
		} else if (belowBe instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
			BlockEntity coreBe = level.getBlockEntity(dummy.getControllerPos());
			if (coreBe instanceof MultiblockControllerBlockEntity lower && lower.isCargoElevator()
					&& dummy.getControllerPos().getX() == pos.getX()
					&& dummy.getControllerPos().getZ() == pos.getZ()) {
				lowerCore = dummy.getControllerPos();
			}
		}
		if (lowerCore == null || lowerCore.equals(pos)) {
			return false;
		}
		BlockEntity coreBe = level.getBlockEntity(lowerCore);
		if (!(coreBe instanceof MultiblockControllerBlockEntity lower)) {
			return false;
		}
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int added = getLayerCount();
		int startY = pos.getY();
		int endY = pos.getY() + height;
		for (int y = startY; y <= endY; y++) {
			MultiblockControllerBlock.fillElevatorLayer(level, lowerCore, y, facing);
		}
		// Retarget any remaining dummies that still point at this controller.
		int[] rot = MultiblockHelper.rotateDims(StructureType.CARGO_ELEVATOR.getDims(), facing);
		StructureDummyBlock.safeRem = true;
		try {
			for (int y = startY; y <= endY; y++) {
				for (int x = pos.getX() - rot[4]; x <= pos.getX() + rot[5]; x++) {
					for (int z = pos.getZ() - rot[2]; z <= pos.getZ() + rot[3]; z++) {
						BlockPos cell = new BlockPos(x, y, z);
						BlockEntity cellBe = level.getBlockEntity(cell);
						if (cellBe instanceof StructureDummyBlockEntity dummy
								&& pos.equals(dummy.getControllerPos())) {
							dummy.setControllerPos(lowerCore);
						}
					}
				}
			}
			level.setBlock(pos, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
			BlockEntity replaced = level.getBlockEntity(pos);
			if (replaced instanceof StructureDummyBlockEntity dummy) {
				dummy.setControllerPos(lowerCore);
			}
		} finally {
			StructureDummyBlock.safeRem = false;
		}
		lower.setHeight(lower.getHeight() + added);
		return true;
	}

	/**
	 * Carry riders with the moving platform (matches 1.7.10 lift loop).
	 * Deck collision is disabled while moving, so this is what keeps you on the elevator.
	 */
	private void stickCargoRiders(Level level, BlockPos pos) {
		if (extension == prevExtension) {
			return;
		}
		double platformDelta = extension - prevExtension;
		double platformTop = pos.getY() + 1.0D + extension;
		// Swept volume between previous and current deck height (1.7.10).
		double liftUpper = pos.getY() + 1.0D + Math.max(extension, prevExtension) + 0.25D;
		double liftLower = pos.getY() + 1.0D + Math.min(extension, prevExtension) - 0.35D;
		AABB box = new AABB(pos.getX() - 0.99D, liftLower, pos.getZ() - 0.99D,
				pos.getX() + 1.99D, liftUpper, pos.getZ() + 1.99D);
		List<Entity> riders = level.getEntities(null, box);
		for (Entity e : riders) {
			var motion = e.getDeltaMovement();
			// Real jump: upward speed clearly above what the elevator itself applies.
			if (motion.y > Math.max(platformDelta, 0.0D) + 0.2D) {
				continue;
			}
			AABB bb = e.getBoundingBox();
			double midX = (bb.minX + bb.maxX) * 0.5D;
			double midZ = (bb.minZ + bb.maxZ) * 0.5D;
			if (midX < pos.getX() - 0.99D || midX > pos.getX() + 1.99D
					|| midZ < pos.getZ() - 0.99D || midZ > pos.getZ() + 1.99D) {
				continue;
			}
			if (bb.minY < liftLower || bb.minY > liftUpper) {
				continue;
			}
			double yOff = e.getY() - bb.minY;
			// Seat flush on the deck (no sink — sink caused sneak clip and reverse drop-through).
			e.setPos(e.getX(), platformTop + yOff, e.getZ());
			e.setOnGround(true);
			e.fallDistance = 0.0F;
			e.setDeltaMovement(motion.x, platformDelta, motion.z);
		}
	}

	private void tickCargoElevatorClient(Level level, BlockPos pos) {
		prevExtension = extension;
		double diff = syncExtension - extension;
		if (Math.abs(diff) <= CARGO_ELEVATOR_SPEED) {
			extension = syncExtension;
		} else {
			extension += Math.copySign(CARGO_ELEVATOR_SPEED, diff);
		}
		if (targetExtension > height) {
			targetExtension = height;
		}
		// Client must carry the local player too while deck collision is off.
		stickCargoRiders(level, pos);
	}

	private void tickShortDoor(Level level, BlockPos pos) {
		boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above(6));
		if (powered) {
			if (!redstoned) {
				tryToggle();
			}
			redstoned = true;
		} else {
			redstoned = false;
		}

		if (doorState != DOOR_MOVING) {
			animTick = 0;
			return;
		}

		animTick++;
		if (isOpening) {
			if (animTick >= 0) {
				removeDoorDummy(level, pos.above(1));
			}
			if (animTick >= 20) {
				removeDoorDummy(level, pos.above(2));
			}
			if (animTick >= 40) {
				removeDoorDummy(level, pos.above(3));
			}
			if (animTick >= 60) {
				removeDoorDummy(level, pos.above(4));
			}
			if (animTick >= 80) {
				removeDoorDummy(level, pos.above(5));
			}
		} else {
			if (animTick >= 20) {
				placeDoorDummy(level, pos.above(5));
			}
			if (animTick >= 40) {
				placeDoorDummy(level, pos.above(4));
			}
			if (animTick >= 60) {
				placeDoorDummy(level, pos.above(3));
			}
			if (animTick >= 80) {
				placeDoorDummy(level, pos.above(2));
			}
			if (animTick >= 100) {
				placeDoorDummy(level, pos.above(1));
			}
		}

		if (animTick >= 100) {
			if (isOpening) {
				finishShortOpen(level, pos);
			} else {
				finishShortClose(level, pos);
			}
		}

		setChanged();
		syncToClient();
	}

	private void tickWideDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.SLIDING_BLAST_DOOR, SlidingBlastDoorAnim.TIME_TO_OPEN,
				() -> beginWideOpen(), () -> beginWideClose(),
				ModSounds.SLIDING_DOOR_OPENED.get(), ModSounds.SLIDING_DOOR_SHUT.get());
	}

	private void tickFireDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.FIRE_DOOR, FireDoorAnim.TIME_TO_OPEN,
				() -> beginFireOpen(), () -> beginFireClose(),
				ModSounds.WGH_STOP.get(), ModSounds.WGH_STOP.get());
	}

	private void tickSecureDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.SECURE_ACCESS_DOOR, SecureDoorAnim.TIME_TO_OPEN,
				() -> beginSecureOpen(), () -> beginSecureClose(),
				ModSounds.GARAGE_STOP.get(), ModSounds.GARAGE_STOP.get());
	}

	private void tickVehicleDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.LARGE_VEHICLE_DOOR, VehicleDoorAnim.TIME_TO_OPEN,
				() -> beginVehicleOpen(), () -> beginVehicleClose(),
				ModSounds.GARAGE_STOP.get(), ModSounds.GARAGE_STOP.get());
	}

	private void tickContainmentDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.QE_CONTAINMENT_DOOR, ContainmentDoorAnim.TIME_TO_OPEN,
				() -> beginContainmentOpen(), () -> beginContainmentClose(),
				ModSounds.WGH_STOP.get(), ModSounds.WGH_STOP.get());
	}

	private void tickQeSlidingDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.QE_SLIDING_DOOR, QeSlidingDoorAnim.TIME_TO_OPEN,
				() -> beginQeSlidingOpen(), () -> beginQeSlidingClose(),
				ModSounds.QE_SLIDING_OPENED.get(), ModSounds.QE_SLIDING_SHUT.get());
	}

	private void tickAirlockDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.ROUND_AIRLOCK_DOOR, AirlockDoorAnim.TIME_TO_OPEN,
				() -> beginAirlockOpen(), () -> beginAirlockClose(),
				ModSounds.GARAGE_STOP.get(), ModSounds.GARAGE_STOP.get());
	}

	private void tickSlidingSealDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.SLIDING_SEAL_DOOR, SlidingSealDoorAnim.TIME_TO_OPEN,
				() -> beginSlidingSealOpen(), () -> beginSlidingSealClose(),
				ModSounds.SLIDING_SEAL_STOP.get(), ModSounds.SLIDING_SEAL_STOP.get());
	}

	private void tickWaterDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.WATER_DOOR, WaterDoorAnim.TIME_TO_OPEN,
				() -> beginWaterOpen(), () -> beginWaterClose(),
				ModSounds.WGH_BIG_STOP.get(), ModSounds.DOOR_LEVER.get());
	}

	private void tickVaultDoor(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.VT_BLAST_DOOR, VaultDoorAnim.TIME_TO_OPEN,
				() -> beginVaultOpen(), () -> beginVaultClose(),
				null, null);
		if (doorState == WIDE_OPENING || doorState == WIDE_CLOSING) {
			if (doorState == WIDE_CLOSING && openTicks == 30) {
				level.playSound(null, pos, ModSounds.VAULT_SCRAPE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			for (int i = 45; i <= 115; i += 10) {
				if (openTicks == i) {
					level.playSound(null, pos, ModSounds.VAULT_THUD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
		}
	}

	private void tickSiloHatch(Level level, BlockPos pos) {
		StructureType type = isSiloHatchLarge() ? StructureType.SILO_HATCH_LARGE : StructureType.SILO_HATCH;
		tickGenericDoor(level, pos, type, SiloHatchAnim.TIME_TO_OPEN,
				() -> beginSiloHatchOpen(), () -> beginSiloHatchClose(),
				ModSounds.WGH_BIG_STOP.get(), ModSounds.WGH_BIG_STOP.get());
	}

	private void tickTransitionSeal(Level level, BlockPos pos) {
		tickGenericDoor(level, pos, StructureType.TRANSITION_SEAL, TransitionSealAnim.TIME_TO_OPEN,
				() -> beginTransitionSealOpen(), () -> beginTransitionSealClose(),
				null, null);
	}

	private void tickGenericDoor(Level level, BlockPos pos, StructureType type, int timeToOpen,
			Runnable beginOpen, Runnable beginClose,
			net.minecraft.sounds.SoundEvent openEnd, net.minecraft.sounds.SoundEvent closeEnd) {
		boolean powered = isStructurePowered(level, pos, type);
		if (powered) {
			deferWideClose = false;
			if (doorState == WIDE_CLOSED) {
				beginOpen.run();
			}
			redstoned = true;
		} else {
			if (redstoned) {
				if (doorState == WIDE_OPEN) {
					beginClose.run();
				} else if (doorState == WIDE_OPENING) {
					deferWideClose = true;
				}
			}
			redstoned = false;
		}

		if (doorState == WIDE_OPENING) {
			openTicks++;
			if (openTicks >= timeToOpen) {
				openTicks = timeToOpen;
				doorState = WIDE_OPEN;
				if (openEnd != null) {
					level.playSound(null, pos, openEnd, SoundSource.BLOCKS, 2.0F, 1.0F);
				}
				if (deferWideClose) {
					deferWideClose = false;
					beginClose.run();
				}
			}
			setChanged();
			syncToClient();
		} else if (doorState == WIDE_CLOSING) {
			openTicks--;
			if (openTicks <= 0) {
				openTicks = 0;
				doorState = WIDE_CLOSED;
				if (closeEnd != null) {
					level.playSound(null, pos, closeEnd, SoundSource.BLOCKS, 2.0F, 1.0F);
				}
			}
			setChanged();
			syncToClient();
		}
	}

	private boolean isStructurePowered(Level level, BlockPos core, StructureType type) {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		boolean[] powered = {false};
		MultiblockHelper.forEachCellWithExtras(core, type, facing, cell -> {
			if (!powered[0] && level.hasNeighborSignal(cell)) {
				powered[0] = true;
			}
		});
		return powered[0];
	}

	public void tryToggle() {
		if (level == null || level.isClientSide) {
			return;
		}
		if (isShortBlastDoor()) {
			if (doorState == DOOR_CLOSED) {
				openShort();
				toggleShortNeighbours(true);
			} else if (doorState == DOOR_OPEN) {
				closeShort();
				toggleShortNeighbours(false);
			}
			return;
		}
		if (isWideSlidingDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.SLIDING_BLAST_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginWideOpen();
			} else if (doorState == WIDE_OPEN) {
				beginWideClose();
			}
			return;
		}
		if (isFireDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.FIRE_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginFireOpen();
			} else if (doorState == WIDE_OPEN) {
				beginFireClose();
			}
			return;
		}
		if (isSecureAccessDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.SECURE_ACCESS_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginSecureOpen();
			} else if (doorState == WIDE_OPEN) {
				beginSecureClose();
			}
			return;
		}
		if (isLargeVehicleDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.LARGE_VEHICLE_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginVehicleOpen();
			} else if (doorState == WIDE_OPEN) {
				beginVehicleClose();
			}
			return;
		}
		if (isQeContainmentDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.QE_CONTAINMENT_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginContainmentOpen();
			} else if (doorState == WIDE_OPEN) {
				beginContainmentClose();
			}
			return;
		}
		if (isQeSlidingDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.QE_SLIDING_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginQeSlidingOpen();
			} else if (doorState == WIDE_OPEN) {
				beginQeSlidingClose();
			}
			return;
		}
		if (isRoundAirlockDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.ROUND_AIRLOCK_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginAirlockOpen();
			} else if (doorState == WIDE_OPEN) {
				beginAirlockClose();
			}
			return;
		}
		if (isSlidingSealDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.SLIDING_SEAL_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginSlidingSealOpen();
			} else if (doorState == WIDE_OPEN) {
				beginSlidingSealClose();
			}
			return;
		}
		if (isWaterDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.WATER_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginWaterOpen();
			} else if (doorState == WIDE_OPEN) {
				beginWaterClose();
			}
			return;
		}
		if (isVaultDoor()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.VT_BLAST_DOOR)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginVaultOpen();
			} else if (doorState == WIDE_OPEN) {
				beginVaultClose();
			}
			return;
		}
		if (isSiloHatch()) {
			StructureType type = isSiloHatchLarge() ? StructureType.SILO_HATCH_LARGE : StructureType.SILO_HATCH;
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, type)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginSiloHatchOpen();
			} else if (doorState == WIDE_OPEN) {
				beginSiloHatchClose();
			}
			return;
		}
		if (isTransitionSeal()) {
			if (doorState == WIDE_CLOSED && isStructurePowered(level, worldPosition, StructureType.TRANSITION_SEAL)) {
				return;
			}
			if (doorState == WIDE_CLOSED) {
				beginTransitionSealOpen();
			} else if (doorState == WIDE_OPEN) {
				beginTransitionSealClose();
			}
		}
	}

	private void openShort() {
		if (doorState != DOOR_CLOSED || level == null) {
			return;
		}
		isOpening = true;
		doorState = DOOR_MOVING;
		animTick = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.REACTOR_START.get(), SoundSource.BLOCKS, 0.5F, 0.75F);
		setChanged();
		syncToClient();
	}

	private void closeShort() {
		if (doorState != DOOR_OPEN || level == null) {
			return;
		}
		isOpening = false;
		doorState = DOOR_MOVING;
		animTick = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.REACTOR_START.get(), SoundSource.BLOCKS, 0.5F, 0.75F);
		setChanged();
		syncToClient();
	}

	private void finishShortOpen(Level level, BlockPos pos) {
		doorState = DOOR_OPEN;
		animTick = 0;
		level.playSound(null, pos, ModSounds.REACTOR_STOP.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void finishShortClose(Level level, BlockPos pos) {
		doorState = DOOR_CLOSED;
		animTick = 0;
		level.playSound(null, pos, ModSounds.REACTOR_STOP.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginWideOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.SLIDING_DOOR_OPENING.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginWideClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = SlidingBlastDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.SLIDING_DOOR_OPENING.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginFireOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		// Move/alarm loops are started client-side and stopped when the anim ends
		// (wgh_start is longer than timeToOpen if played as a one-shot).
		setChanged();
		syncToClient();
	}

	private void beginFireClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = FireDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginSecureOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginSecureClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = SecureDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginVehicleOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginVehicleClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = VehicleDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginContainmentOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginContainmentClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = ContainmentDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginQeSlidingOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.QE_SLIDING_OPENING.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginQeSlidingClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = QeSlidingDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		// 1.7.10 close loop reuses the opening slide sound; shut plays at close end.
		level.playSound(null, worldPosition, ModSounds.QE_SLIDING_OPENING.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginAirlockOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginAirlockClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = AirlockDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginSlidingSealOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.SLIDING_SEAL_OPEN.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginSlidingSealClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = SlidingSealDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.SLIDING_SEAL_OPEN.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginWaterOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.DOOR_LEVER.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginWaterClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = WaterDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginVaultOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.VAULT_SCRAPE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginVaultClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = VaultDoorAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginSiloHatchOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginSiloHatchClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = SiloHatchAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		setChanged();
		syncToClient();
	}

	private void beginTransitionSealOpen() {
		if (doorState != WIDE_CLOSED || level == null) {
			return;
		}
		doorState = WIDE_OPENING;
		openTicks = 0;
		animStartGameTime = level.getGameTime();
		level.playSound(null, worldPosition, ModSounds.TRANSITION_SEAL_OPEN.get(), SoundSource.BLOCKS, 6.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	private void beginTransitionSealClose() {
		if (doorState != WIDE_OPEN || level == null) {
			return;
		}
		doorState = WIDE_CLOSING;
		openTicks = TransitionSealAnim.TIME_TO_OPEN;
		animStartGameTime = level.getGameTime();
		// Only one seal clip exists; reuse it for close (same as wide sliding door reuses opening).
		level.playSound(null, worldPosition, ModSounds.TRANSITION_SEAL_OPEN.get(), SoundSource.BLOCKS, 6.0F, 1.0F);
		setChanged();
		syncToClient();
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MultiblockControllerBlockEntity be) {
		if (be.isCargoElevator()) {
			be.tickCargoElevatorClient(level, pos);
		} else if (be.isFireDoor()) {
			com.hbmr.client.FireDoorClientSounds.tick(be);
		} else if (be.isSecureAccessDoor()) {
			com.hbmr.client.SecureDoorClientSounds.tick(be);
		} else if (be.isLargeVehicleDoor()) {
			com.hbmr.client.VehicleDoorClientSounds.tick(be);
		} else if (be.isQeContainmentDoor()) {
			com.hbmr.client.ContainmentDoorClientSounds.tick(be);
		} else if (be.isRoundAirlockDoor()) {
			com.hbmr.client.AirlockDoorClientSounds.tick(be);
		} else if (be.isWaterDoor()) {
			com.hbmr.client.WaterDoorClientSounds.tick(be);
		} else if (be.isSiloHatch()) {
			com.hbmr.client.SiloHatchClientSounds.tick(be);
		}
	}

	private void toggleShortNeighbours(boolean opening) {
		if (level == null) {
			return;
		}
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			BlockPos neigh = worldPosition.relative(dir);
			BlockEntity te = level.getBlockEntity(neigh);
			if (!(te instanceof MultiblockControllerBlockEntity other) || !other.isShortBlastDoor()) {
				continue;
			}
			if (opening && other.doorState == DOOR_CLOSED) {
				other.openShort();
				other.toggleShortNeighbours(true);
			} else if (!opening && other.doorState == DOOR_OPEN) {
				other.closeShort();
				other.toggleShortNeighbours(false);
			}
		}
	}

	private void placeDoorDummy(Level level, BlockPos pos) {
		BlockState present = level.getBlockState(pos);
		if (!present.canBeReplaced() && !present.is(ModBlocks.STRUCTURE_DUMMY.get())) {
			level.destroyBlock(pos, false);
		}
		StructureDummyBlock.safeRem = true;
		try {
			level.setBlock(pos, ModBlocks.STRUCTURE_DUMMY.get().defaultBlockState(), 3);
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof StructureDummyBlockEntity dummy) {
				dummy.setControllerPos(worldPosition);
			}
		} finally {
			StructureDummyBlock.safeRem = false;
		}
	}

	private void removeDoorDummy(Level level, BlockPos pos) {
		if (level.getBlockState(pos).is(ModBlocks.STRUCTURE_DUMMY.get())) {
			StructureDummyBlock.safeRem = true;
			try {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			} finally {
				StructureDummyBlock.safeRem = false;
			}
		}
	}

	private void syncToClient() {
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	/** Frequent motion sync without full block-update hitching. */
	private void syncCargoMotionToClient() {
		if (!(level instanceof ServerLevel server)) {
			return;
		}
		ClientboundBlockEntityDataPacket pkt = ClientboundBlockEntityDataPacket.create(this);
		for (ServerPlayer player : server.players()) {
			if (player.blockPosition().closerThan(worldPosition, 96.0D)) {
				player.connection.send(pkt);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt(TAG_HEIGHT, height);
		tag.putString(TAG_TYPE, structureType.name());
		tag.putInt(TAG_DOOR_STATE, doorState);
		tag.putBoolean(TAG_IS_OPENING, isOpening);
		tag.putInt(TAG_ANIM_TICK, animTick);
		tag.putLong(TAG_ANIM_START, animStartGameTime);
		tag.putBoolean(TAG_REDSTONED, redstoned);
		tag.putInt(TAG_OPEN_TICKS, openTicks);
		tag.putInt(TAG_SKIN, skinIndex);
		tag.putDouble(TAG_EXTENSION, extension);
		tag.putInt(TAG_TARGET_EXTENSION, targetExtension);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		height = tag.getInt(TAG_HEIGHT);
		if (tag.contains(TAG_TYPE)) {
			try {
				structureType = StructureType.valueOf(tag.getString(TAG_TYPE));
			} catch (IllegalArgumentException ignored) {
			}
		}
		doorState = tag.getInt(TAG_DOOR_STATE);
		isOpening = tag.getBoolean(TAG_IS_OPENING);
		animTick = tag.getInt(TAG_ANIM_TICK);
		animStartGameTime = tag.getLong(TAG_ANIM_START);
		redstoned = tag.getBoolean(TAG_REDSTONED);
		openTicks = tag.getInt(TAG_OPEN_TICKS);
		skinIndex = tag.getInt(TAG_SKIN);
		int maxSkin = DoorSkins.skinCount(structureType);
		if (maxSkin > 0) {
			skinIndex = Math.floorMod(skinIndex, maxSkin);
		} else {
			skinIndex = 0;
		}
		extension = tag.getDouble(TAG_EXTENSION);
		targetExtension = tag.getInt(TAG_TARGET_EXTENSION);
		prevExtension = extension;
		syncExtension = extension;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		if (tag != null && level != null && level.isClientSide && isCargoElevator()) {
			height = tag.getInt(TAG_HEIGHT);
			targetExtension = Math.min(tag.getInt(TAG_TARGET_EXTENSION), height);
			syncExtension = tag.getDouble(TAG_EXTENSION);
			if (tag.contains(TAG_TYPE)) {
				try {
					structureType = StructureType.valueOf(tag.getString(TAG_TYPE));
				} catch (IllegalArgumentException ignored) {
				}
			}
			// Hard snap only if we fell way behind (lag); otherwise client tick eases toward sync.
			if (Math.abs(extension - syncExtension) > 1.0D) {
				extension = syncExtension;
				prevExtension = extension;
			}
			return;
		}
		super.onDataPacket(net, pkt);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
				? getBlockState().getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;
		int[] rot = MultiblockHelper.rotateDims(structureType.getDims(), facing);
		int extra = structureType == StructureType.CARGO_ELEVATOR ? height : 0;
		double pad = isWideSlidingDoor() ? SlidingBlastDoorAnim.MAX_OPEN + 1.0D
				: (isFireDoor() ? FireDoorAnim.MAX_RAISE + 1.0D
				: (isVaultDoor() ? VaultDoorAnim.SLIDE_DISTANCE + 2.0D
				: (isLargeVehicleDoor() ? VehicleDoorAnim.MAX_OPEN + 2.0D
				: (isSecureAccessDoor() ? SecureDoorAnim.MAX_RAISE + 1.0D
				: (isQeContainmentDoor() ? ContainmentDoorAnim.MAX_RAISE + 1.0D
				: (isQeSlidingDoor() ? QeSlidingDoorAnim.MAX_OPEN + 1.0D
				: (isRoundAirlockDoor() ? AirlockDoorAnim.MAX_OPEN + 1.0D
				: (isSlidingSealDoor() ? SlidingSealDoorAnim.MAX_SLIDE + 1.0D
				: (isWaterDoor() ? 2.0D
				: (isTransitionSeal() ? 8.0D : 1.0D))))))))));
		double yPad = isTransitionSeal() ? 6.0D
				: (isSecureAccessDoor() ? SecureDoorAnim.MAX_RAISE + 2.0D
				: (isQeContainmentDoor() ? ContainmentDoorAnim.MAX_RAISE + 2.0D
				: (structureType == StructureType.RBMK_DEBRIS_FLAMING
						|| structureType == StructureType.RBMK_DEBRIS_SMOLDERING ? 4.5D : 2.0D)));
		double minX = worldPosition.getX() - rot[4] - pad;
		double minY = worldPosition.getY() - rot[1];
		double minZ = worldPosition.getZ() - rot[2] - pad;
		double maxX = worldPosition.getX() + rot[5] + 1.0D + pad;
		double maxY = worldPosition.getY() + rot[0] + extra + yPad;
		double maxZ = worldPosition.getZ() + rot[3] + 1.0D + pad;
		// Include extra footprints (e.g. exposure chamber coil arm) so frustum culling
		// does not drop the model when only the main tank AABB is off-screen.
		int[][] extras = structureType.getExtraDims();
		if (extras != null) {
			for (int[] extraDims : extras) {
				int[] er = MultiblockHelper.rotateDims(extraDims, facing);
				double eMinX = Math.min(worldPosition.getX() - er[4], worldPosition.getX() + er[5] + 1.0D);
				double eMaxX = Math.max(worldPosition.getX() - er[4], worldPosition.getX() + er[5] + 1.0D);
				double eMinY = Math.min(worldPosition.getY() - er[1], worldPosition.getY() + er[0] + 1.0D);
				double eMaxY = Math.max(worldPosition.getY() - er[1], worldPosition.getY() + er[0] + 1.0D);
				double eMinZ = Math.min(worldPosition.getZ() - er[2], worldPosition.getZ() + er[3] + 1.0D);
				double eMaxZ = Math.max(worldPosition.getZ() - er[2], worldPosition.getZ() + er[3] + 1.0D);
				minX = Math.min(minX, eMinX);
				minY = Math.min(minY, eMinY);
				minZ = Math.min(minZ, eMinZ);
				maxX = Math.max(maxX, eMaxX);
				maxY = Math.max(maxY, eMaxY);
				maxZ = Math.max(maxZ, eMaxZ);
			}
		}
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
