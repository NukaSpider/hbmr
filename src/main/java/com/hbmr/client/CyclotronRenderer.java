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
 * Cyclotron matching 1.7.10 {@code RenderCyclotron}: direct texture binds per part.
 * Body atlas is {@code 224×224} and plugs are {@code 28×32} (non-PoT) — baking into the
 * block atlas remaps UVs and makes the machine look like a different model.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CyclotronRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/cyclotron.obj");
	private static final ResourceLocation BODY =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/cyclotron.png");
	private static final ResourceLocation ASHES =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/cyclotron_ashes.png");
	private static final ResourceLocation BOOK =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/cyclotron_book.png");
	private static final ResourceLocation GAVEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/cyclotron_gavel.png");
	private static final ResourceLocation COIN =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/cyclotron_coin.png");

	private static WavefrontObjModel model;

	private CyclotronRenderer() {
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

	/** World + inventory idle (empty plugs). */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		WavefrontObjModel m = model();

		VertexConsumer body = buffer.getBuffer(RenderType.entityCutoutNoCull(BODY));
		m.renderPart("Body", poseStack, body, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		VertexConsumer ashes = buffer.getBuffer(RenderType.entityCutoutNoCull(ASHES));
		m.renderPart("B1", poseStack, ashes, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		VertexConsumer book = buffer.getBuffer(RenderType.entityCutoutNoCull(BOOK));
		m.renderPart("B2", poseStack, book, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		VertexConsumer gavel = buffer.getBuffer(RenderType.entityCutoutNoCull(GAVEL));
		m.renderPart("B3", poseStack, gavel, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		VertexConsumer coin = buffer.getBuffer(RenderType.entityCutoutNoCull(COIN));
		m.renderPart("B4", poseStack, coin, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}
}
