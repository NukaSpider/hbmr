package com.hbmr.block.network;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Full-cube fluid endpoint (coated ducts, valves as cubes).
 */
public class FluidNetworkNodeBlock extends Block implements FluidNetworkConnectable {
	private final boolean exhaustNetwork;

	public FluidNetworkNodeBlock(Properties properties) {
		this(properties, false);
	}

	public FluidNetworkNodeBlock(Properties properties, boolean exhaustNetwork) {
		super(properties);
		this.exhaustNetwork = exhaustNetwork;
	}

	@Override
	public boolean isExhaustNetwork() {
		return exhaustNetwork;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
