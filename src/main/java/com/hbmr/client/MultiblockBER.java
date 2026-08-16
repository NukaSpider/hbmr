package com.hbmr.client;

import com.hbmr.block.multiblock.MultiblockControllerBlock;
import com.hbmr.block.multiblock.MultiblockControllerBlockEntity;
import com.hbmr.block.multiblock.StructureType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class MultiblockBER implements BlockEntityRenderer<MultiblockControllerBlockEntity> {
	public MultiblockBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(MultiblockControllerBlockEntity be, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		StructureType type = be.getStructureType();
		if (!type.useObjBer()) {
			return;
		}

		BlockState state = be.getBlockState();
		Direction facing = state.hasProperty(MultiblockControllerBlock.FACING)
				? state.getValue(MultiblockControllerBlock.FACING)
				: Direction.NORTH;

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.0D, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(facingToYRot(facing)));

		if (type.isRbmkColumn()) {
			RBMKColumnRenderer.renderWorld(type, poseStack, buffer, packedLight, packedOverlay);
			poseStack.popPose();
			return;
		}

		if (type == StructureType.SLIDING_BLAST_DOOR_SHORT) {
			poseStack.mulPose(Axis.YP.rotationDegrees(180));
			renderShortBlastDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.SLIDING_BLAST_DOOR) {
			renderWideSlidingDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.FIRE_DOOR) {
			renderFireDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.SECURE_ACCESS_DOOR) {
			renderSecureAccessDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.LARGE_VEHICLE_DOOR) {
			renderLargeVehicleDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.QE_CONTAINMENT_DOOR) {
			renderQeContainmentDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.QE_SLIDING_DOOR) {
			renderQeSlidingDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.SLIDING_SEAL_DOOR) {
			renderSlidingSealDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.WATER_DOOR) {
			renderWaterDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.BURNER_PRESS) {
			// 1.7.10 RenderPress ignores facing and always uses +180° Y.
			poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing) + 180.0F));
			renderBurnerPress(poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.ELECTRIC_PRESS) {
			// facingToYRot already matches 1.7.10 RenderEPress totals (N90/S270/W180/E0).
			// Do not add another +180 — that faced the machine away from the player.
			renderElectricPress(poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.ROUND_AIRLOCK_DOOR) {
			renderRoundAirlockDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.VT_BLAST_DOOR) {
			renderVaultDoor(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.SILO_HATCH || type == StructureType.SILO_HATCH_LARGE) {
			renderSiloHatch(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state,
					type == StructureType.SILO_HATCH_LARGE);
		} else if (type == StructureType.TRANSITION_SEAL) {
			renderTransitionSeal(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.CARGO_ELEVATOR) {
			renderCargoElevator(be, partialTick, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.IRON_FURNACE) {
			// 1.7.10 RenderFurnaceIron: meta→yaw then translate -0.5,0,-0.5
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			poseStack.translate(-0.5D, 0.0D, -0.5D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.BOILER
				|| type == StructureType.STIRLING_ENGINE
				|| type == StructureType.HEAVY_STIRLING_ENGINE
				|| type == StructureType.CREATIVE_STIRLING_ENGINE
				|| type == StructureType.STIRLING_SAWMILL) {
			// Same facing family as iron, but centered on the multiblock footprint
			// (iron's -0.5,-0.5 left these 0.5 left / 0.5 back of the hitbox).
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.STEEL_FURNACE) {
			// 1.7.10 RenderFurnaceSteel: meta yaw then −90 Y ≡ facingToYRot; no extra translate.
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.STRAND_CASTER) {
			// Match 1.7.10 RenderStrandCaster exactly (undo shared facingToYRot first):
			// center → meta yaw → translate(0.5,0,0.5) → +180 Y.
			poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing)));
			float yaw = switch (facing) {
				case NORTH -> 0.0F;
				case SOUTH -> 180.0F;
				case WEST -> 90.0F;
				case EAST -> 270.0F;
				default -> 0.0F;
			};
			poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
			poseStack.translate(0.5D, 0.0D, 0.5D);
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.COMBINATION_OVEN || type == StructureType.INDUSTRIAL_BOILER) {
			// No facing rotation in 1.7.10 TESR
			poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing)));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.GAS_CENTRIFUGE) {
			// facingToYRot matches centrifuge meta map, then +180 Y (RenderCentrifuge gas path).
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.FEL) {
			// FEL: N0/W90/S180/E270 ≡ facingToYRot − 90.
			poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.WOOD_BURNING_GENERATOR) {
			// RenderWoodBurner: N180/W270/S0/E90 then translate(-0.5,0,-0.5).
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			poseStack.translate(-0.5D, 0.0D, -0.5D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.INDUSTRIAL_COMBUSTION_ENGINE) {
			// After facing: translate(-0.5, 0, 3) in local space.
			poseStack.translate(-0.5D, 0.0D, 3.0D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.RESEARCH_REACTOR) {
			// RenderSmallReactor ignores facing; always +180 Y.
			poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing) + 180.0F));
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.INDUSTRIAL_GENERATOR) {
			// Decorative 1×1: IGen yaw (facingToYRot+90) then scale to fit one block.
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			poseStack.scale(0.18F, 0.18F, 0.18F);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.RADIATION_POWERED_ENGINE) {
			// Match 1.7.10 RenderRadGen (facingToYRot already applied).
			renderRadGen(poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.GEOTHERMAL_HEAT_EXCHANGER) {
			// Non-PoT texture — direct Wavefront path like radgen / RenderHephaestus.
			HephaestusRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.CYCLOTRON) {
			// Non-PoT body/plugs — atlas bake remaps UVs into a scrambled look.
			CyclotronRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (BarrelRenderer.isBarrel(type)) {
			BarrelRenderer.render(poseStack, buffer, packedLight, packedOverlay, type);
		} else if (type == StructureType.BATTERY_SOCKET) {
			// Non-PoT 74×72 — Socket + Supports only (packs are inventory contents).
			BatterySocketRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.FENSU) {
			// fensu2 / RenderBatteryREDD yaw is facingToYRot + 180 (not old RenderFENSU).
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			FensuRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.OLD_CAPACITOR) {
			CapacitorRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.MICROWAVE) {
			// 1.7.10 RenderMicrowave: facingToYRot−90, y−0.785, then (−0.5, 0, 0.65).
			poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
			poseStack.translate(-0.5D, -0.785D, 0.65D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.CHEMICAL_FACTORY) {
			// forge:obj bake fails on v//n faces — direct Wavefront path.
			ChemicalFactoryRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.PARTICLE_SOURCE
				|| type == StructureType.RF_CAVITY
				|| type == StructureType.QUADRUPOLE_MAGNETS
				|| type == StructureType.DIPOLE_MAGNETS) {
			// Core sits at heightOffset 1; 1.7.10 TESR uses y - 1 so the feet sit on the ground.
			if (type == StructureType.DIPOLE_MAGNETS) {
				// RenderPADipole ignores facing.
				poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing)));
			}
			poseStack.translate(0.0D, -1.0D, 0.0D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.PARTICLE_DETECTOR) {
			// RenderPADetector: y - 2 with facing.
			poseStack.translate(0.0D, -2.0D, 0.0D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.RBMK_CONSOLE) {
			// 1.7.10 RenderRBMKConsole: +0.5 X after facing.
			poseStack.translate(0.5D, 0.0D, 0.0D);
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
		} else if (type == StructureType.RBMK_CRANE_CONSOLE) {
			// 1.7.10 RenderCraneConsole: +0.5 X (console + shotgun/mininuke desk props).
			poseStack.translate(0.5D, 0.0D, 0.0D);
			CraneConsoleRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.RBMK_AUTOLOADER) {
			// Non-PoT texture — direct Wavefront path (atlas crush flattened shaft vents).
			AutoloaderRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.CONVEYOR_SPLITTER) {
			long time = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
			ConveyorSplitterRenderer.renderWorld(poseStack, buffer, packedLight, packedOverlay, facing,
					facingToYRot(facing), time);
		} else if (type == StructureType.RBMK_DEBRIS_FLAMING
				|| type == StructureType.RBMK_DEBRIS_SMOLDERING) {
			renderSingleMachine(type, poseStack, buffer, packedLight, packedOverlay, state);
			// Flames face the camera in world space (undo block facing).
			poseStack.mulPose(Axis.YP.rotationDegrees(-facingToYRot(facing)));
			long time = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
			RBMKFlameRenderer.render(poseStack, buffer, time, partialTick, be.getBlockPos());
		} else if (type == StructureType.EXPOSURE_CHAMBER) {
			// 1.7.10 disables GL cull so the far chamber wall is visible through the shell.
			ResourceLocation modelLoc = type.getBerModelLocation();
			if (modelLoc != null) {
				renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc,
						RenderMode.CUTOUT_NO_CULL);
			}
		} else {
			// HEAT_EXCHANGING_HEATER / CRUCIBLE / centrifuge-family / blast furnace / etc.
			// match facingToYRot; other single-model types too
			ResourceLocation modelLoc = type.getBerModelLocation();
			if (modelLoc != null) {
				boolean cutout = true;
				renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc, cutout);
			}
		}
		poseStack.popPose();
	}

	private static void renderSingleMachine(StructureType type, PoseStack poseStack, MultiBufferSource buffer,
			int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation modelLoc = type.getBerModelLocation();
		if (modelLoc != null) {
			renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc, true);
		}
	}

	/**
	 * Matches 1.7.10 {@code RenderRadGen} via {@link RadGenRenderer} (direct texture,
	 * smooth normals — not Forge baked atlas models).
	 */
	private static void renderRadGen(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state) {
		RadGenRenderer.render(poseStack, buffer, packedLight, packedOverlay);
	}

	/**
	 * Matches 1.7.10 {@code RenderBlastDoor}: timer 5 = closed, 0 = open.
	 * Moving assembly is translated by {@code -(5 - timer)} relative to the closed pose.
	 */
	private static void renderShortBlastDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.SLIDING_BLAST_DOOR_SHORT.getAllBerModelLocations();
		ResourceLocation base = parts[0];
		ResourceLocation tooth = parts[1];
		ResourceLocation slider = parts[2];
		ResourceLocation block = parts[3];

		double timer = be.getDoorAnimTimer(partialTick);
		double moving = 5.0D - timer; // 0 when closed, 5 when open

		renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, base, 0.0D, false);
		renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, block, 3.0D, false);
		renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, tooth, moving, false);
		if (timer > 1.0D) {
			renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, slider, moving, false);
		}
		if (timer > 2.0D) {
			renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, slider, moving + 1.0D, false);
		}
		if (timer > 3.0D) {
			renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, slider, moving + 2.0D, false);
		}
		if (timer > 4.0D) {
			renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, slider, moving + 3.0D, false);
		}
	}

	/**
	 * Matches 1.7.10 {@code RenderSlidingBlastDoor}: Frame fixed; doors slide on Z; locks rotate on X.
	 * Cutout so window-pane alpha in the texture is see-through.
	 */
	private static void renderWideSlidingDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.SLIDING_BLAST_DOOR.getAllBerModelLocations();
		ResourceLocation frame = parts[0];
		ResourceLocation leftDoor = parts[1];
		ResourceLocation rightDoor = parts[2];
		ResourceLocation leftLock = parts[3];
		ResourceLocation rightLock = parts[4];

		double open = be.getWideDoorSlide(partialTick);
		double lock = be.getWideDoorLockDegrees(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);

		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, open);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, leftDoor, true);
		poseStack.pushPose();
		poseStack.translate(0.0D, 1.8125D, 0.0D);
		poseStack.mulPose(Axis.XP.rotationDegrees((float) (90.0D + lock)));
		poseStack.translate(0.0D, -1.8125D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, rightLock, true);
		poseStack.popPose();
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, -open);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, rightDoor, true);
		poseStack.pushPose();
		poseStack.translate(0.0D, 1.8125D, 0.0D);
		poseStack.mulPose(Axis.XP.rotationDegrees((float) (90.0D + lock)));
		poseStack.translate(0.0D, -1.8125D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, leftLock, true);
		poseStack.popPose();
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderFireDoor}: +90° yaw, −0.5 X; Door raises on Y up to 2.75.
	 */
	private static void renderFireDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.fireFrame(skin);
		ResourceLocation door = com.hbmr.block.multiblock.DoorSkins.fireDoor(skin);

		poseStack.mulPose(Axis.YP.rotationDegrees(90));
		poseStack.translate(-0.5D, 0.0D, 0.0D);

		double raise = be.getFireDoorRaise(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, false);
		poseStack.pushPose();
		poseStack.translate(0.0D, raise, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, door, false);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderSecureDoor}: +1 Y base (OBJ yMin is -1); Frame fixed; Door raises up to 3.5.
	 */
	private static void renderSecureAccessDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.secureFrame(skin);
		ResourceLocation door = com.hbmr.block.multiblock.DoorSkins.secureDoor(skin);

		poseStack.translate(0.0D, 1.0D, 0.0D);
		double raise = be.getSecureDoorRaise(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);
		poseStack.pushPose();
		poseStack.translate(0.0D, raise, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, door, true);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderVehicleDoor}: +90° Y; Frame fixed; Left/Right slide on X,
	 * clipped to {@code |x| <= 3.4375} so open leaves hide in the frame (image-2 look).
	 */
	private static void renderLargeVehicleDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.LARGE_VEHICLE_DOOR.getAllBerModelLocations();
		ResourceLocation frame = parts[0];
		ResourceLocation left = parts[1];
		ResourceLocation right = parts[2];

		poseStack.mulPose(Axis.YP.rotationDegrees(90));
		float open = (float) be.getVehicleDoorSlide(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);
		// Clip |x|<=3.4375 like 1.7.10 so open leaves vanish into the frame (image 2).
		renderVehicleLeaf(poseStack, buffer, packedLight, packedOverlay, state, left, -open);
		renderVehicleLeaf(poseStack, buffer, packedLight, packedOverlay, state, right, open);
	}

	private static final float VEHICLE_DOOR_CLIP = 3.4375F;

	private static void renderVehicleLeaf(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, float slideX) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(slideX, 0.0D, 0.0D);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		ModelXClipRenderer.render(poseStack, consumer, state, model, packedLight, packedOverlay,
				slideX, VEHICLE_DOOR_CLIP);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderContainmentDoor}: +0.25 X; Frame fixed; Door raises up to 2.25,
	 * clipped to {@code y <= 3} so the panel vanishes into the frame when open.
	 */
	private static void renderQeContainmentDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.containmentFrame(skin);
		ResourceLocation door = com.hbmr.block.multiblock.DoorSkins.containmentDoor(skin);

		poseStack.translate(0.25D, 0.0D, 0.0D);
		float raise = (float) be.getContainmentDoorRaise(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);
		renderContainmentDoorLeaf(poseStack, buffer, packedLight, packedOverlay, state, door, raise);
	}

	/**
	 * Matches 1.7.10 {@code RenderSlidingDoor}: +0.53125 X / +0.001 Y / +0.5 Z; Frame fixed;
	 * Left/Right slide on ±Z up to 0.95. Uses translucent so glass/alpha textures show correctly.
	 */
	private static void renderQeSlidingDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.QE_SLIDING_DOOR.getAllBerModelLocations();
		ResourceLocation frame = parts[0];
		ResourceLocation left = parts[1];
		ResourceLocation right = parts[2];

		poseStack.translate(0.53125D, 0.001D, 0.5D);
		double open = be.getQeSlidingDoorSlide(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, RenderMode.TRANSLUCENT);
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, open);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, left, RenderMode.TRANSLUCENT);
		poseStack.popPose();
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, -open);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, right, RenderMode.TRANSLUCENT);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderSealDoor}: +0.5 X; Frame fixed; Door slides on +Z (smoothstep×0.9),
	 * clipped to {@code z <= 0.5001}. Translucent like the QE sliding door.
	 */
	private static void renderSlidingSealDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.SLIDING_SEAL_DOOR.getAllBerModelLocations();
		ResourceLocation frame = parts[0];
		ResourceLocation door = parts[1];

		poseStack.translate(0.5D, 0.0D, 0.0D);
		float slide = (float) be.getSlidingSealDoorSlide(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, RenderMode.TRANSLUCENT);
		renderSealDoorLeaf(poseStack, buffer, packedLight, packedOverlay, state, door, slide);
	}

	private static final float SEAL_DOOR_CLIP_Z = 0.5001F;

	private static void renderSealDoorLeaf(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, float slideZ) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, slideZ);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
		ModelXClipRenderer.renderZMax(poseStack, consumer, state, model, packedLight, packedOverlay,
				slideZ, SEAL_DOOR_CLIP_Z);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderWaterDoor}: +0.375 X / +90° Y; Frame fixed; Door swings on Y;
	 * Bolts retract on −X; Top/Bottom spin with bolt.
	 */
	private static void renderWaterDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.waterFrame(skin);
		ResourceLocation door = com.hbmr.block.multiblock.DoorSkins.waterDoor(skin);
		ResourceLocation bolts = com.hbmr.block.multiblock.DoorSkins.waterBolts(skin);
		ResourceLocation top = com.hbmr.block.multiblock.DoorSkins.waterTop(skin);
		ResourceLocation bottom = com.hbmr.block.multiblock.DoorSkins.waterBottom(skin);

		double rot = be.getWaterDoorDegrees(partialTick);
		float bolt = (float) be.getWaterDoorBolt(partialTick);

		poseStack.translate(0.375D, 0.0D, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(90));
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);

		poseStack.pushPose();
		poseStack.translate(-1.1875D, 0.0D, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees((float) -rot));
		poseStack.translate(1.1875D, 0.0D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, door, true);

		poseStack.pushPose();
		poseStack.translate(-0.4D * bolt, 0.0D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, bolts, true);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.40625D, 2.28125D, 0.0D);
		poseStack.mulPose(Axis.ZP.rotationDegrees(bolt * 360.0F));
		poseStack.translate(-0.40625D, -2.28125D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, top, true);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.40625D, 0.71875D, 0.0D);
		poseStack.mulPose(Axis.ZP.rotationDegrees(bolt * 360.0F));
		poseStack.translate(-0.40625D, -0.71875D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, bottom, true);
		poseStack.popPose();

		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderAirlockDoor}: +0.5 Z; Frame fixed; Left/Right slide on ±Z up to 1.5,
	 * clipped to {@code |z| <= 1.999} so open leaves vanish into the frame.
	 */
	private static void renderRoundAirlockDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.airlockFrame(skin);
		ResourceLocation left = com.hbmr.block.multiblock.DoorSkins.airlockLeft(skin);
		ResourceLocation right = com.hbmr.block.multiblock.DoorSkins.airlockRight(skin);

		poseStack.translate(0.0D, 0.0D, 0.5D);
		float open = (float) be.getAirlockDoorSlide(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, true);
		renderAirlockLeaf(poseStack, buffer, packedLight, packedOverlay, state, left, open);
		renderAirlockLeaf(poseStack, buffer, packedLight, packedOverlay, state, right, -open);
	}

	private static final float AIRLOCK_DOOR_CLIP_Z = 1.999F;

	private static void renderAirlockLeaf(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, float slideZ) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, slideZ);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		ModelXClipRenderer.renderZ(poseStack, consumer, state, model, packedLight, packedOverlay,
				slideZ, AIRLOCK_DOOR_CLIP_Z);
		poseStack.popPose();
	}

	private static final float CONTAINMENT_DOOR_CLIP_Y = 3.0F;

	private static void renderContainmentDoorLeaf(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, float raiseY) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.0D, raiseY, 0.0D);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
		ModelXClipRenderer.renderYMax(poseStack, consumer, state, model, packedLight, packedOverlay,
				raiseY, CONTAINMENT_DOOR_CLIP_Y);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderVaultDoor}: Frame fixed; Door pulls on −X, slides on +Z, rolls on X.
	 */
	private static void renderVaultDoor(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		int skin = be.getSkinIndex();
		ResourceLocation frame = com.hbmr.block.multiblock.DoorSkins.vaultFrame(skin);
		ResourceLocation door = com.hbmr.block.multiblock.DoorSkins.vaultDoor(skin);
		ResourceLocation label = com.hbmr.block.multiblock.DoorSkins.vaultLabel(skin);

		double pull = be.getVaultPull(partialTick);
		double slide = be.getVaultSlide(partialTick);
		double roll = be.getVaultRollDegrees(partialTick);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, false);

		poseStack.pushPose();
		poseStack.translate(-pull, 0.0D, 0.0D);
		poseStack.translate(0.0D, 0.0D, slide);
		poseStack.translate(0.0D, 2.5D, 0.0D);
		poseStack.mulPose(Axis.XP.rotationDegrees((float) roll));
		poseStack.translate(0.0D, -2.5D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, door, false);
		// Tiny depth bias + slight scale about hub so the plate fills the rim without floating.
		poseStack.translate(0.001D, 0.0D, 0.0D);
		poseStack.translate(0.0D, 2.5D, 0.0D);
		poseStack.scale(1.04F, 1.04F, 1.04F);
		poseStack.translate(0.0D, -2.5D, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, label, false);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderDoorGeneric#doPartTransform} for {@code DoorDecl.SILO_HATCH}:
	 * Frame fixed; Hatch lifts then pitches about hinge.
	 */
	private static void renderSiloHatch(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state,
			boolean large) {
		ResourceLocation[] parts = (large ? StructureType.SILO_HATCH_LARGE : StructureType.SILO_HATCH)
				.getAllBerModelLocations();
		ResourceLocation frame = parts[0];
		ResourceLocation hatch = parts[1];

		float lift = be.getSiloHatchLift(partialTick);
		float rot = be.getSiloHatchRotDegrees(partialTick);
		float hingeY = com.hbmr.block.multiblock.SiloHatchAnim.HINGE_Y;
		float hingeZ = com.hbmr.block.multiblock.SiloHatchAnim.hingeZ(large);

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, frame, false);

		poseStack.pushPose();
		poseStack.translate(0.0D, hingeY, hingeZ);
		poseStack.mulPose(Axis.XP.rotationDegrees(rot));
		poseStack.translate(0.0D, -hingeY + lift, -hingeZ);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, hatch, false);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderCargoElevator}: Base fixed; Platform + Pistons travel with
	 * extension; corner guides stacked per layer.
	 */
	private static void renderCargoElevator(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.CARGO_ELEVATOR.getAllBerModelLocations();
		ResourceLocation base = parts[0];
		ResourceLocation platform = parts[1];
		ResourceLocation piston = parts[2];
		// parts[3..6] = guide corners: (-1,-1), (-1,+1), (+1,-1), (+1,+1)
		int[][] corners = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

		BlockPos core = be.getBlockPos();
		var level = be.getLevel();
		int layers = be.getLayerCount();

		boolean anyPole = false;
		for (int i = 0; i < layers && !anyPole; i++) {
			for (int c = 0; c < 4; c++) {
				if (level == null) {
					break;
				}
				BlockPos post = core.offset(corners[c][0], i, corners[c][1]);
				if (!level.getBlockState(post).isAir()) {
					anyPole = true;
					break;
				}
			}
		}

		// No poles → render nothing (including the base plate).
		if (!anyPole) {
			return;
		}

		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, base, true);

		double extension = Math.min(be.getCargoExtension(partialTick), be.getHeight());
		poseStack.pushPose();
		poseStack.translate(0.0D, extension, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, platform, true);
		for (int i = 0; i < extension + 1.0D; i++) {
			renderBaked(poseStack, buffer, packedLight, packedOverlay, state, piston, true);
			poseStack.translate(0.0D, -1.0D, 0.0D);
		}
		poseStack.popPose();

		for (int i = 0; i < layers; i++) {
			poseStack.pushPose();
			poseStack.translate(0.0D, i, 0.0D);
			for (int c = 0; c < 4; c++) {
				boolean present = true;
				if (level != null) {
					BlockPos post = core.offset(corners[c][0], i, corners[c][1]);
					present = !level.getBlockState(post).isAir();
				}
				if (present) {
					renderBaked(poseStack, buffer, packedLight, packedOverlay, state, parts[3 + c], true);
				}
			}
			poseStack.popPose();
		}
	}

	/**
	 * Matches 1.7.10 {@code RenderPress}: body + raised head (idle, not pressing).
	 * Head is scaled 0.99 on X/Z like 1.7.10 to avoid z-fighting the body top knob.
	 */
	private static void renderBurnerPress(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.BURNER_PRESS.getAllBerModelLocations();
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, parts[0], true);
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.875D, 0.0D);
		poseStack.scale(0.99F, 1.0F, 0.99F);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, parts[1], true);
		poseStack.popPose();
	}

	/**
	 * Matches 1.7.10 {@code RenderEPress}: body + head at +1 then raised (idle).
	 */
	private static void renderElectricPress(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state) {
		ResourceLocation[] parts = StructureType.ELECTRIC_PRESS.getAllBerModelLocations();
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, parts[0], true);
		renderPartAt(poseStack, buffer, packedLight, packedOverlay, state, parts[1], 1.875D, true);
	}

	/**
	 * Matches 1.7.10 {@code DoorDecl.TRANSITION_SEAL}: +0.5 Z offset + Collada keyframe anim.
	 */
	private static void renderTransitionSeal(MultiblockControllerBlockEntity be, float partialTick,
			PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState state) {
		poseStack.translate(0.0D, 0.0D, 0.5D);
		float ticks = be.getTransitionSealOpenTicks(partialTick);
		org.joml.Matrix4f mat = new org.joml.Matrix4f();
		for (TransitionSealClientAnim.Part part : TransitionSealClientAnim.parts()) {
			if (!TransitionSealClientAnim.matrixFor(part, ticks, mat)) {
				continue;
			}
			poseStack.pushPose();
			poseStack.mulPoseMatrix(mat);
			renderBaked(poseStack, buffer, packedLight, packedOverlay, state, part.model(), false);
			poseStack.popPose();
		}
	}

	private enum RenderMode {
		SOLID, CUTOUT, CUTOUT_NO_CULL, TRANSLUCENT
	}

	private static void renderPartAt(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, double y, boolean cutout) {
		poseStack.pushPose();
		poseStack.translate(0.0D, y, 0.0D);
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc, cutout);
		poseStack.popPose();
	}

	private static void renderBaked(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, boolean cutout) {
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc,
				cutout ? RenderMode.CUTOUT : RenderMode.SOLID, 1f, 1f, 1f);
	}

	private static void renderBaked(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, RenderMode mode) {
		renderBaked(poseStack, buffer, packedLight, packedOverlay, state, modelLoc, mode, 1f, 1f, 1f);
	}

	private static void renderBaked(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			int packedOverlay, BlockState state, ResourceLocation modelLoc, RenderMode mode,
			float r, float g, float b) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		RenderType type = switch (mode) {
			case CUTOUT -> RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS);
			case CUTOUT_NO_CULL -> RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS);
			case TRANSLUCENT -> RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
			case SOLID -> RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS);
		};
		VertexConsumer consumer = buffer.getBuffer(type);
		renderer.renderModel(poseStack.last(), consumer, state, model, r, g, b, packedLight, packedOverlay,
				ModelData.EMPTY, null);
	}

	private static float facingToYRot(Direction facing) {
		return switch (facing) {
			case NORTH -> 90F;
			case WEST -> 180F;
			case SOUTH -> 270F;
			default -> 0F;
		};
	}

	@Override
	public boolean shouldRenderOffScreen(MultiblockControllerBlockEntity be) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
