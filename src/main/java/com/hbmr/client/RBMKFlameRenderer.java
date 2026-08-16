package com.hbmr.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * Animated RBMK debris fire matching 1.7.10 {@code ParticleRBMKFlame}:
 * strip texture with black = empty, additive {@code SRC_ALPHA}/{@code ONE} so black drops out.
 */
public final class RBMKFlameRenderer {
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath("hbmr", "textures/particle/rbmk_fire.png");
	private static final int FRAMES = 14;
	/**
	 * Local offsets (block-centered) + scale. Spawn height matches 1.7.10
	 * {@code y + 1.75}; scale range {@code 1..2}.
	 */
	private static final float[][] FLAMES = {
			{0.10F, 1.75F, 0.05F, 1.25F},
			{-0.15F, 1.75F, -0.10F, 1.55F},
			{0.05F, 1.75F, 0.20F, 1.10F},
	};

	private RBMKFlameRenderer() {
	}

	/**
	 * Pose must be centered on the block (0.5, 0, 0.5) with facing rotation undone.
	 */
	public static void render(PoseStack poseStack, MultiBufferSource buffer, long gameTime,
			float partialTick, BlockPos pos) {
		float age = gameTime + partialTick;
		float camYaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
		VertexConsumer vc = buffer.getBuffer(FlameRenderType.TYPE);

		for (int i = 0; i < FLAMES.length; i++) {
			float[] f = FLAMES[i];
			// 1.7.10: texIndex = particleAge * 5 % 14; full strip is 14 frames wide.
			int texIndex = ((int) age * 5 + i * 3) % FRAMES;
			float f0 = 1.0F / FRAMES;
			float uMin = texIndex * f0;
			float uMax = uMin + f0;
			float alpha = 0.5F;
			poseStack.pushPose();
			poseStack.translate(f[0], f[1], f[2]);
			poseStack.mulPose(Axis.YP.rotationDegrees(-camYaw));
			emitBillboard(poseStack, vc, f[3], uMin, uMax, alpha);
			poseStack.popPose();
		}
	}

	private static void emitBillboard(PoseStack poseStack, VertexConsumer vc, float scale,
			float uMin, float uMax, float alpha) {
		Matrix4f m = poseStack.last().pose();
		// Centered on the spawn point. 1.7.10 used (±scale - 1) plus a camera-basis
		// translate that cancelled the -1; we only rotate yaw, so keep the quad centered.
		float x0 = -scale;
		float x1 = scale;
		float y0 = -scale * 2.0F;
		float y1 = scale * 2.0F;
		vert(vc, m, x0, y0, uMax, 1f, alpha);
		vert(vc, m, x0, y1, uMax, 0f, alpha);
		vert(vc, m, x1, y1, uMin, 0f, alpha);
		vert(vc, m, x1, y0, uMin, 1f, alpha);
	}

	private static void vert(VertexConsumer vc, Matrix4f m,
			float x, float y, float u, float v, float a) {
		vc.vertex(m, x, y, 0f)
				.color(1f, 1f, 1f, a)
				.uv(u, v)
				.uv2(0xF000F0)
				.endVertex();
	}

	private static final class FlameRenderType extends RenderType {
		private static final TransparencyStateShard SRC_ALPHA_ONE = new TransparencyStateShard(
				"hbmr_src_alpha_one",
				() -> {
					RenderSystem.enableBlend();
					RenderSystem.depthMask(false);
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				},
				() -> {
					RenderSystem.depthMask(true);
					RenderSystem.disableBlend();
					RenderSystem.defaultBlendFunc();
				});

		static final RenderType TYPE = create(
				"hbmr_rbmk_flame",
				DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
				VertexFormat.Mode.QUADS,
				256,
				false,
				true,
				CompositeState.builder()
						.setShaderState(new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
						.setTextureState(new TextureStateShard(TEXTURE, false, false))
						.setTransparencyState(SRC_ALPHA_ONE)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.setCullState(NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setWriteMaskState(COLOR_WRITE)
						.createCompositeState(false));

		private FlameRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
				boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
			super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
		}
	}
}
