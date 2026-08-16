package com.hbmr.block.network;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Blocks that thin pneumatic tubes visually connect to (1.7.10 {@code TileEntityPneumoTube} neighbors). */
public interface PneumaticNetworkConnectable {
	/**
	 * @param face face of this block being approached by a neighbor tube
	 */
	default boolean canPneumaticConnect(BlockState state, Direction face) {
		return true;
	}
}
