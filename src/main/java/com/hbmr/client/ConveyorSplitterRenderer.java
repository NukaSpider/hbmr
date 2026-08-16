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
 * Matches 1.7.10 {@code RenderSplitter}: per-part icons on {@code splitter.obj},
 * left (core) + right (east dummy) halves. Textures come from the block atlas so
 * animated belt frames (and IIcon-style UVs) match 1.7.10.
 * <p>
 * World uses Front-faces-{@code FACING} yaw (placement is look-opposite), which
 * inverts Left/Right vs half placement compared to 1.7 meta yaw — outer panels
 * are swapped in {@link #renderWorld} only. Inventory keeps 1.7 −90° assignment.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ConveyorSplitterRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/multiblock/splitter.obj");

	private static final ResourceLocation TOP_LEFT = blockTex("crane_splitter_top_left");
	private static final ResourceLocation TOP_RIGHT = blockTex("crane_splitter_top_right");
	private static final ResourceLocation FRONT_LEFT = blockTex("crane_splitter_front_left");
	private static final ResourceLocation FRONT_RIGHT = blockTex("crane_splitter_front_right");
	private static final ResourceLocation BACK_LEFT = blockTex("crane_splitter_back_left");
	private static final ResourceLocation BACK_RIGHT = blockTex("crane_splitter_back_right");
	private static final ResourceLocation LEFT = blockTex("crane_splitter_left");
	private static final ResourceLocation RIGHT = blockTex("crane_splitter_right");
	private static final ResourceLocation INNER = blockTex("crane_splitter_inner");
	private static final ResourceLocation INNER_SIDE = blockTex("crane_splitter_inner_side");
	private static final ResourceLocation BELT = blockTex("crane_splitter_belt");

	private static WavefrontObjModel model;

	private ConveyorSplitterRenderer() {
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
	 * Yaw so mesh Front (−X) faces {@code facing}.
	 * Placement uses look{@code getOpposite()} (furnace convention); 1.7.10 Dummyable
	 * meta is look-direction, so their RenderSplitter table is inverted vs our FACING.
	 */
	public static float hbmYaw(Direction facing) {
		return switch (facing) {
			case NORTH -> 270.0F;
			case SOUTH -> 90.0F;
			case WEST -> 0.0F;
			case EAST -> 180.0F;
			default -> 0.0F;
		};
	}

	/**
	 * World: both halves from the controller. Pose is already at core center
	 * ({@code +0.5,0,+0.5}) with MultiblockBER {@code facingToYRot} applied — undo that,
	 * apply front-facing yaw, then draw left at core and right on the east-dim neighbor.
	 */
	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, Direction facing, float facingToYRot, long gameTime) {
		poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot));
		poseStack.mulPose(Axis.YP.rotationDegrees(hbmYaw(facing)));

		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		renderHalf(poseStack, vc, packedLight, packedOverlay, true, true);

		int dx = 0;
		int dz = 0;
		switch (facing) {
			case SOUTH -> dx = 1;
			case NORTH -> dx = -1;
			case EAST -> dz = -1;
			case WEST -> dz = 1;
			default -> {
			}
		}
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(-hbmYaw(facing)));
		poseStack.translate(dx, 0.0D, dz);
		poseStack.mulPose(Axis.YP.rotationDegrees(hbmYaw(facing)));
		renderHalf(poseStack, vc, packedLight, packedOverlay, false, true);
		poseStack.popPose();
	}

	/**
	 * Inventory: matches 1.7.10 {@code renderInventoryBlock}
	 * (scale handled by MultiblockItemRenderer; we do −90 Y and both halves).
	 */
	public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay) {
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		poseStack.translate(0.0D, -0.5D, 0.5D);
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		renderHalf(poseStack, vc, packedLight, packedOverlay, true, false);
		poseStack.translate(0.0D, 0.0D, -1.0D);
		renderHalf(poseStack, vc, packedLight, packedOverlay, false, false);
	}

	/**
	 * @param swapOuterSides world Front-faces-FACING yaw inverts Left/Right vs half placement;
	 *                       inventory −90° yaw does not need the swap.
	 */
	private static void renderHalf(PoseStack poseStack, VertexConsumer vc, int packedLight,
			int packedOverlay, boolean isLeft, boolean swapOuterSides) {
		WavefrontObjModel m = model();
		part(m, poseStack, vc, packedLight, packedOverlay, "Top", isLeft ? TOP_LEFT : TOP_RIGHT);
		part(m, poseStack, vc, packedLight, packedOverlay, "Bottom", isLeft ? TOP_RIGHT : TOP_LEFT);
		boolean outerIsRight = isLeft == swapOuterSides;
		if (outerIsRight) {
			part(m, poseStack, vc, packedLight, packedOverlay, "Right", RIGHT);
		} else {
			part(m, poseStack, vc, packedLight, packedOverlay, "Left", LEFT);
		}
		part(m, poseStack, vc, packedLight, packedOverlay, "Back", isLeft ? BACK_LEFT : BACK_RIGHT);
		part(m, poseStack, vc, packedLight, packedOverlay, "Front", isLeft ? FRONT_LEFT : FRONT_RIGHT);
		part(m, poseStack, vc, packedLight, packedOverlay, "Inner", INNER);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerLeft", INNER_SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerRight", INNER_SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerTop", INNER_SIDE);
		part(m, poseStack, vc, packedLight, packedOverlay, "InnerBottom", BELT);
	}

	/** Draw a part with 0–1 OBJ UVs remapped into the current atlas sprite (1.7 IIcon style). */
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
