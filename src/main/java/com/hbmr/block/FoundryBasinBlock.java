package com.hbmr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Visuals-first foundry basin / shallow mold ({@code FoundryBasin}/{@code FoundryMold} in 1.7.10).
 */
public class FoundryBasinBlock extends Block {
	/** Half-high hollow basin (shallow mold). */
	public static final VoxelShape SHAPE_SHALLOW = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 2.0D),
			Block.box(0.0D, 0.0D, 0.0D, 2.0D, 8.0D, 16.0D),
			Block.box(14.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
			Block.box(0.0D, 0.0D, 14.0D, 16.0D, 8.0D, 16.0D)
	);

	/** Nearly full-height hollow basin. */
	public static final VoxelShape SHAPE_FULL = Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
			Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D),
			Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D)
	);

	private final VoxelShape shape;

	public FoundryBasinBlock(Properties properties, VoxelShape shape) {
		super(properties);
		this.shape = shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shape;
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
