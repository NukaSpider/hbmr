package com.hbmr.client;

import com.hbmr.block.AnvilBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Vanilla falling-block renderer only draws {@link RenderShape#MODEL}. Our anvils use
 * {@link RenderShape#ENTITYBLOCK_ANIMATED} (placed BER), so they need this path while airborne.
 */
public class HbmrFallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
	private final BlockRenderDispatcher dispatcher;

	public HbmrFallingBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.dispatcher = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(FallingBlockEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		BlockState state = entity.getBlockState();
		if (state.getRenderShape() == RenderShape.INVISIBLE) {
			return;
		}

		Level level = entity.level();
		if (state == level.getBlockState(entity.blockPosition())) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(-0.5D, 0.0D, -0.5D);

		if (state.getBlock() instanceof AnvilBlock) {
			BakedModel model = this.dispatcher.getBlockModel(state);
			VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
			this.dispatcher.getModelRenderer().renderModel(poseStack.last(), consumer, state, model, 1f, 1f, 1f,
					packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
		} else if (state.getRenderShape() == RenderShape.MODEL) {
			BlockPos lightPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
			this.dispatcher.getModelRenderer().tesselateBlock(level, this.dispatcher.getBlockModel(state), state,
					lightPos, poseStack, buffer.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(state)),
					false, RandomSource.create(), state.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY,
					ModelData.EMPTY, null);
		}

		poseStack.popPose();
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(FallingBlockEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
