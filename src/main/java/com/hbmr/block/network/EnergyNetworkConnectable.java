package com.hbmr.block.network;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Marker for blocks that visually connect to the red-copper energy network.
 */
public interface EnergyNetworkConnectable {
	/**
	 * @param face face of this block being approached by a neighbor cable
	 */
	default boolean canEnergyConnect(BlockState state, Direction face) {
		return true;
	}
}
