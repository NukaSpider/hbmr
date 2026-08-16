package com.hbmr.block.network;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Marker for blocks that visually connect on the fluid duct network.
 * Exhaust (smoke) pipes are a separate channel and must not link to normal ducts.
 */
public interface FluidNetworkConnectable {
	/**
	 * @param face face of this block being approached by a neighbor duct
	 */
	default boolean canFluidConnect(BlockState state, Direction face) {
		return true;
	}

	/**
	 * Exhaust / smoke network (1.7.10 {@code FluidDuctBoxExhaust}), vs universal fluid ducts.
	 */
	default boolean isExhaustNetwork() {
		return false;
	}
}
