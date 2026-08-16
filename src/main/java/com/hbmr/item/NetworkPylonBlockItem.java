package com.hbmr.item;

import com.hbmr.block.network.NetworkPylonBlock;
import com.hbmr.block.network.NetworkPylonKind;
import com.hbmr.block.multiblock.MultiblockHelper;
import com.hbmr.client.NetworkPylonItemRenderer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/** Block item that draws pylon/connector OBJs in inventory like 1.7.10 TESR items. */
public class NetworkPylonBlockItem extends BlockItem {
	public NetworkPylonBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		if (!(getBlock() instanceof NetworkPylonBlock pylon)) {
			return super.place(context);
		}
		NetworkPylonKind kind = pylon.kind();
		if (kind.placementOffset() == 0) {
			return super.place(context);
		}

		BlockState state = getPlacementState(context);
		if (state == null || !state.hasProperty(NetworkPylonBlock.FACING)) {
			return InteractionResult.FAIL;
		}
		Direction facing = state.getValue(NetworkPylonBlock.FACING);
		BlockPos click = context.getClickedPos();
		BlockPos core = MultiblockHelper.coreFromClick(click, facing, kind.placementOffset());
		Level level = context.getLevel();

		if (!MultiblockHelper.checkSpace(level, core, kind.footprintDims(), facing, click)) {
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

		SoundType sound = placed.getSoundType(level, core, player);
		level.playSound(player, core, getPlaceSound(placed), SoundSource.BLOCKS,
				(sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
		level.gameEvent(GameEvent.BLOCK_PLACE, core, GameEvent.Context.of(player, placed));

		if (player != null && !player.getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return NetworkPylonItemRenderer.getInstance();
			}
		});
	}
}
