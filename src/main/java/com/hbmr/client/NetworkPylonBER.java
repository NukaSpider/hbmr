package com.hbmr.client;

import com.hbmr.block.network.NetworkPylonBlock;
import com.hbmr.block.network.NetworkPylonBlockEntity;
import com.hbmr.block.network.NetworkPylonKind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** World renderer for electricity pylons and connectors. */
public class NetworkPylonBER implements BlockEntityRenderer<NetworkPylonBlockEntity> {
	public NetworkPylonBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(NetworkPylonBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState state = be.getBlockState();
		NetworkPylonKind kind = be.kind();
		Direction facing = Direction.NORTH;
		if (state.getBlock() instanceof NetworkPylonBlock) {
			facing = state.getValue(NetworkPylonBlock.FACING);
		}
		NetworkPylonRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, kind, facing);
	}

	@Override
	public boolean shouldRenderOffScreen(NetworkPylonBlockEntity be) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
