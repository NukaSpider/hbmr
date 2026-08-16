package com.hbmr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Inventory / hand renderer matching 1.7.10 {@code RenderCable#renderInventoryBlock}.
 */
public final class CableNeoItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static CableNeoItemRenderer instance;

	public static CableNeoItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new CableNeoItemRenderer(mc);
		}
		return instance;
	}

	private CableNeoItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// ItemRenderer expects 0…1 models; cable_neo.obj is origin-centered (−0.5…0.5).
		poseStack.translate(0.5D, 0.5D, 0.5D);
		CableNeoRenderer.renderInventory(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
