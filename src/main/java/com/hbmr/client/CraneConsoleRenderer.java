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
 * Crane console matching 1.7.10 {@code RenderCraneConsole} / item render:
 * direct texture bind (non-PoT console atlas — avoid block-atlas UV crush).
 * Shotgun uses {@code ks23.png}; MiniNuke uses projectile {@code mini_nuke.png}
 * (those binds were commented out upstream after the KS-23 item was removed).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CraneConsoleRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/rbmk_crane_console.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/rbmk_crane_console.png");
	private static final ResourceLocation SHOTGUN_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/ks23.png");
	private static final ResourceLocation MINI_NUKE_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/mini_nuke.png");
	/** Near-white pixel for untextured lamp fill (same trick as radgen lights). */
	private static final ResourceLocation FILL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/radgen_glass_fill.png");

	private static WavefrontObjModel model;

	private CraneConsoleRenderer() {
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

	/** World idle: loading lamp off (dark green), target lamp red (no valid target). */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, false);
	}

	/** Inventory: bright green lamps like a powered console. */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, true);
	}

	private static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			boolean itemBright) {
		WavefrontObjModel m = model();
		VertexConsumer solid = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Console_Coonsole", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Joystick", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Meter1", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Meter2", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		// Desk props — separate weapon textures (1.7.10 RenderCraneConsole, re-enabled).
		VertexConsumer shotgun = buffer.getBuffer(RenderType.entityCutoutNoCull(SHOTGUN_TEX));
		m.renderPart("Shotgun", poseStack, shotgun, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Shotgun1", poseStack, shotgun, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		VertexConsumer nuke = buffer.getBuffer(RenderType.entityCutoutNoCull(MINI_NUKE_TEX));
		m.renderPart("MiniNuke", poseStack, nuke, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		// Lamps: no texture in 1.7.10 — flat emissive color.
		VertexConsumer lamp = buffer.getBuffer(RenderType.entityCutoutNoCull(FILL));
		int fullBright = 0xF000F0;
		if (itemBright) {
			m.renderPart("Lamp1", poseStack, lamp, 0f, 1f, 0f, 1f, fullBright, packedOverlay);
			m.renderPart("Lamp2", poseStack, lamp, 0f, 1f, 0f, 1f, fullBright, packedOverlay);
		} else {
			m.renderPart("Lamp1", poseStack, lamp, 0f, 0.1f, 0f, 1f, fullBright, packedOverlay);
			m.renderPart("Lamp2", poseStack, lamp, 1f, 0f, 0f, 1f, fullBright, packedOverlay);
		}
	}
}
