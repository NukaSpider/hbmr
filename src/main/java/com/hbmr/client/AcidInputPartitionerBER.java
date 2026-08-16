package com.hbmr.client;

import com.hbmr.block.AcidInputPartitionerBlock;
import com.hbmr.block.AcidInputPartitionerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AcidInputPartitionerBER implements BlockEntityRenderer<AcidInputPartitionerBlockEntity> {
	public AcidInputPartitionerBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(AcidInputPartitionerBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.hasProperty(AcidInputPartitionerBlock.FACING)
				? state.getValue(AcidInputPartitionerBlock.FACING)
				: Direction.NORTH;
		AcidInputPartitionerRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, facing);
	}
}
