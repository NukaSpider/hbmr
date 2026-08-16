package com.hbmr.block.network;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Full-cube / OBJ energy endpoint with no facing (pylons, coated cable, switches as cubes).
 */
public class EnergyNetworkNodeBlock extends Block implements EnergyNetworkConnectable {
	public EnergyNetworkNodeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
