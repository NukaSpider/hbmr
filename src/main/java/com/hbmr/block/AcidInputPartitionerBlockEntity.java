package com.hbmr.block;

import com.hbmr.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** BER host for {@link AcidInputPartitionerBlock}. */
public class AcidInputPartitionerBlockEntity extends BlockEntity {
	public AcidInputPartitionerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ACID_INPUT_PARTITIONER.get(), pos, state);
	}
}
