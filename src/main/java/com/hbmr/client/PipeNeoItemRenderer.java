package com.hbmr.client;

import com.hbmr.block.network.FluidDuctMaterial;
import com.hbmr.block.network.FluidNetworkBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class PipeNeoItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static PipeNeoItemRenderer instance;

	public static PipeNeoItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new PipeNeoItemRenderer(mc);
		}
		return instance;
	}

	private PipeNeoItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		FluidDuctMaterial material = FluidDuctMaterial.NEO;
		if (stack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			if (block instanceof FluidNetworkBlock duct) {
				material = duct.getMaterial();
			}
		}
		poseStack.pushPose();
		// ItemRenderer expects 0…1 models; pipe_neo.obj is origin-centered (−0.5…0.5).
		poseStack.translate(0.5D, 0.5D, 0.5D);
		PipeNeoRenderer.renderInventory(poseStack, buffer, packedLight, packedOverlay, material);
		poseStack.popPose();
	}
}
