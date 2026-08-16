package com.hbmr.client;

import com.hbmr.block.network.FluidNetworkBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PipeNeoBER implements BlockEntityRenderer<FluidNetworkBlockEntity> {
	public PipeNeoBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(FluidNetworkBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		PipeNeoRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, be.getBlockState());
	}
}
