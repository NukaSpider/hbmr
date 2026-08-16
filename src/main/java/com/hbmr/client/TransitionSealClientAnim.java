package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.multiblock.TransitionSealAnim;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Client-side Collada animation for the transition seal (matches 1.7.10 {@code ColladaLoader} + {@code Transform}).
 */
public final class TransitionSealClientAnim {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ResourceLocation ANIM_LOC =
			ResourceLocation.fromNamespaceAndPath(HBMR.MODID, "models/block/multiblock/transition_seal_anim.bin");

	private static boolean loaded;
	private static final List<Part> PARTS = new ArrayList<>();
	private static int numKeyFrames = 1;

	public record Part(String name, ResourceLocation model, float[][] keyframes, boolean[] hidden, float[] bind,
			boolean animated) {
	}

	private TransitionSealClientAnim() {
	}

	public static void invalidate() {
		loaded = false;
		PARTS.clear();
		numKeyFrames = 1;
	}

	public static List<Part> parts() {
		ensureLoaded();
		return PARTS;
	}

	public static int numKeyFrames() {
		ensureLoaded();
		return numKeyFrames;
	}

	/**
	 * Map openTicks 0…{@link TransitionSealAnim#TIME_TO_OPEN} onto Collada keyframes and write a
	 * column-major matrix for {@link com.mojang.blaze3d.vertex.PoseStack#mulPoseMatrix}.
	 *
	 * @return true if the part should render (not hidden)
	 */
	public static boolean matrixFor(Part part, float openTicks, Matrix4f dest) {
		ensureLoaded();
		if (!part.animated) {
			rowMajorToMatrix(part.bind, dest);
			return true;
		}
		float t = TransitionSealAnim.normTime(openTicks);
		float remapped = t * (numKeyFrames - 1);
		int first = Math.min(numKeyFrames - 1, Math.max(0, (int) remapped));
		int next = Math.min(numKeyFrames - 1, first + 1);
		float inter = remapped - first;
		if (part.hidden[first]) {
			return false;
		}
		interpolate(part.keyframes[first], part.keyframes[next], inter, dest);
		return true;
	}

	private static void ensureLoaded() {
		if (loaded) {
			return;
		}
		loaded = true;
		try {
			load();
		} catch (Exception e) {
			LOGGER.error("Failed to load transition seal animation", e);
		}
	}

	private static void load() throws IOException {
		Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(ANIM_LOC);
		if (res.isEmpty()) {
			LOGGER.error("Missing {}", ANIM_LOC);
			return;
		}
		try (InputStream in = res.get().open(); DataInputStream data = new DataInputStream(in)) {
			byte[] magic = data.readNBytes(4);
			if (magic.length != 4 || magic[0] != 'H' || magic[1] != 'B' || magic[2] != 'M' || magic[3] != 'S') {
				throw new IOException("Bad anim magic");
			}
			int lengthMs = Integer.reverseBytes(data.readInt());
			numKeyFrames = Integer.reverseBytes(data.readInt());
			int numParts = Integer.reverseBytes(data.readInt());
			if (lengthMs != TransitionSealAnim.ANIM_LENGTH_MS) {
				LOGGER.warn("Anim length {} != expected {}", lengthMs, TransitionSealAnim.ANIM_LENGTH_MS);
			}
			PARTS.clear();
			for (int p = 0; p < numParts; p++) {
				int nameLen = Short.reverseBytes(data.readShort()) & 0xFFFF;
				byte[] nameBytes = data.readNBytes(nameLen);
				String name = new String(nameBytes);
				String safe = name.replace('.', '_').replace(' ', '_').toLowerCase(java.util.Locale.ROOT);
				ResourceLocation model = ResourceLocation.fromNamespaceAndPath(HBMR.MODID,
						"block/multiblock/transition_seal_" + safe + "_ber");
				int animated = data.readUnsignedByte();
				if (animated != 0) {
					float[][] kfs = new float[numKeyFrames][16];
					boolean[] hid = new boolean[numKeyFrames];
					for (int i = 0; i < numKeyFrames; i++) {
						for (int j = 0; j < 16; j++) {
							kfs[i][j] = Float.intBitsToFloat(Integer.reverseBytes(data.readInt()));
						}
						hid[i] = data.readUnsignedByte() != 0;
					}
					PARTS.add(new Part(name, model, kfs, hid, null, true));
				} else {
					float[] bind = new float[16];
					for (int j = 0; j < 16; j++) {
						bind[j] = Float.intBitsToFloat(Integer.reverseBytes(data.readInt()));
					}
					data.readUnsignedByte(); // unused hide
					PARTS.add(new Part(name, model, null, null, bind, false));
				}
			}
			LOGGER.info("Loaded transition seal anim: {} parts, {} keyframes", PARTS.size(), numKeyFrames);
		}
	}

	/** Collada row-major → JOML column-major. */
	private static void rowMajorToMatrix(float[] row, Matrix4f dest) {
		dest.set(
				row[0], row[4], row[8], row[12],
				row[1], row[5], row[9], row[13],
				row[2], row[6], row[10], row[14],
				row[3], row[7], row[11], row[15]);
	}

	/**
	 * Matches 1.7.10 {@code Transform#interpolateAndApply} output (column-major for GL MultMatrix).
	 */
	private static void interpolate(float[] aRow, float[] bRow, float inter, Matrix4f dest) {
		Decomp a = Decomp.fromRowMajor(aRow);
		Decomp b = Decomp.fromRowMajor(bRow);
		Vector3f trans = new Vector3f(a.tx, a.ty, a.tz).lerp(new Vector3f(b.tx, b.ty, b.tz), inter);
		Vector3f scale = new Vector3f(a.sx, a.sy, a.sz).lerp(new Vector3f(b.sx, b.sy, b.sz), inter);
		Quaternionf rot = slerp(a.rot, b.rot, inter);
		dest.identity();
		dest.rotate(rot);
		dest.scale(scale);
		dest.m30(trans.x);
		dest.m31(trans.y);
		dest.m32(trans.z);
	}

	private static Quaternionf slerp(Quaternionf v0, Quaternionf v1, float t) {
		Quaternionf a = new Quaternionf(v0);
		Quaternionf b = new Quaternionf(v1);
		float dot = a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
		if (dot < 0.0F) {
			b.set(-b.x, -b.y, -b.z, -b.w);
			dot = -dot;
		}
		if (dot > 0.9999999F) {
			return new Quaternionf(
					a.x + t * (b.x - a.x),
					a.y + t * (b.y - a.y),
					a.z + t * (b.z - a.z),
					a.w + t * (b.w - a.w)).normalize();
		}
		double theta0 = Math.acos(dot);
		double theta = theta0 * t;
		double sinTheta = Math.sin(theta);
		double sinTheta0 = Math.sin(theta0);
		float s0 = (float) (Math.cos(theta) - dot * sinTheta / sinTheta0);
		float s1 = (float) (sinTheta / sinTheta0);
		return new Quaternionf(
				s0 * a.x + s1 * b.x,
				s0 * a.y + s1 * b.y,
				s0 * a.z + s1 * b.z,
				s0 * a.w + s1 * b.w);
	}

	private static final class Decomp {
		final float sx, sy, sz, tx, ty, tz;
		final Quaternionf rot;

		Decomp(float sx, float sy, float sz, float tx, float ty, float tz, Quaternionf rot) {
			this.sx = sx;
			this.sy = sy;
			this.sz = sz;
			this.tx = tx;
			this.ty = ty;
			this.tz = tz;
			this.rot = rot;
		}

		static Decomp fromRowMajor(float[] m) {
			// Same indexing as 1.7.10 Transform (row-major Collada).
			float[] c = m.clone();
			float scaleX = len3(c[0], c[1], c[2]);
			float scaleY = len3(c[4], c[5], c[6]);
			float scaleZ = len3(c[8], c[9], c[10]);
			if (scaleX > 1e-8F) {
				c[0] /= scaleX;
				c[1] /= scaleX;
				c[2] /= scaleX;
			}
			if (scaleY > 1e-8F) {
				c[4] /= scaleY;
				c[5] /= scaleY;
				c[6] /= scaleY;
			}
			if (scaleZ > 1e-8F) {
				c[8] /= scaleZ;
				c[9] /= scaleZ;
				c[10] /= scaleZ;
			}
			float tx = c[3];
			float ty = c[7];
			float tz = c[11];
			// Build rotation matrix column-major for Quaternionf.setFromNormalized
			Matrix4f rotMat = new Matrix4f();
			rotMat.set(
					c[0], c[4], c[8], 0,
					c[1], c[5], c[9], 0,
					c[2], c[6], c[10], 0,
					0, 0, 0, 1);
			Quaternionf q = new Quaternionf();
			rotMat.getNormalizedRotation(q);
			return new Decomp(scaleX, scaleY, scaleZ, tx, ty, tz, q);
		}

		private static float len3(float x, float y, float z) {
			return (float) Math.sqrt(x * x + y * y + z * z);
		}
	}
}
