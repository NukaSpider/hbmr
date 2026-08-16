package com.hbmr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * NTM anvil ({@code NTMAnvil} in 1.7.10): gravity + fall damage like vanilla anvil.
 * Tier is stored for later GUI recipes.
 * <p>
 * Placed mesh is drawn by {@link com.hbmr.client.AnvilBER} (chunk AO blacks Forge OBJ faces).
 */
public class AnvilBlock extends FallingBlock implements EntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	/** Long axis along X — half-block thick like the anvil body. */
	private static final VoxelShape SHAPE_LONG_X = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 11.0D, 12.0D);
	/** Long axis along Z. */
	private static final VoxelShape SHAPE_LONG_Z = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 11.0D, 16.0D);

	private final int tier;

	public AnvilBlock(Properties properties, int tier) {
		super(properties.sound(SoundType.ANVIL));
		this.tier = tier;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public int getTier() {
		return tier;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		return facing.getAxis() == Direction.Axis.Z ? SHAPE_LONG_X : SHAPE_LONG_Z;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		// Full cube so stacked FallingBlockEntities rest on each other instead of clipping through.
		return Shapes.block();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return false;
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		// Not MODEL (chunk AO blacks Forge OBJ faces). ENTITYBLOCK_ANIMATED so AnvilBER's
		// renderSingleBlock actually draws (INVISIBLE is a no-op in that path).
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnvilBlockEntity(pos, state);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	/** Same hurt values as vanilla {@link net.minecraft.world.level.block.AnvilBlock}. */
	@Override
	public void falling(FallingBlockEntity entity) {
		entity.setHurtsEntities(2.0F, 40);
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replacedState, FallingBlockEntity entity) {
		if (!entity.isSilent()) {
			level.levelEvent(1031, pos, 0);
		}
	}

	@Override
	public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
		if (!entity.isSilent()) {
			level.levelEvent(1029, pos, 0);
		}
	}

	@Override
	public DamageSource getFallDamageSource(Entity entity) {
		return entity.damageSources().anvil(entity);
	}
}
