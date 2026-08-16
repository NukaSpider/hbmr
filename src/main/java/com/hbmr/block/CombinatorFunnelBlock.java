package com.hbmr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Visuals-first combinator funnel ({@code MachineFunnel} in 1.7.10). */
public class CombinatorFunnelBlock extends Block {
	private static final VoxelShape SHAPE = Shapes.or(
			Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
			Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	);

	public CombinatorFunnelBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}
}
