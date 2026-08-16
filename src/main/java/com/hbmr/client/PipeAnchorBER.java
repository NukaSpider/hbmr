package com.hbmr.client;

import com.hbmr.block.network.PipeAnchorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PipeAnchorBER implements BlockEntityRenderer<PipeAnchorBlockEntity> {
	public PipeAnchorBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(PipeAnchorBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		PipeAnchorRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, be.getBlockState());
	}
}
