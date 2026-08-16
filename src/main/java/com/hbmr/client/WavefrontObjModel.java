package com.hbmr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal Wavefront OBJ loader for TESR-style part rendering (matches 1.7.10
 * {@code HFRWavefrontObject} UV/normal behaviour for machine models).
 * <p>
 * Bakes into triangle lists and draws with a direct texture {@link RenderType}
 * so UVs are not remapped through the block atlas (avoids mipmap moiré on
 * non-PoT machine textures).
 */
public final class WavefrontObjModel {
	private final Map<String, List<Tri>> parts = new HashMap<>();
	private final boolean flipV;

	private WavefrontObjModel(boolean flipV) {
		this.flipV = flipV;
	}

	public static WavefrontObjModel load(ResourceLocation modelLoc, boolean flipV) {
		WavefrontObjModel model = new WavefrontObjModel(flipV);
		Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(modelLoc);
		if (res.isEmpty()) {
			return model;
		}
		List<float[]> positions = new ArrayList<>();
		List<float[]> texCoords = new ArrayList<>();
		List<float[]> normals = new ArrayList<>();
		String current = "Default";
		model.parts.put(current, new ArrayList<>());

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(res.get().open(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] t = line.split("\\s+");
				switch (t[0]) {
					case "v" -> positions.add(new float[]{
							Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3])});
					case "vt" -> texCoords.add(new float[]{
							Float.parseFloat(t[1]), Float.parseFloat(t[2])});
					case "vn" -> normals.add(new float[]{
							Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3])});
					case "o", "g" -> {
						current = t.length > 1 ? t[1] : "Default";
						model.parts.computeIfAbsent(current, k -> new ArrayList<>());
					}
					case "f" -> {
						if (t.length < 4) {
							break;
						}
						Vert[] verts = new Vert[t.length - 1];
						for (int i = 1; i < t.length; i++) {
							verts[i - 1] = parseVert(t[i], positions, texCoords, normals, flipV);
						}
						// Fan triangulation (OBJ is already tris for radgen).
						for (int i = 1; i + 1 < verts.length; i++) {
							model.parts.get(current).add(new Tri(verts[0], verts[i], verts[i + 1]));
						}
					}
					default -> {
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load OBJ " + modelLoc, e);
		}
		return model;
	}

	private static Vert parseVert(String token, List<float[]> positions, List<float[]> texCoords,
			List<float[]> normals, boolean flipV) {
		String[] idx = token.split("/");
		float[] p = positions.get(Integer.parseInt(idx[0]) - 1);
		float u = 0f;
		float v = 0f;
		if (idx.length > 1 && !idx[1].isEmpty()) {
			float[] tc = texCoords.get(Integer.parseInt(idx[1]) - 1);
			u = tc[0];
			v = flipV ? 1f - tc[1] : tc[1];
		}
		float nx = 0f;
		float ny = 1f;
		float nz = 0f;
		if (idx.length > 2 && !idx[2].isEmpty()) {
			float[] n = normals.get(Integer.parseInt(idx[2]) - 1);
			nx = n[0];
			ny = n[1];
			nz = n[2];
		}
		return new Vert(p[0], p[1], p[2], u, v, nx, ny, nz);
	}

	public void renderAll(PoseStack poseStack, VertexConsumer consumer,
			float r, float g, float b, float a, int packedLight, int packedOverlay) {
		renderAll(poseStack, consumer, r, g, b, a, packedLight, packedOverlay, 1f, 1f, 0f, 0f);
	}

	public void renderAll(PoseStack poseStack, VertexConsumer consumer,
			float r, float g, float b, float a, int packedLight, int packedOverlay,
			float uScale, float vScale, float uOff, float vOff) {
		for (String name : parts.keySet()) {
			renderPart(name, poseStack, consumer, r, g, b, a, packedLight, packedOverlay, uScale, vScale, uOff, vOff);
		}
	}

	public void renderPart(String name, PoseStack poseStack, VertexConsumer consumer,
			float r, float g, float b, float a, int packedLight, int packedOverlay) {
		renderPart(name, poseStack, consumer, r, g, b, a, packedLight, packedOverlay, 1f, 1f, 0f, 0f);
	}

	/** Like {@link #renderPart} with a texture-matrix style UV transform (scale then translate). */
	public void renderPart(String name, PoseStack poseStack, VertexConsumer consumer,
			float r, float g, float b, float a, int packedLight, int packedOverlay,
			float uScale, float vScale, float uOff, float vOff) {
		List<Tri> tris = parts.get(name);
		if (tris == null || tris.isEmpty()) {
			return;
		}
		Matrix4f pose = poseStack.last().pose();
		Matrix3f normalMat = poseStack.last().normal();
		Vector3f n = new Vector3f();
		for (Tri tri : tris) {
			emit(consumer, pose, normalMat, n, tri.a, r, g, b, a, packedLight, packedOverlay, uScale, vScale, uOff, vOff);
			emit(consumer, pose, normalMat, n, tri.b, r, g, b, a, packedLight, packedOverlay, uScale, vScale, uOff, vOff);
			emit(consumer, pose, normalMat, n, tri.c, r, g, b, a, packedLight, packedOverlay, uScale, vScale, uOff, vOff);
			// Entity render types expect quads — duplicate last vertex as degenerate 4th.
			emit(consumer, pose, normalMat, n, tri.c, r, g, b, a, packedLight, packedOverlay, uScale, vScale, uOff, vOff);
		}
	}

	private static void emit(VertexConsumer consumer, Matrix4f pose, Matrix3f normalMat, Vector3f n,
			Vert vert, float r, float g, float b, float a, int packedLight, int packedOverlay,
			float uScale, float vScale, float uOff, float vOff) {
		n.set(vert.nx, vert.ny, vert.nz);
		normalMat.transform(n);
		if (n.lengthSquared() > 1.0E-6F) {
			n.normalize();
		}
		consumer.vertex(pose, vert.x, vert.y, vert.z)
				.color(r, g, b, a)
				.uv(vert.u * uScale + uOff, vert.v * vScale + vOff)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(n.x, n.y, n.z)
				.endVertex();
	}

	private record Vert(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
	}

	private record Tri(Vert a, Vert b, Vert c) {
	}
}
