package com.hbmr.client;

import com.hbmr.block.multiblock.StructureType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * RBMK column visuals matching 1.7.10 {@code RenderRBMKRod} / {@code RenderRBMKControl} /
 * {@code RenderRBMKReflector}: fuel channels use Cap/Inner OBJs (empty by default);
 * control/boiler/heater use cubes + pipe stubs + Lid; passive columns are textured cubes.
 */
@Mod.EventBusSubscriber(modid = "hbmr", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RBMKColumnRenderer {
	public static final int COLUMN_HEIGHT = 4;

	private static final ResourceLocation ELEMENT_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/rbmk/rbmk_element.obj");
	private static final ResourceLocation LID_OBJ =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/rbmk/rbmk_rods.obj");

	private static WavefrontObjModel elementModel;
	private static WavefrontObjModel lidModel;

	private RBMKColumnRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> {
			elementModel = null;
			lidModel = null;
		});
	}

	public static boolean isColumn(StructureType type) {
		return type.isRbmkColumn();
	}

	private enum Kind {
		FUEL, CONTROL, PIPED, PASSIVE
	}

	private static Kind kind(StructureType type) {
		return switch (type) {
			case RBMK_FUEL_CHANNEL, RBMK_MODERATED_FUEL_CHANNEL,
					RBMK_FUEL_CHANNEL_REASIM, RBMK_MODERATED_FUEL_CHANNEL_REASIM -> Kind.FUEL;
			case RBMK_CONTROL_RODS, RBMK_MODERATED_CONTROL_RODS, RBMK_AUTOMATIC_CONTROL_RODS,
					RBMK_CONTROL_RODS_REASIM, RBMK_AUTOMATIC_CONTROL_RODS_REASIM -> Kind.CONTROL;
			case RBMK_STEAM_CHANNEL, RBMK_FLUID_HEATER -> Kind.PIPED;
			default -> Kind.PASSIVE;
		};
	}

	public static void renderWorld(StructureType type, PoseStack poseStack, MultiBufferSource buffer,
			int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// BER already centered at block; OBJ/cubes use local 0…1 with center origin for OBJ.
		renderColumn(type, poseStack, buffer, packedLight, packedOverlay, false);
		poseStack.popPose();
	}

	public static void renderItem(StructureType type, PoseStack poseStack, MultiBufferSource buffer,
			int packedLight, int packedOverlay) {
		poseStack.pushPose();
		Kind k = kind(type);
		// Control inventory uses −0.75; fuel/passive use −0.675.
		poseStack.translate(0.0D, k == Kind.CONTROL || k == Kind.PIPED ? -0.75D : -0.675D, 0.0D);
		poseStack.scale(0.35F, 0.35F, 0.35F);
		renderColumn(type, poseStack, buffer, packedLight, packedOverlay, true);
		poseStack.popPose();
	}

	private static void renderColumn(StructureType type, PoseStack poseStack, MultiBufferSource buffer,
			int light, int overlay, boolean item) {
		Kind k = kind(type);
		String id = type.name().toLowerCase();

		for (int i = 0; i < COLUMN_HEIGHT; i++) {
			poseStack.pushPose();
			poseStack.translate(0.0D, i, 0.0D);
			switch (k) {
				case FUEL -> renderFuelLayer(id, poseStack, buffer, light, overlay);
				// Full cube including top — 1.7.10 ISBRH renderStandardBlock. Omitting the top
				// left a gap under the lid (y 1.0…1.125) and the lid's cutout side UVs made the
				// top cell look see-through.
				case CONTROL -> renderColumnCube(id, poseStack, buffer, light, overlay, true);
				// Steam/heater: keep the top platform under the pipe stubs (1.7.10
				// renderStandardBlock + pipes on y+1).
				case PIPED -> renderColumnCube(id, poseStack, buffer, light, overlay, true);
				// Passive columns: immediate side/top binds (MultiBufferSource was painting
				// _top onto every wall for reflector/absorber/moderator/etc.).
				case PASSIVE -> renderColumnCube(id, poseStack, buffer, light, overlay, true);
			}
			poseStack.popPose();
		}

		if (k == Kind.CONTROL || k == Kind.PIPED) {
			poseStack.pushPose();
			poseStack.translate(0.0D, COLUMN_HEIGHT - 1, 0.0D);
			// Pipe PNGs are mostly alpha=0 with black RGB. 1.7.10 draws them in the opaque
			// pass (black panels); entityCutout discards those texels and punches sky holes.
			// UVs must be cropped to each stub's block bounds (RenderBlocks), not full 0–1 —
			// pipe_top is a 2×2 atlas of ports.
			renderPipesImmediate(poseStack, buffer, tex(id + "_pipe_side"), tex(id + "_pipe_top"),
					light, overlay);
			if (k == Kind.CONTROL) {
				// World TESR (RenderRBMKControlRod) binds rbmk_control[_auto].png on Lid —
				// not the block _top icon (that is inventory-only in 1.7.10).
				WavefrontObjModel lid = lid();
				// Cutout for rod holes in the lid plate; cull so inner double-faces don't
				// punch extra sky holes from inside. Opaque column cube underneath fills
				// transparent side UVs (same stack as 1.7 ISBRH + TESR).
				VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(controlLidTex(type)));
				lid.renderPart("Lid", poseStack, vc, 1f, 1f, 1f, 1f, light, overlay);
			}
			poseStack.popPose();
		}
	}

	private static ResourceLocation controlLidTex(StructureType type) {
		return switch (type) {
			case RBMK_AUTOMATIC_CONTROL_RODS, RBMK_AUTOMATIC_CONTROL_RODS_REASIM ->
					tex("rbmk_control_auto");
			default -> tex("rbmk_control");
		};
	}

	private static void renderFuelLayer(String id, PoseStack poseStack, MultiBufferSource buffer,
			int light, int overlay) {
		ResourceLocation side = tex(id + "_side");
		ResourceLocation top = tex(id + "_top");
		ResourceLocation inner = tex(id + "_inner");

		// Outer shell sides only (open top/bottom like 1.7.10 overrideOnlyRenderSides).
		renderCubeSides(poseStack, buffer, side, light, overlay);

		WavefrontObjModel element = element();
		VertexConsumer capVc = buffer.getBuffer(RenderType.entityCutoutNoCull(top));
		element.renderPart("Cap", poseStack, capVc, 1f, 1f, 1f, 1f, light, overlay);
		VertexConsumer innerVc = buffer.getBuffer(RenderType.entityCutoutNoCull(inner));
		element.renderPart("Inner", poseStack, innerVc, 1f, 1f, 1f, 1f, light, overlay);
		// Empty by default — Rods only when fuel is inserted (later).
	}

	private static void renderPipesImmediate(PoseStack poseStack, MultiBufferSource buffer,
			ResourceLocation pipeSide, ResourceLocation pipeTop, int light, int overlay) {
		// Four 0.375×0.125×0.375 stubs at y+1 of the top cell (RenderRBMKControl).
		float h = 0.125F;
		float[][] boxes = {
				{0.0625F, 0.4375F, 0.0625F, 0.4375F},
				{0.0625F, 0.4375F, 0.5625F, 0.9375F},
				{0.5625F, 0.9375F, 0.5625F, 0.9375F},
				{0.5625F, 0.9375F, 0.0625F, 0.4375F},
		};
		// Do not MultiBufferSource.endBatch() here — that flushes translucent BER geometry
		// (e.g. debris flames) early so later opaque columns draw over them.
		poseStack.pushPose();
		poseStack.translate(-0.5D, 1.0D, -0.5D);

		var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
		var overlayTexture = Minecraft.getInstance().gameRenderer.overlayTexture();
		lightTexture.turnOnLightLayer();
		overlayTexture.setupOverlayColor();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShader(GameRenderer::getRendertypeEntitySolidShader);

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buf = tesselator.getBuilder();

		RenderSystem.setShaderTexture(0, pipeSide);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
		for (float[] b : boxes) {
			emitPipeBoxSides(poseStack, buf, b[0], 0f, b[2], b[1], h, b[3], light, overlay);
		}
		BufferUploader.drawWithShader(buf.end());

		RenderSystem.setShaderTexture(0, pipeTop);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
		for (float[] b : boxes) {
			emitPipeBoxEnds(poseStack, buf, b[0], 0f, b[2], b[1], h, b[3], light, overlay);
		}
		BufferUploader.drawWithShader(buf.end());

		overlayTexture.teardownOverlayColor();
		lightTexture.turnOffLightLayer();
		poseStack.popPose();
	}

	/**
	 * Pipe stub sides with UVs cropped to block-local bounds (1.7.10 {@code RenderBlocks}).
	 */
	private static void emitPipeBoxSides(PoseStack poseStack, VertexConsumer side,
			float x0, float y0, float z0, float x1, float y1, float z1, int light, int overlay) {
		PoseStack.Pose last = poseStack.last();
		Matrix4f m = last.pose();
		Matrix3f n = last.normal();
		face(side, m, n, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1,
				x0, 1f - y0, x1, 1f - y0, x1, 1f - y1, x0, 1f - y1, 0, 0, 1, light, overlay);
		face(side, m, n, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0,
				1f - x1, 1f - y0, 1f - x0, 1f - y0, 1f - x0, 1f - y1, 1f - x1, 1f - y1, 0, 0, -1, light, overlay);
		face(side, m, n, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1,
				1f - z1, 1f - y0, 1f - z0, 1f - y0, 1f - z0, 1f - y1, 1f - z1, 1f - y1, 1, 0, 0, light, overlay);
		face(side, m, n, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0,
				z0, 1f - y0, z1, 1f - y0, z1, 1f - y1, z0, 1f - y1, -1, 0, 0, light, overlay);
	}

	/**
	 * Pipe stub ends — {@code pipe_top} is a 2×2 port atlas; UVs stay in each stub's quadrant.
	 */
	private static void emitPipeBoxEnds(PoseStack poseStack, VertexConsumer top,
			float x0, float y0, float z0, float x1, float y1, float z1, int light, int overlay) {
		PoseStack.Pose last = poseStack.last();
		Matrix4f m = last.pose();
		Matrix3f n = last.normal();
		face(top, m, n, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
				x0, z1, x1, z1, x1, z0, x0, z0, 0, 1, 0, light, overlay);
		face(top, m, n, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1,
				x0, 1f - z0, x1, 1f - z0, x1, 1f - z1, x0, 1f - z1, 0, -1, 0, light, overlay);
	}

	/**
	 * Column body with immediate side then top draws. Binding each PNG and uploading before
	 * the next face set avoids MultiBufferSource reusing the end tile on walls.
	 * <p>
	 * Does not call {@code endBatch()} — flushing the shared BER buffer early draws
	 * translucent geometry (debris flames) before later opaque columns, so fire ends up behind rods.
	 */
	private static void renderColumnCube(String id, PoseStack poseStack, MultiBufferSource buffer,
			int light, int overlay, boolean drawTop) {
		renderCubeImmediate(poseStack, tex(id + "_side"), tex(id + "_top"), light, overlay, drawTop);
	}

	private static void renderCubeImmediate(PoseStack poseStack, ResourceLocation side, ResourceLocation top,
			int light, int overlay, boolean drawTop) {
		poseStack.pushPose();
		poseStack.translate(-0.5D, 0.0D, -0.5D);

		var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
		var overlayTexture = Minecraft.getInstance().gameRenderer.overlayTexture();
		lightTexture.turnOnLightLayer();
		overlayTexture.setupOverlayColor();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShader(GameRenderer::getRendertypeEntitySolidShader);

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buf = tesselator.getBuilder();

		RenderSystem.setShaderTexture(0, side);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
		emitCubeSides(poseStack, buf, light, overlay, 0f, 1f, 0f, 1f);
		BufferUploader.drawWithShader(buf.end());

		RenderSystem.setShaderTexture(0, top);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
		emitCubeEnds(poseStack, buf, light, overlay, drawTop, 0f, 1f, 0f, 1f);
		BufferUploader.drawWithShader(buf.end());

		overlayTexture.teardownOverlayColor();
		lightTexture.turnOffLightLayer();
		poseStack.popPose();
	}

	private static void renderCubeSides(PoseStack poseStack, MultiBufferSource buffer,
			ResourceLocation side, int light, int overlay) {
		poseStack.pushPose();
		poseStack.translate(-0.5D, 0.0D, -0.5D);
		VertexConsumer sideVc = buffer.getBuffer(RenderType.entityCutoutNoCull(side));
		emitCubeSides(poseStack, sideVc, light, overlay, 0f, 1f, 0f, 1f);
		poseStack.popPose();
	}

	private static void emitCubeSides(PoseStack poseStack, VertexConsumer side, int light, int overlay,
			float u0, float u1, float v0, float v1) {
		PoseStack.Pose last = poseStack.last();
		Matrix4f m = last.pose();
		Matrix3f n = last.normal();
		face(side, m, n, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
				u0, v1, u1, v1, u1, v0, u0, v0, 0, 0, 1, light, overlay);
		face(side, m, n, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0,
				u0, v1, u1, v1, u1, v0, u0, v0, 0, 0, -1, light, overlay);
		face(side, m, n, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1,
				u0, v1, u1, v1, u1, v0, u0, v0, 1, 0, 0, light, overlay);
		face(side, m, n, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0,
				u0, v1, u1, v1, u1, v0, u0, v0, -1, 0, 0, light, overlay);
	}

	private static void emitCubeEnds(PoseStack poseStack, VertexConsumer top, int light, int overlay,
			boolean drawTop, float u0, float u1, float v0, float v1) {
		PoseStack.Pose last = poseStack.last();
		Matrix4f m = last.pose();
		Matrix3f n = last.normal();
		if (drawTop) {
			face(top, m, n, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0,
					u0, v1, u1, v1, u1, v0, u0, v0, 0, 1, 0, light, overlay);
		}
		face(top, m, n, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1,
				u0, v1, u1, v1, u1, v0, u0, v0, 0, -1, 0, light, overlay);
	}

	private static void face(VertexConsumer vc, Matrix4f m, Matrix3f normalMat,
			float x0, float y0, float z0, float x1, float y1, float z1,
			float x2, float y2, float z2, float x3, float y3, float z3,
			float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3,
			float nx, float ny, float nz, int light, int overlay) {
		Vector3f n = new Vector3f(nx, ny, nz);
		normalMat.transform(n);
		if (n.lengthSquared() > 1e-6f) {
			n.normalize();
		}
		vert(vc, m, x0, y0, z0, u0, v0, n, light, overlay);
		vert(vc, m, x1, y1, z1, u1, v1, n, light, overlay);
		vert(vc, m, x2, y2, z2, u2, v2, n, light, overlay);
		vert(vc, m, x3, y3, z3, u3, v3, n, light, overlay);
	}

	private static void vert(VertexConsumer vc, Matrix4f m, float x, float y, float z,
			float u, float v, Vector3f n, int light, int overlay) {
		vc.vertex(m, x, y, z).color(1f, 1f, 1f, 1f).uv(u, v)
				.overlayCoords(overlay).uv2(light).normal(n.x, n.y, n.z).endVertex();
	}

	private static ResourceLocation tex(String path) {
		return ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/" + path + ".png");
	}

	private static WavefrontObjModel element() {
		if (elementModel == null) {
			elementModel = WavefrontObjModel.load(ELEMENT_OBJ, true);
		}
		return elementModel;
	}

	private static WavefrontObjModel lid() {
		if (lidModel == null) {
			// HFRWavefrontObject.parseTextureCoordinate uses (u, 1-v) — same as flipV=true.
			lidModel = WavefrontObjModel.load(LID_OBJ, true);
		}
		return lidModel;
	}
}
