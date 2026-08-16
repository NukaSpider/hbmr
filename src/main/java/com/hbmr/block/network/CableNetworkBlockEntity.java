package com.hbmr.block.network;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Marker BE so thin copper cable uses entity-path OBJ rendering like 1.7.10 {@code RenderCable}. */
public class CableNetworkBlockEntity extends BlockEntity {
	public CableNetworkBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CABLE_NETWORK.get(), pos, state);
	}
}
