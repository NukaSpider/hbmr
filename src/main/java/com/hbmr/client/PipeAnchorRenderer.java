package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.network.PipeAnchorBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Matches 1.7.10 {@code RenderPipeAnchor}: center + facing rotation + {@code y-0.5},
 * then only the {@code Anchor} part (Pipe/Ring are inter-anchor links).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PipeAnchorRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/network/pipe_anchor.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/network/pipe_anchor.png");

	private static WavefrontObjModel model;

	private PipeAnchorRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> model = null);
	}

	private static WavefrontObjModel model() {
		if (model == null) {
			model = WavefrontObjModel.load(MODEL, true);
		}
		return model;
	}

	public static void renderInventory(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		poseStack.pushPose();
		// Item cube is 0…1; Anchor is center-XZ, y 0…0.75 (same as 1.7.10 TESR item).
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.translate(0.0D, -0.375D, 0.0D);
		poseStack.scale(1.1F, 1.1F, 1.1F);
		drawAnchor(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}

	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state) {
		Direction facing = state.hasProperty(PipeAnchorBlock.FACING)
				? state.getValue(PipeAnchorBlock.FACING)
				: Direction.UP;

		poseStack.pushPose();
		// 1.7.10: translate to block center, face rotate, then y-0.5 so Anchor feet sit on the face.
		poseStack.translate(0.5D, 0.5D, 0.5D);
		applyFacing(poseStack, facing);
		poseStack.translate(0.0D, -0.5D, 0.0D);
		drawAnchor(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}

	/** Same rotations as 1.7.10 {@code RenderPipeAnchor} meta 0–5. */
	private static void applyFacing(PoseStack poseStack, Direction facing) {
		switch (facing) {
			case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
			case UP -> {
			}
			case NORTH -> {
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.mulPose(Axis.ZP.rotationDegrees(180));
			}
			case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
			case WEST -> {
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.mulPose(Axis.ZP.rotationDegrees(90));
			}
			case EAST -> {
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.mulPose(Axis.ZP.rotationDegrees(270));
			}
		}
	}

	private static void drawAnchor(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		model().renderPart("Anchor", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}
}
