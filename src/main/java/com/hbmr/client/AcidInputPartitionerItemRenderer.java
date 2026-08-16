package com.hbmr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class AcidInputPartitionerItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static AcidInputPartitionerItemRenderer instance;

	public static AcidInputPartitionerItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new AcidInputPartitionerItemRenderer(mc);
		}
		return instance;
	}

	private AcidInputPartitionerItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// ItemRenderer expects 0…1 models; crane_buffer.obj is origin-centered (−0.5…0.5).
		poseStack.translate(0.5D, 0.5D, 0.5D);
		AcidInputPartitionerRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
