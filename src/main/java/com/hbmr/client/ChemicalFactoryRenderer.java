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
 * Chemical factory visuals. Forge's {@code forge:obj} baker fails on this mesh
 * ({@code f v//n} faces → IndexOutOfBounds), so we draw via {@link WavefrontObjModel}
 * like barrels / FEnSU. Parts match 1.7.10 {@code RenderChemicalFactory} idle pose.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ChemicalFactoryRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/chemical_factory.obj");
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/chemical_factory.png");

	private static WavefrontObjModel model;

	private ChemicalFactoryRenderer() {
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

	/** World + inventory: Base + Frame + both fans (static). */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		m.renderPart("Base", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Frame", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Fan1", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
		m.renderPart("Fan2", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}
}
