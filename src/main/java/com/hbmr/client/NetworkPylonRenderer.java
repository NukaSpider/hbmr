package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.network.NetworkPylonKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * Matches 1.7.10 {@code RenderPylon} / {@code RenderPylonMedium} / {@code RenderPylonLarge}
 * / {@code RenderConnector*} / {@code RenderSubstation}: center-origin OBJs, direct texture
 * bind (no block-atlas UV remap — network textures are non-PoT).
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NetworkPylonRenderer {
	private static final Map<ResourceLocation, WavefrontObjModel> BY_MODEL = new HashMap<>();

	private NetworkPylonRenderer() {
	}

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) manager -> BY_MODEL.clear());
	}

	private static WavefrontObjModel model(NetworkPylonKind kind) {
		return BY_MODEL.computeIfAbsent(kind.model(), loc -> WavefrontObjModel.load(loc, true));
	}

	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			NetworkPylonKind kind, Direction facing) {
		poseStack.pushPose();
		if (kind.isConnector()) {
			// Match 1.7.10 RenderConnector*: center → face rot → drop to floor origin.
			poseStack.translate(0.5D, 0.5D, 0.5D);
			applyFacing(poseStack, kind, facing);
			poseStack.translate(0.0D, -0.5D, 0.0D);
		} else {
			poseStack.translate(0.5D, 0.0D, 0.5D);
			applyFacing(poseStack, kind, facing);
		}
		draw(poseStack, buffer, packedLight, packedOverlay, kind);
		poseStack.popPose();
	}

	public static void renderInventory(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, NetworkPylonKind kind) {
		renderInventory(poseStack, buffer, packedLight, packedOverlay, kind, true);
	}

	public static void renderInventory(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, NetworkPylonKind kind, boolean gui) {
		poseStack.pushPose();
		float extent = guiExtent(kind);
		float s = 0.9F / extent;
		if (!gui) {
			s *= 1.35F;
		}
		if (kind == NetworkPylonKind.MEDIUM_WOOD
				|| kind == NetworkPylonKind.MEDIUM_WOOD_TRANSFORMER
				|| kind == NetworkPylonKind.MEDIUM_STEEL
				|| kind == NetworkPylonKind.MEDIUM_STEEL_TRANSFORMER) {
			// Face the crossarm toward the camera without the old arm offset that shoved it out of slot.
			poseStack.mulPose(Axis.YP.rotationDegrees(gui ? 0.0F : 90.0F));
		}
		poseStack.scale(s, s, s);
		// Center tall meshes on the item origin (feet at y=0 in the OBJ).
		poseStack.translate(0.0D, -extent * 0.5D, 0.0D);
		// GUI isometric still reads a touch low — nudge up in model units after scale.
		if (gui) {
			poseStack.translate(0.0D, extent * 0.08D, 0.0D);
		}
		draw(poseStack, buffer, packedLight, packedOverlay, kind);
		poseStack.popPose();
	}

	/** Largest axis of the inventory mesh in block units. */
	private static float guiExtent(NetworkPylonKind kind) {
		return switch (kind) {
			case CONNECTOR -> 1.0F;
			case CONNECTOR_SUPER -> 1.15F;
			case PYLON -> 5.8F;
			case MEDIUM_WOOD, MEDIUM_WOOD_TRANSFORMER, MEDIUM_STEEL, MEDIUM_STEEL_TRANSFORMER -> 7.8F;
			case LARGE -> 14.5F;
			case SUBSTATION -> 5.5F;
		};
	}

	private static void applyFacing(PoseStack poseStack, NetworkPylonKind kind, Direction facing) {
		if (kind.isConnector()) {
			// ForgeDirection meta rotations from RenderConnector / RenderConnectorSuper.
			switch (facing) {
				case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
				case UP -> {
				}
				case NORTH -> {
					poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
					poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
				}
				case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
				case WEST -> {
					poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
					poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
				}
				case EAST -> {
					poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
					poseStack.mulPose(Axis.ZP.rotationDegrees(270.0F));
				}
			}
			return;
		}
		if (kind == NetworkPylonKind.PYLON) {
			return;
		}
		float yRot;
		if (kind == NetworkPylonKind.SUBSTATION) {
			// 1.7.10 RenderSubstation: meta 2/3 (N/S) → 90°, meta 4/5 (W/E) → 0°.
			yRot = switch (facing) {
				case NORTH, SOUTH -> 90.0F;
				default -> 0.0F;
			};
		} else if (kind == NetworkPylonKind.LARGE) {
			// Mesh is authored axis-aligned; +90° from generic pole yaw so placement faces correctly.
			yRot = switch (facing) {
				case NORTH -> 270.0F;
				case WEST -> 0.0F;
				case EAST -> 180.0F;
				default -> 90.0F; // SOUTH
			};
		} else {
			yRot = switch (facing) {
				case NORTH -> 180.0F;
				case WEST -> 270.0F;
				case EAST -> 90.0F;
				default -> 0.0F;
			};
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
	}

	private static void draw(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay,
			NetworkPylonKind kind) {
		WavefrontObjModel m = model(kind);
		// Connectors ship Blender double-sided shells (same verts, opposite normals). NoCull draws both
		// and Z-fights on the orange tip — 1.7 used GL cull so only the outward faces showed.
		RenderType type = kind.isConnector()
				? RenderType.entityCutout(kind.texture())
				: RenderType.entityCutoutNoCull(kind.texture());
		VertexConsumer vc = buffer.getBuffer(type);
		// Connector textures are integer-upscaled then padded to PoT; remap UVs into the content region.
		float uScale = 1f;
		float vScale = 1f;
		if (kind == NetworkPylonKind.CONNECTOR) {
			uScale = 0.75f;
			vScale = 0.625f;
		} else if (kind == NetworkPylonKind.CONNECTOR_SUPER) {
			uScale = 0.5625f;
			vScale = 0.9375f;
		}
		String part = kind.primaryPart();
		if (part == null) {
			m.renderAll(poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay, uScale, vScale, 0f, 0f);
		} else {
			m.renderPart(part, poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay, uScale, vScale, 0f, 0f);
			if (kind.hasTransformer()) {
				m.renderPart("Transformer", poseStack, vc, 1f, 1f, 1f, 1f, packedLight, packedOverlay, uScale, vScale, 0f, 0f);
			}
		}
	}
}
