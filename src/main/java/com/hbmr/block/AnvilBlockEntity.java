package com.hbmr.block;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Marker BE so anvils can use entity-path lighting via {@link com.hbmr.client.AnvilBER}. */
public class AnvilBlockEntity extends BlockEntity {
	public AnvilBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ANVIL.get(), pos, state);
	}
}
