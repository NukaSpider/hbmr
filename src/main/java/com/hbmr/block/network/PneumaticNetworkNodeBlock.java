package com.hbmr.block.network;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Full-cube pneumatic endpoint (1.7.10 {@code PneumoTubePaintableBlock}).
 * Thin {@link BoxDuctBlock} pneumatic tubes connect into this block.
 */
public class PneumaticNetworkNodeBlock extends Block implements PneumaticNetworkConnectable {
	public PneumaticNetworkNodeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
