package com.hbmr.client;

import com.hbmr.HBMR;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Old capacitor matching 1.7.10 {@code RenderCapacitor}: per-part atlas sprites on
 * {@code capacitor.obj} (same IIcon remap as the splitter / partitioner).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CapacitorRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/old_capacitor.obj");

	private static final ResourceLocation TOP = blockTex("capacitor_copper_top");
	private static final ResourceLocation SIDE = blockTex("capacitor_copper_side");
	private static final ResourceLocation BOTTOM = blockTex("capacitor_copper_bottom");
	private static final ResourceLocation INNER_TOP = blockTex("capacitor_copper_inner_top");
	private static final ResourceLocation INNER_SIDE = blockTex("capacitor_copper_inner_side");

	private static WavefrontObjModel model;

	private CapacitorRenderer() {
	}

	private static ResourceLocation blockTex(String name) {
		return ResourceLocation.fromNamespaceAndPath("hbmr", "block/" + name);
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

	private static TextureAtlasSprite sprite(ResourceLocation id) {
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(id);
	}

	public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		part(m, poseStack, vc, packedLight, packedOverlay, "Top", TOP);
		part(m, poseStack, vc, packedLight, packedOverlay, "Side", SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "Bottom", BOTTOM);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerTop", INNER_TOP);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerSide", INNER_SIDE);
	}

	private static void part(WavefrontObjModel m, PoseStack poseStack, VertexConsumer vc,
			int packedLight, int packedOverlay, String name, ResourceLocation spriteId) {
		TextureAtlasSprite sprite = sprite(spriteId);
		float u0 = sprite.getU0();
		float v0 = sprite.getV0();
		float uScale = sprite.getU1() - u0;
		float vScale = sprite.getV1() - v0;
		m.renderPart(name, poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay,
				uScale, vScale, u0, v0);
	}
}
