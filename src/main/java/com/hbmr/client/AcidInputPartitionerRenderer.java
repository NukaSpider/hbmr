package com.hbmr.client;

import com.hbmr.HBMR;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Matches 1.7.10 {@code RenderPartitioner}: per-part icons on {@code crane_buffer.obj}.
 * Belt uses {@code crane_splitter_belt} so the animated atlas frames scroll like the splitter.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AcidInputPartitionerRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/crane_buffer.obj");

	private static final ResourceLocation SIDE = blockTex("crane_partitioner_side");
	private static final ResourceLocation BACK = blockTex("crane_partitioner_back");
	private static final ResourceLocation TOP = blockTex("crane_top");
	private static final ResourceLocation INNER = blockTex("crane_splitter_inner");
	private static final ResourceLocation INNER_SIDE = blockTex("crane_splitter_inner_side");
	private static final ResourceLocation BELT = blockTex("crane_splitter_belt");

	private static WavefrontObjModel model;

	private AcidInputPartitionerRenderer() {
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
			// HFRWavefrontObject uses (u, 1-v).
			model = WavefrontObjModel.load(MODEL, true);
		}
		return model;
	}

	private static TextureAtlasSprite sprite(ResourceLocation id) {
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(id);
	}

	/**
	 * Yaw so the open conveyor (+X in the OBJ) faces {@code FACING}.
	 * Placement uses furnace convention (look-opposite), so the belt faces away
	 * from the player on place.
	 */
	public static float hbmYaw(Direction facing) {
		return switch (facing) {
			case NORTH -> 90.0F;
			case SOUTH -> 270.0F;
			case WEST -> 180.0F;
			case EAST -> 0.0F;
			default -> 0.0F;
		};
	}

	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, Direction facing) {
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.0D, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(hbmYaw(facing)));
		draw(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}

	/** Inventory mesh; isometric GUI pose comes from the item model display. */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		poseStack.pushPose();
		poseStack.translate(0.0D, -0.5D, 0.0D);
		draw(poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();
	}

	private static void draw(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		WavefrontObjModel m = model();
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		part(m, poseStack, vc, packedLight, packedOverlay, "Side", SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "Back", BACK);
		part(m, poseStack, vc, packedLight, packedOverlay, "Top_Top.001", TOP);
		part(m, poseStack, vc, packedLight, packedOverlay, "Inner", INNER);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerSide", INNER_SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "Belt", BELT);
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
