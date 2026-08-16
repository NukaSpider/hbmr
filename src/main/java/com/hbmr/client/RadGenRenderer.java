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
 * Renders the radiation-powered engine matching 1.7.10 {@code RenderRadGen}:
 * direct texture bind (no block-atlas remapping), smooth vertex normals,
 * untextured emissive Light, translucent blue Glass fill, then textured lattice.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RadGenRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/radiation_powered_engine.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/radiation_powered_engine.png");
	/** Near-white translucent pixel — substitutes for GL textures-disabled fill. */
	private static final ResourceLocation FILL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/radgen_glass_fill.png");

	private static WavefrontObjModel model;

	private RadGenRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> model = null);
	}

	private static WavefrontObjModel model() {
		if (model == null) {
			// flip_v true matches Forge OBJ / Blender → Minecraft UV convention.
			model = WavefrontObjModel.load(MODEL, true);
		}
		return model;
	}

	/** World / BER path — idle dark-green lights (animation later). */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, false);
	}

	/** Inventory path — bright green lights like ItemRenderLibrary. */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		render(poseStack, buffer, packedLight, packedOverlay, true);
	}

	private static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			boolean itemBrightLight) {
		WavefrontObjModel m = model();

		VertexConsumer solid = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Base", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Rotor", poseStack, solid, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		float lg = itemBrightLight ? 1f : 0.1f;
		VertexConsumer light = buffer.getBuffer(RenderType.entityCutoutNoCull(FILL));
		m.renderPart("Light", poseStack, light, 0f, lg, 0f, 1f, 0xF000F0, packedOverlay);

		// Untextured blue glass fill (alpha ~0.3), then textured lattice with alpha test.
		VertexConsumer fill = buffer.getBuffer(RenderType.entityTranslucent(FILL));
		m.renderPart("Glass", poseStack, fill, 0.5f, 0.75f, 1f, 0.3f, packedLight, packedOverlay);

		VertexConsumer glass = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Glass", poseStack, glass, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}
}
