package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.network.FluidDuctMaterial;
import com.hbmr.block.network.FluidNetworkBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Matches 1.7.10 {@code RenderTestPipe}: {@code pipe_neo.obj} with base + tinted overlay.
 * Fluid tint defaults to NONE ({@code 0x888888}) until pipe fluid types are ported.
 */
@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PipeNeoRenderer {
	private static final ResourceLocation MODEL =
			ResourceLocation.fromNamespaceAndPath("hbmr", "models/block/network/pipe_neo.obj");
	/** 1.7.10 {@code Fluids.NONE} color used for unused/empty pipe overlays. */
	private static final int NONE_FLUID_COLOR = 0x888888;

	private static WavefrontObjModel model;

	private PipeNeoRenderer() {
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

	public static ResourceLocation baseTex(FluidDuctMaterial material) {
		return switch (material) {
			case NEO -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_neo.png");
			case SILVER -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_silver.png");
			case COLORED -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_colored.png");
			case PNEUMATIC -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pneumatic_tube.png");
		};
	}

	public static ResourceLocation overlayTex(FluidDuctMaterial material) {
		return switch (material) {
			case NEO -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_neo_overlay.png");
			case SILVER -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_silver_overlay.png");
			case COLORED -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pipe_colored_overlay.png");
			case PNEUMATIC -> ResourceLocation.fromNamespaceAndPath("hbmr", "textures/block/pneumatic_tube.png");
		};
	}

	/** Inventory: four horizontal stubs (1.7.10 {@code renderInventoryBlock}). */
	public static void renderInventory(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, FluidDuctMaterial material) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(180));
		poseStack.scale(1.25F, 1.25F, 1.25F);
		renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pX");
		renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nX");
		renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pZ");
		renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nZ");
		poseStack.popPose();
	}

	/**
	 * World: center-origin parts per connection mask
	 * (same rules as 1.7.10 {@code RenderTestPipe#renderWorldBlock}, including Z swap).
	 */
	public static void renderWorld(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state) {
		if (!(state.getBlock() instanceof FluidNetworkBlock duct)) {
			return;
		}
		FluidDuctMaterial material = duct.getMaterial();

		boolean pX = state.getValue(FluidNetworkBlock.EAST);
		boolean nX = state.getValue(FluidNetworkBlock.WEST);
		boolean pY = state.getValue(FluidNetworkBlock.UP);
		boolean nY = state.getValue(FluidNetworkBlock.DOWN);
		boolean pZ = state.getValue(FluidNetworkBlock.SOUTH);
		boolean nZ = state.getValue(FluidNetworkBlock.NORTH);

		int mask = (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);

		if (mask == 0) {
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pX");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nX");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pY");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nY");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pZ");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nZ");
		} else if (mask == 0b100000 || mask == 0b010000) {
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pX");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nX");
		} else if (mask == 0b001000 || mask == 0b000100) {
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pY");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nY");
		} else if (mask == 0b000010 || mask == 0b000001) {
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pZ");
			renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nZ");
		} else {
			if (pX) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pX");
			}
			if (nX) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nX");
			}
			if (pY) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pY");
			}
			if (nY) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nY");
			}
			// 1.7.10 swaps Z part names relative to world +Z/−Z.
			if (pZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nZ");
			}
			if (nZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pZ");
			}

			if (!pX && !pY && !pZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "ppn");
			}
			if (!pX && !pY && !nZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "ppp");
			}
			if (!nX && !pY && !pZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "npn");
			}
			if (!nX && !pY && !nZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "npp");
			}
			if (!pX && !nY && !pZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pnn");
			}
			if (!pX && !nY && !nZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "pnp");
			}
			if (!nX && !nY && !pZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nnn");
			}
			if (!nX && !nY && !nZ) {
				renderDuct(poseStack, buffer, packedLight, packedOverlay, material, "nnp");
			}
		}

		poseStack.popPose();
	}

	private static void renderDuct(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, FluidDuctMaterial material, String part) {
		WavefrontObjModel m = model();
		float r = ((NONE_FLUID_COLOR >> 16) & 0xFF) / 255f;
		float g = ((NONE_FLUID_COLOR >> 8) & 0xFF) / 255f;
		float b = (NONE_FLUID_COLOR & 0xFF) / 255f;

		VertexConsumer base = buffer.getBuffer(RenderType.entityCutoutNoCull(baseTex(material)));
		m.renderPart(part, poseStack, base, 1f, 1f, 1f, 1f, packedLight, packedOverlay);

		VertexConsumer overlay = buffer.getBuffer(RenderType.entityCutoutNoCull(overlayTex(material)));
		m.renderPart(part, poseStack, overlay, r, g, b, 1f, packedLight, packedOverlay);
	}
}
