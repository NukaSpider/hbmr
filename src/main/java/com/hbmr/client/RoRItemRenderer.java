package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.item.RoRBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Inventory renderer matching 1.7.10 {@code RBMKMiniPanelBase#renderInventoryBlock}
 * plus each panel's overlay OBJs (keypad buttons, levers, gauges, etc.).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RoRItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static final ResourceLocation PANEL_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/rbmk_display.png");
	private static final ResourceLocation KEYPAD_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/ror/keypad.png");
	private static final ResourceLocation LEVER_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/ror/lever.png");
	private static final ResourceLocation GAUGE_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/ror/gauge.png");
	private static final ResourceLocation INDICATOR_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/ror/indicator.png");
	private static final ResourceLocation NUMITRON_TEX =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/ror/numitron.png");

	private static final ResourceLocation BUTTON_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/ror/button.obj");
	private static final ResourceLocation LEVER_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/ror/lever.obj");
	private static final ResourceLocation GAUGE_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/ror/gauge.obj");
	private static final ResourceLocation INDICATOR_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/ror/indicator.obj");
	private static final ResourceLocation NUMITRON_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/ror/numitron.obj");

	private static RoRItemRenderer instance;
	private static WavefrontObjModel button;
	private static WavefrontObjModel lever;
	private static WavefrontObjModel gauge;
	private static WavefrontObjModel indicator;
	private static WavefrontObjModel numitron;

	public static RoRItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new RoRItemRenderer(mc);
		}
		return instance;
	}

	private RoRItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> {
			button = lever = gauge = indicator = numitron = null;
		});
	}

	private static WavefrontObjModel button() {
		if (button == null) {
			button = WavefrontObjModel.load(BUTTON_OBJ, true);
		}
		return button;
	}

	private static WavefrontObjModel lever() {
		if (lever == null) {
			lever = WavefrontObjModel.load(LEVER_OBJ, true);
		}
		return lever;
	}

	private static WavefrontObjModel gauge() {
		if (gauge == null) {
			gauge = WavefrontObjModel.load(GAUGE_OBJ, true);
		}
		return gauge;
	}

	private static WavefrontObjModel indicator() {
		if (indicator == null) {
			indicator = WavefrontObjModel.load(INDICATOR_OBJ, true);
		}
		return indicator;
	}

	private static WavefrontObjModel numitron() {
		if (numitron == null) {
			numitron = WavefrontObjModel.load(NUMITRON_OBJ, true);
		}
		return numitron;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!(stack.getItem() instanceof RoRBlockItem item)) {
			return;
		}

		poseStack.pushPose();
		boolean gui = ctx == ItemDisplayContext.GUI || ctx == ItemDisplayContext.FIXED;
		if (gui) {
			poseStack.translate(0.5D, 0.5D, 0.0D);
			poseStack.mulPose(Axis.XP.rotationDegrees(30));
			poseStack.mulPose(Axis.YP.rotationDegrees(225));
			poseStack.scale(0.625F, 0.625F, 0.625F);
			// 1.20 GUI view shows the slab's thick back; face the cut side + overlays at camera.
			poseStack.mulPose(Axis.YP.rotationDegrees(180));
		} else {
			poseStack.translate(0.5D, 0.25D, 0.5D);
			poseStack.scale(0.4F, 0.4F, 0.4F);
		}

		// Panel body (1.7.10 inventory: +90 Y, −0.5, slab on +X).
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(90));
		poseStack.translate(-0.5D, -0.5D, -0.5D);
		renderPanel(poseStack, buffer, stack, packedLight, packedOverlay);
		poseStack.popPose();

		// Overlays (1.7.10: Y−0.5 then −90 Y).
		poseStack.pushPose();
		poseStack.translate(0.0D, -0.5D, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(-90));
		renderOverlays(item.getKind(), poseStack, buffer, packedLight, packedOverlay);
		poseStack.popPose();

		poseStack.popPose();
	}

	private static void renderPanel(PoseStack pose, MultiBufferSource buffer, ItemStack stack,
			int light, int overlay) {
		// Use the item's block model if present; else draw nothing beyond overlays.
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
		if (model == null || model.isCustomRenderer()) {
			// builtin/entity has no quads — draw a simple slab with the panel texture.
			VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(PANEL_TEX));
			emitBox(pose, vc, 0.25f, 0f, 0f, 1f, 1f, 1f, light, overlay);
			return;
		}
		ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
		VertexConsumer vc = ItemRenderer.getFoilBufferDirect(buffer, RenderType.entityCutout(
				net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS), true, stack.hasFoil());
		ir.renderModelLists(model, stack, light, overlay, pose, vc);
	}

	private static void renderOverlays(RoRBlockItem.RoRKind kind, PoseStack pose,
			MultiBufferSource buffer, int light, int overlay) {
		switch (kind) {
			case KEYPAD -> {
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(KEYPAD_TEX));
				for (int i = 0; i < 4; i++) {
					pose.pushPose();
					pose.translate(0.25D, (i / 2) * -0.5D + 0.25D, (i % 2) * -0.5D + 0.25D);
					button().renderPart("Socket", pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					button().renderPart("Button", pose, vc, 0.65f, 0f, 0f, 1f, light, overlay);
					pose.popPose();
				}
			}
			case LEVER -> {
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(LEVER_TEX));
				for (int i = 0; i < 2; i++) {
					pose.pushPose();
					pose.translate(0.25D, 0.0D, i * -0.5D + 0.25D);
					lever().renderPart("Base", pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					lever().renderPart("Lever", pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					pose.popPose();
				}
			}
			case GAUGE -> {
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(GAUGE_TEX));
				for (int i = 0; i < 4; i++) {
					pose.pushPose();
					pose.translate(0.25D, (i / 2) * -0.5D + 0.25D, (i % 2) * -0.5D + 0.25D);
					gauge().renderPart("Gauge", pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					pose.pushPose();
					pose.translate(0.0D, 0.4375D, -0.125D);
					pose.mulPose(Axis.XP.rotationDegrees(85));
					pose.translate(0.0D, -0.4375D, 0.125D);
					gauge().renderPart("Needle", pose, vc, 0.5f, 0f, 0f, 1f, light, overlay);
					pose.popPose();
					pose.popPose();
				}
			}
			case INDICATOR -> {
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(INDICATOR_TEX));
				for (int i = 0; i < 6; i++) {
					pose.pushPose();
					pose.translate(0.25D, (i / 2) * -0.3125D + 0.3125D, (i % 2) * 0.5D - 0.25D);
					indicator().renderAll(pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					pose.popPose();
				}
			}
			case NUMERIC, GRAPH -> {
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(NUMITRON_TEX));
				for (int i = 0; i < 2; i++) {
					pose.pushPose();
					pose.translate(0.25D, i * -0.5D + 0.25D, 0.0D);
					numitron().renderAll(pose, vc, 1f, 1f, 1f, 1f, light, overlay);
					pose.popPose();
				}
			}
			case BLANK, DISPLAY -> {
			}
		}
	}

	/** Axis-aligned box in local space with full UV on each face. */
	private static void emitBox(PoseStack poseStack, VertexConsumer vc,
			float x0, float y0, float z0, float x1, float y1, float z1, int light, int overlay) {
		var pose = poseStack.last();
		// +Z
		quad(vc, pose, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1, light, overlay);
		// -Z
		quad(vc, pose, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1, light, overlay);
		// +Y
		quad(vc, pose, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0, light, overlay);
		// -Y
		quad(vc, pose, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0, light, overlay);
		// +X
		quad(vc, pose, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0, light, overlay);
		// -X (front of panel slab)
		quad(vc, pose, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0, light, overlay);
	}

	private static void quad(VertexConsumer vc, PoseStack.Pose pose,
			float x0, float y0, float z0, float x1, float y1, float z1,
			float x2, float y2, float z2, float x3, float y3, float z3,
			float nx, float ny, float nz, int light, int overlay) {
		vc.vertex(pose.pose(), x0, y0, z0).color(1f, 1f, 1f, 1f).uv(0f, 1f)
				.overlayCoords(overlay).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
		vc.vertex(pose.pose(), x1, y1, z1).color(1f, 1f, 1f, 1f).uv(1f, 1f)
				.overlayCoords(overlay).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
		vc.vertex(pose.pose(), x2, y2, z2).color(1f, 1f, 1f, 1f).uv(1f, 0f)
				.overlayCoords(overlay).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
		vc.vertex(pose.pose(), x3, y3, z3).color(1f, 1f, 1f, 1f).uv(0f, 0f)
				.overlayCoords(overlay).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
	}
}
