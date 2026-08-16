package com.hbmr.client;

import com.hbmr.HBMR;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Battery socket matching 1.7.10 {@code RenderBatterySocket}: direct texture bind
 * ({@code battery_socket.png} is 74×72 non-PoT). Idle/empty shows Socket (+ Supports
 * in-world); Battery/Capacitor packs are inventory contents, not the empty mesh.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BatterySocketRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/battery_socket.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/battery_socket.png");

	private static WavefrontObjModel model;

	private BatterySocketRenderer() {
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

	/** Inventory: Socket only (1.7.10 {@code renderCommon}). */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Socket", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}

	/** World idle: Socket + Supports (empty frame). */
	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		// 1.7.10 after facing yaw: translate(-0.5, 0, +0.5).
		poseStack.pushPose();
		poseStack.translate(-0.5D, 0.0D, 0.5D);
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Socket", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Supports", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
