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
 * FEnSU visuals use the modern 1.7.10 {@code fensu2.obj} / Battery REDD mesh
 * (old {@code fensu.obj} is the deprecated yellow-spoke model). Texture is
 * 448×268 non-PoT — direct Wavefront bind. Idle: Base + Wheel + Lights
 * ({@code RenderBatteryREDD} / item path; Plasma is the charged effect).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FensuRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/fensu.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/fensu.png");

	private static WavefrontObjModel model;

	private FensuRenderer() {
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

	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Base", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Wheel", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		// Lights use fullbright in 1.7; keep them bright via max light.
		m.renderPart("Lights", poseStack, vc, 1f, 1f, 1f, 1f, 0x00F000F0, packedOverlay);
	}
}
