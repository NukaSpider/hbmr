package com.hbmr.item;

import com.hbmr.block.multiblock.MultiblockControllerBlock;
import com.hbmr.block.multiblock.MultiblockControllerBlockEntity;
import com.hbmr.block.multiblock.MultiblockHelper;
import com.hbmr.block.multiblock.StructureDummyBlockEntity;
import com.hbmr.block.multiblock.StructureType;
import com.hbmr.client.MultiblockItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * BlockItem that renders multiblock controllers via a shared BEWLR (3D OBJ in inventory).
 * Places offset structures at the final core position (edge click, no teleport jump).
 */
public class MultiblockBlockItem extends BlockItem {
	public MultiblockBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		if (!(getBlock() instanceof MultiblockControllerBlock controller)) {
			return super.place(context);
		}
		StructureType type = controller.getStructureType();

		// Stacking a cargo elevator on top of itself: place at the clicked cell, then extend.
		if (type == StructureType.CARGO_ELEVATOR && wouldExtendCargoElevator(context)) {
			return super.place(context);
		}

		if (type.getOffset() == 0 && type.getHeightOffset() <= 0) {
			return super.place(context);
		}

		BlockState state = getPlacementState(context);
		if (state == null || !state.hasProperty(MultiblockControllerBlock.FACING)) {
			return InteractionResult.FAIL;
		}
		Direction facing = state.getValue(MultiblockControllerBlock.FACING);
		BlockPos click = context.getClickedPos();
		BlockPos core = type.getOffset() != 0
				? MultiblockHelper.coreFromClick(click, facing, type.getOffset())
				: click.above(type.getHeightOffset());
		Level level = context.getLevel();

		if (!MultiblockHelper.checkSpace(level, core, type, facing, click)) {
			return InteractionResult.FAIL;
		}

		BlockPlaceContext atCore = BlockPlaceContext.at(context, core, context.getClickedFace());
		if (atCore == null) {
			atCore = new BlockPlaceContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(),
					new BlockHitResult(context.getClickLocation(), context.getClickedFace(), core, context.isInside()));
		}

		if (!placeBlock(atCore, state)) {
			return InteractionResult.FAIL;
		}

		Player player = context.getPlayer();
		BlockState placed = level.getBlockState(core);
		placed.getBlock().setPlacedBy(level, core, placed, player, context.getItemInHand());

		// Custom offset path skips BlockItem.place() — restore vanilla place sound / game event.
		SoundType sound = placed.getSoundType(level, core, player);
		level.playSound(player, core, getPlaceSound(placed, level, core, player), SoundSource.BLOCKS,
				(sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
		level.gameEvent(GameEvent.BLOCK_PLACE, core, GameEvent.Context.of(player, placed));

		if (player != null && !player.getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	/** True when placing this elevator would stack onto an existing shaft under the click. */
	private static boolean wouldExtendCargoElevator(BlockPlaceContext context) {
		BlockPos placedPos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(placedPos);
		// Must be replacing air / replaceable at the place cell
		if (!state.canBeReplaced(context)) {
			return false;
		}
		Direction facing = context.getHorizontalDirection().getOpposite();
		Level level = context.getLevel();
		for (int dy = 1; dy <= 64; dy++) {
			BlockPos below = placedPos.below(dy);
			BlockEntity be = level.getBlockEntity(below);
			BlockPos core = null;
			if (be instanceof MultiblockControllerBlockEntity ctrl && ctrl.isCargoElevator()) {
				core = below;
			} else if (be instanceof StructureDummyBlockEntity dummy && dummy.getControllerPos() != null) {
				BlockEntity coreBe = level.getBlockEntity(dummy.getControllerPos());
				if (coreBe instanceof MultiblockControllerBlockEntity ctrl && ctrl.isCargoElevator()) {
					core = dummy.getControllerPos();
				}
			}
			if (core == null) {
				continue;
			}
			BlockState coreState = level.getBlockState(core);
			if (!coreState.hasProperty(MultiblockControllerBlock.FACING)
					|| coreState.getValue(MultiblockControllerBlock.FACING) != facing) {
				continue;
			}
			BlockEntity coreBe = level.getBlockEntity(core);
			if (!(coreBe instanceof MultiblockControllerBlockEntity ctrl)) {
				continue;
			}
			int nextY = core.getY() + ctrl.getLayerCount();
			if (nextY != placedPos.getY()) {
				continue;
			}
			int[] rot = MultiblockHelper.rotateDims(StructureType.CARGO_ELEVATOR.getDims(), facing);
			int dx = placedPos.getX() - core.getX();
			int dz = placedPos.getZ() - core.getZ();
			if (dx < -rot[4] || dx > rot[5] || dz < -rot[2] || dz > rot[3]) {
				continue;
			}
			return MultiblockHelper.checkExtraLayer(level, core, nextY, StructureType.CARGO_ELEVATOR.getDims(), facing);
		}
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return MultiblockItemRenderer.getInstance();
			}
		});
	}
}
