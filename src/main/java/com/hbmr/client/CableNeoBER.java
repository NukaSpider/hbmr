package com.hbmr.client;

import com.hbmr.block.network.CableNetworkBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** World renderer for normal red copper cable ({@code cable_neo.obj}). */
public class CableNeoBER implements BlockEntityRenderer<CableNetworkBlockEntity> {
	public CableNeoBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(CableNetworkBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		CableNeoRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, be.getBlockState());
	}
}
