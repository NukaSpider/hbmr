package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.multiblock.StructureType;
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
 * Fluid barrels matching 1.7.10 {@code RenderBarrel}: {@code barrel.obj} "Barrel" part
 * with a direct texture bind. Atlases are {@code 48×48} (non-PoT) — baking into the
 * block atlas remaps UVs so every barrel reads as the same tiny blue cube.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BarrelRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/safe_barrel.obj");

	private static WavefrontObjModel model;

	private BarrelRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> model = null);
	}

	private static WavefrontObjModel model() {
		if (model == null) {
			// HFRWavefrontObject uses (u, 1-v).
			model = WavefrontObjModel.load(MODEL, true);
		}
		return model;
	}

	public static ResourceLocation texture(StructureType type) {
		String name = switch (type) {
			case STEEL_BARREL -> "steel_barrel";
			case TECHNETIUM_STEEL_BARREL -> "technetium_steel_barrel";
			case MAGNETIC_ANTIMATTER_CONTAINER -> "magnetic_antimatter_container";
			default -> "safe_barrel";
		};
		return ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/multiblock/" + name + ".png");
	}

	public static boolean isBarrel(StructureType type) {
		return type == StructureType.SAFE_BARREL
				|| type == StructureType.STEEL_BARREL
				|| type == StructureType.TECHNETIUM_STEEL_BARREL
				|| type == StructureType.MAGNETIC_ANTIMATTER_CONTAINER;
	}

	/** World + inventory: Barrel part only (Connector is fluid TESR in 1.7). */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, StructureType type) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(texture(type)));
		m.renderPart("Barrel", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
	}
}
