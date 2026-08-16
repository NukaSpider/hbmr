package com.hbmr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class PipeAnchorItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static PipeAnchorItemRenderer instance;

	public static PipeAnchorItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new PipeAnchorItemRenderer(mc);
		}
		return instance;
	}

	private PipeAnchorItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		PipeAnchorRenderer.renderInventory(poseStack, buffer, packedLight, packedOverlay);
	}
}
