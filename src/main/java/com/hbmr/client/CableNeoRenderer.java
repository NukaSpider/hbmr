package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.network.CableNetworkBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Matches 1.7.10 {@code RenderCable}: {@code cable_neo.obj} parts with {@code cable_neo.png}
 * bound directly (same as ObjUtil.renderPartWithIcon on the block icon).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CableNeoRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/network/cable_neo.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/cable_neo.png");

	private static WavefrontObjModel model;

	private CableNeoRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> model = null);
	}

	private static WavefrontObjModel model() {
		if (model == null) {
			// HFRWavefrontObject uses (u, 1-v) — same as flipV=true.
			model = WavefrontObjModel.load(MODEL, true);
		}
		return model;
	}

	/** Inventory: Core + four horizontal arms (1.7.10 renderInventoryBlock). */
	public static void renderInventory(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(180));
		poseStack.scale(1.05F, 1.05F, 1.05F);
		m.renderPart("Core", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("posX", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("negX", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("posZ", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("negZ", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		poseStack.popPose();
	}

	/**
	 * World: translate to block center then draw parts per connections
	 * (same part rules as 1.7.10 RenderCable#renderWorldBlock, including Z swap).
	 */
	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			BlockState state) {
		boolean pX = state.getValue(CableNetworkBlock.EAST);
		boolean nX = state.getValue(CableNetworkBlock.WEST);
		boolean pY = state.getValue(CableNetworkBlock.UP);
		boolean nY = state.getValue(CableNetworkBlock.DOWN);
		boolean pZ = state.getValue(CableNetworkBlock.SOUTH);
		boolean nZ = state.getValue(CableNetworkBlock.NORTH);

		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);

		if (pX && nX && !pY && !nY && !pZ && !nZ) {
			m.renderPart("CX", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		} else if (!pX && !nX && pY && nY && !pZ && !nZ) {
			m.renderPart("CY", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		} else if (!pX && !nX && !pY && !nY && pZ && nZ) {
			m.renderPart("CZ", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		} else {
			m.renderPart("Core", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			if (pX) {
				m.renderPart("posX", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
			if (nX) {
				m.renderPart("negX", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
			if (pY) {
				m.renderPart("posY", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
			if (nY) {
				m.renderPart("negY", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
			// 1.7.10 swaps Z part names relative to world +Z/−Z.
			if (nZ) {
				m.renderPart("posZ", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
			if (pZ) {
				m.renderPart("negZ", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			}
		}
		poseStack.popPose();
	}
}
