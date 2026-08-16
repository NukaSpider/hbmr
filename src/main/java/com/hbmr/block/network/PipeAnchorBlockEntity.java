package com.hbmr.block.network;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** BER host for {@link PipeAnchorBlock}. */
public class PipeAnchorBlockEntity extends BlockEntity {
	public PipeAnchorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PIPE_ANCHOR.get(), pos, state);
	}
}
