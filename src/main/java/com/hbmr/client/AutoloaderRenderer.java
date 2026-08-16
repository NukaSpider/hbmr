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
 * RBMK autoloader matching 1.7.10 {@code RenderRBMKAutoloader}: direct texture bind.
 * Atlas is {@code 152×106} (non-PoT) — baking into the block atlas crushes UVs so the
 * shaft vents read as flat grey panels.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AutoloaderRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/rbmk_autoloader.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/rbmk_autoloader.png");

	/** Idle piston height (1.7.10: {@code translate(0, 4, 0)} when progress is 0). */
	private static final double PISTON_IDLE_Y = 4.0D;

	private static WavefrontObjModel model;

	private AutoloaderRenderer() {
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

	/** World idle: piston raised. */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, PISTON_IDLE_Y);
	}

	/** Inventory: both parts at rest like 1.7.10 item common render. */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, 0.0D);
	}

	private static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			double pistonY) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Base", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		poseStack.pushPose();
		poseStack.translate(0.0D, pistonY, 0.0D);
		m.renderPart("Piston", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
