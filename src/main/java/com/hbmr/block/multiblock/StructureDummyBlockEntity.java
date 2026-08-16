package com.hbmr.block.multiblock;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class StructureDummyBlockEntity extends BlockEntity {
	public static final String TAG_CONTROLLER = "Controller";

	@Nullable
	private BlockPos controllerPos;

	public StructureDummyBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STRUCTURE_DUMMY.get(), pos, state);
	}

	public void setControllerPos(BlockPos controllerPos) {
		this.controllerPos = controllerPos.immutable();
		setChanged();
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	@Nullable
	public BlockPos getControllerPos() {
		return controllerPos;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (controllerPos != null) {
			tag.putLong(TAG_CONTROLLER, controllerPos.asLong());
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(TAG_CONTROLLER)) {
			controllerPos = BlockPos.of(tag.getLong(TAG_CONTROLLER));
		}
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
}
