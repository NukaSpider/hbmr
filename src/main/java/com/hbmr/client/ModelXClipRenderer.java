package com.hbmr.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Clips baked door-leaf quads to an X range in model space (1.7.10 {@code glClipPlane}).
 * Emits degenerate quads so it stays compatible with {@code RenderType.entityCutout} (QUADS).
 */
final class ModelXClipRenderer {
	private static final RandomSource RANDOM = RandomSource.create();

	private ModelXClipRenderer() {
	}

	/**
	 * @param slideOffsetX leaf translation already applied on the pose stack
	 * @param clipHalf     keep {@code |modelX + slideOffsetX| <= clipHalf} (vehicle door: 3.4375)
	 */
	static void render(PoseStack poseStack, VertexConsumer consumer, BlockState state, BakedModel model,
			int packedLight, int packedOverlay, float slideOffsetX, float clipHalf) {
		float minModelX = -clipHalf - slideOffsetX;
		float maxModelX = clipHalf - slideOffsetX;
		renderAxisBand(poseStack, consumer, state, model, packedLight, packedOverlay, 0, minModelX, maxModelX);
	}

	/**
	 * Y clip matching 1.7.10 containment {@code glClipPlane(0,-1,0, maxY)} — keep
	 * {@code modelY + slideOffsetY <= maxWorldY}.
	 */
	static void renderYMax(PoseStack poseStack, VertexConsumer consumer, BlockState state, BakedModel model,
			int packedLight, int packedOverlay, float slideOffsetY, float maxWorldY) {
		float maxModelY = maxWorldY - slideOffsetY;
		renderAxisBand(poseStack, consumer, state, model, packedLight, packedOverlay,
				1, Float.NEGATIVE_INFINITY, maxModelY);
	}

	/**
	 * Z clip matching 1.7.10 airlock {@code |z| <= clipHalf} (1.999).
	 */
	static void renderZ(PoseStack poseStack, VertexConsumer consumer, BlockState state, BakedModel model,
			int packedLight, int packedOverlay, float slideOffsetZ, float clipHalf) {
		float minModelZ = -clipHalf - slideOffsetZ;
		float maxModelZ = clipHalf - slideOffsetZ;
		renderAxisBand(poseStack, consumer, state, model, packedLight, packedOverlay, 2, minModelZ, maxModelZ);
	}

	/**
	 * Z max clip matching 1.7.10 seal door {@code glClipPlane(0,0,-1, maxZ)} — keep
	 * {@code modelZ + slideOffsetZ <= maxWorldZ}.
	 */
	static void renderZMax(PoseStack poseStack, VertexConsumer consumer, BlockState state, BakedModel model,
			int packedLight, int packedOverlay, float slideOffsetZ, float maxWorldZ) {
		float maxModelZ = maxWorldZ - slideOffsetZ;
		renderAxisBand(poseStack, consumer, state, model, packedLight, packedOverlay,
				2, Float.NEGATIVE_INFINITY, maxModelZ);
	}

	private static void renderAxisBand(PoseStack poseStack, VertexConsumer consumer, BlockState state, BakedModel model,
			int packedLight, int packedOverlay, int axis, float min, float max) {
		PoseStack.Pose pose = poseStack.last();
		RANDOM.setSeed(42L);
		for (Direction dir : Direction.values()) {
			emitQuads(model.getQuads(state, dir, RANDOM, ModelData.EMPTY, null),
					consumer, pose, packedLight, packedOverlay, axis, min, max);
		}
		RANDOM.setSeed(42L);
		emitQuads(model.getQuads(state, null, RANDOM, ModelData.EMPTY, null),
				consumer, pose, packedLight, packedOverlay, axis, min, max);
	}

	private static void emitQuads(List<BakedQuad> quads, VertexConsumer consumer, PoseStack.Pose pose,
			int packedLight, int packedOverlay, int axis, float min, float max) {
		for (BakedQuad quad : quads) {
			emitClippedQuad(quad, consumer, pose, packedLight, packedOverlay, axis, min, max);
		}
	}

	private static void emitClippedQuad(BakedQuad quad, VertexConsumer consumer, PoseStack.Pose pose,
			int packedLight, int packedOverlay, int axis, float min, float max) {
		int[] data = quad.getVertices();
		int stride = data.length / 4;
		if (stride < 8) {
			return;
		}
		List<float[]> poly = new ArrayList<>(8);
		for (int i = 0; i < 4; i++) {
			poly.add(unpack(data, i, stride));
		}
		if (Float.isFinite(min)) {
			poly = clipAxis(poly, axis, min, true);
		}
		if (Float.isFinite(max)) {
			poly = clipAxis(poly, axis, max, false);
		}
		if (poly.size() < 3) {
			return;
		}
		// entityCutout is QUADS — emit each triangle as a degenerate quad (v0,v1,v2,v2).
		float[] v0 = poly.get(0);
		for (int i = 1; i < poly.size() - 1; i++) {
			put(consumer, pose, v0, packedLight, packedOverlay);
			put(consumer, pose, poly.get(i), packedLight, packedOverlay);
			put(consumer, pose, poly.get(i + 1), packedLight, packedOverlay);
			put(consumer, pose, poly.get(i + 1), packedLight, packedOverlay);
		}
	}

	private static List<float[]> clipAxis(List<float[]> poly, int axis, float bound, boolean keepPositive) {
		if (poly.isEmpty()) {
			return poly;
		}
		List<float[]> out = new ArrayList<>(poly.size() + 2);
		for (int i = 0; i < poly.size(); i++) {
			float[] cur = poly.get(i);
			float[] prev = poly.get((i + poly.size() - 1) % poly.size());
			boolean curIn = keepPositive ? cur[axis] >= bound - 1.0e-4F : cur[axis] <= bound + 1.0e-4F;
			boolean prevIn = keepPositive ? prev[axis] >= bound - 1.0e-4F : prev[axis] <= bound + 1.0e-4F;
			if (curIn) {
				if (!prevIn) {
					out.add(intersect(prev, cur, axis, bound));
				}
				out.add(cur);
			} else if (prevIn) {
				out.add(intersect(prev, cur, axis, bound));
			}
		}
		return out;
	}

	private static float[] intersect(float[] a, float[] b, int axis, float bound) {
		float ad = a[axis] - bound;
		float bd = b[axis] - bound;
		float denom = ad - bd;
		float t = Math.abs(denom) < 1.0e-8F ? 0.0F : ad / denom;
		t = Math.max(0.0F, Math.min(1.0F, t));
		float[] r = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			r[i] = a[i] + t * (b[i] - a[i]);
		}
		return r;
	}

	/** x,y,z, r,g,b,a, u,v, nx,ny,nz */
	private static float[] unpack(int[] data, int index, int stride) {
		int o = index * stride;
		float[] v = new float[12];
		v[0] = Float.intBitsToFloat(data[o]);
		v[1] = Float.intBitsToFloat(data[o + 1]);
		v[2] = Float.intBitsToFloat(data[o + 2]);
		int color = data[o + 3];
		v[3] = (color & 0xFF) / 255.0F;
		v[4] = ((color >> 8) & 0xFF) / 255.0F;
		v[5] = ((color >> 16) & 0xFF) / 255.0F;
		v[6] = ((color >> 24) & 0xFF) / 255.0F;
		v[7] = Float.intBitsToFloat(data[o + 4]);
		v[8] = Float.intBitsToFloat(data[o + 5]);
		int packedNormal = data[o + 7];
		v[9] = ((byte) (packedNormal & 0xFF)) / 127.0F;
		v[10] = ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0F;
		v[11] = ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0F;
		return v;
	}

	private static void put(VertexConsumer consumer, PoseStack.Pose pose, float[] v,
			int packedLight, int packedOverlay) {
		Vector3f normal = new Vector3f(v[9], v[10], v[11]);
		pose.normal().transform(normal);
		if (normal.lengthSquared() > 1.0e-6F) {
			normal.normalize();
		} else {
			normal.set(0.0F, 1.0F, 0.0F);
		}
		consumer.vertex(pose.pose(), v[0], v[1], v[2])
				.color(v[3], v[4], v[5], v[6] <= 0.0F ? 1.0F : v[6])
				.uv(v[7], v[8])
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(normal.x, normal.y, normal.z)
				.endVertex();
	}
}
