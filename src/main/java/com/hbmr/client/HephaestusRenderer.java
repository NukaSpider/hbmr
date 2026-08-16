package com.hbmr.client;

import com.hbmr.HBMR;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Renders the geothermal heat exchanger matching 1.7.10 {@code RenderHephaestus}:
 * direct texture bind (no block-atlas remapping — hephaestus.png is 320×196 non-PoT),
 * three rotors at 120°, idle Core with cobble at half brightness.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HephaestusRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/geothermal_heat_exchanger.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/geothermal_heat_exchanger.png");
	private static final ResourceLocation CORE_IDLE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/hephaestus_core_idle.png");

	private static WavefrontObjModel model;

	private HephaestusRenderer() {
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

	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		WavefrontObjModel m = model();

		VertexConsumer solid = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Main", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		poseStack.pushPose();
		for (int i = 0; i < 3; i++) {
			m.renderPart("Rotor", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
			poseStack.mulPose(Axis.YP.rotationDegrees(120f));
		}
		poseStack.popPose();

		// Idle core: cobble ×0.5 gray, UV scaled like 1.7.10 texture matrix.
		VertexConsumer core = buffer.getBuffer(RenderType.entityCutoutNoCull(CORE_IDLE));
		m.renderPart("Core", poseStack, core, 0.5f, 0.5f, 0.5f, 1f, packedLight, packedOverlay,
				0.5f, 0.5f, 0f, 0f);
	}
}
