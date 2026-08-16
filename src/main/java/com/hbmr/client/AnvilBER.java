package com.hbmr.client;

import com.hbmr.block.AnvilBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Renders placed anvils with entity lighting (same idea as falling-block render)
 * so Forge OBJ faces are not crushed black by chunk AO / shade.
 */
public class AnvilBER implements BlockEntityRenderer<AnvilBlockEntity> {
	public AnvilBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(AnvilBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState state = be.getBlockState();
		BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		renderer.renderModel(poseStack.last(), consumer, state, model, 1f, 1f, 1f, packedLight, packedOverlay,
				ModelData.EMPTY, null);
	}
}
