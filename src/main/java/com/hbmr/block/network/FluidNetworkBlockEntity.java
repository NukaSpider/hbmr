package com.hbmr.block.network;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** BER host for thin universal fluid ducts ({@code pipe_neo.obj}). */
public class FluidNetworkBlockEntity extends BlockEntity {
	public FluidNetworkBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FLUID_NETWORK.get(), pos, state);
	}
}
