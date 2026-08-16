package com.hbmr.client;

import com.hbmr.block.multiblock.MultiblockControllerBlock;
import com.hbmr.block.multiblock.StructureType;
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
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Inventory / hand renderer for multiblock controllers with OBJ BER models.
 */
public class MultiblockItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static MultiblockItemRenderer instance;

	public static MultiblockItemRenderer getInstance() {
		if (instance == null) {
			Minecraft mc = Minecraft.getInstance();
			instance = new MultiblockItemRenderer(mc);
		}
		return instance;
	}

	private MultiblockItemRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return;
		}
		if (!(blockItem.getBlock() instanceof MultiblockControllerBlock controller)) {
			return;
		}

		StructureType type = controller.getStructureType();
		if (type.isRbmkColumn()) {
			poseStack.pushPose();
			applyColumnItemTransforms(displayContext, poseStack);
			RBMKColumnRenderer.renderItem(type, poseStack, buffer, packedLight, packedOverlay);
			poseStack.popPose();
			return;
		}

		ResourceLocation[] models = type.getAllItemBerModelLocations();
		boolean customWavefront = type == StructureType.CONVEYOR_SPLITTER
				|| type == StructureType.RADIATION_POWERED_ENGINE
				|| type == StructureType.GEOTHERMAL_HEAT_EXCHANGER
				|| type == StructureType.CYCLOTRON
				|| BarrelRenderer.isBarrel(type)
				|| type == StructureType.BATTERY_SOCKET
				|| type == StructureType.FENSU
				|| type == StructureType.OLD_CAPACITOR
				|| type == StructureType.CHEMICAL_FACTORY
				|| type == StructureType.RBMK_CRANE_CONSOLE
				|| type == StructureType.RBMK_AUTOLOADER;
		if (models.length == 0 && !customWavefront) {
			return;
		}

		poseStack.pushPose();
		applyTransforms(type, displayContext, poseStack);

		if (type == StructureType.CONVEYOR_SPLITTER) {
			ConveyorSplitterRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
			poseStack.popPose();
			return;
		}

		RenderType renderType;
		if (type == StructureType.EXPOSURE_CHAMBER) {
			// Match 1.7.10: no cull so the far chamber wall shows through the shell.
			renderType = RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS);
		} else if (type == StructureType.SLIDING_BLAST_DOOR
				|| type == StructureType.VT_BLAST_DOOR
				|| type == StructureType.CARGO_ELEVATOR
				|| type == StructureType.SECURE_ACCESS_DOOR
				|| type == StructureType.LARGE_VEHICLE_DOOR
				|| type == StructureType.QE_CONTAINMENT_DOOR
				|| type == StructureType.QE_SLIDING_DOOR
				|| type == StructureType.ROUND_AIRLOCK_DOOR
				|| type == StructureType.SLIDING_SEAL_DOOR
				|| type == StructureType.WATER_DOOR
				|| type == StructureType.BURNER_PRESS
				|| type == StructureType.ELECTRIC_PRESS
				|| type == StructureType.HEAT_EXCHANGING_HEATER
				|| type == StructureType.IRON_FURNACE
				|| type == StructureType.STEEL_FURNACE
				|| type == StructureType.COMBINATION_OVEN
				|| type == StructureType.STIRLING_ENGINE
				|| type == StructureType.HEAVY_STIRLING_ENGINE
				|| type == StructureType.CREATIVE_STIRLING_ENGINE
				|| type == StructureType.STIRLING_SAWMILL
				|| type == StructureType.CRUCIBLE
				|| type == StructureType.STRAND_CASTER
				|| type == StructureType.BOILER
				|| type == StructureType.INDUSTRIAL_BOILER
				|| type == StructureType.CENTRIFUGE
				|| type == StructureType.GAS_CENTRIFUGE
				|| type == StructureType.FEL
				|| type == StructureType.SILEX
				|| type == StructureType.ROTARY_FURNACE
				|| type == StructureType.ORE_ACIDIZER
				|| type == StructureType.BREEDING_REACTOR
				|| type == StructureType.WOOD_BURNING_GENERATOR
				|| type == StructureType.DIESEL_GENERATOR
				|| type == StructureType.INDUSTRIAL_COMBUSTION_ENGINE
				|| type == StructureType.RESEARCH_REACTOR
				|| type == StructureType.ZIRNOX_NUCLEAR_REACTOR
				|| type == StructureType.INDUSTRIAL_GENERATOR
				|| type == StructureType.RADIATION_POWERED_ENGINE
				|| type == StructureType.CYCLOTRON
				|| type == StructureType.RT_GENERATOR
				|| type == StructureType.RTGRC
				|| type == StructureType.GEOTHERMAL_HEAT_EXCHANGER
				|| type == StructureType.PARTICLE_SOURCE
				|| type == StructureType.BEAMLINE
				|| type == StructureType.DRAINAGE_PIPE
				|| type == StructureType.TELEX_MACHINE
				|| type == StructureType.CONVEYOR_SPLITTER
				|| type == StructureType.FAN
				|| type == StructureType.INSERTER
				|| type == StructureType.SAFE_BARREL
				|| type == StructureType.STEEL_BARREL
				|| type == StructureType.TECHNETIUM_STEEL_BARREL
				|| type == StructureType.MAGNETIC_ANTIMATTER_CONTAINER
				|| type == StructureType.BATTERY_SOCKET
				|| type == StructureType.FENSU
				|| type == StructureType.OLD_CAPACITOR
				|| type == StructureType.MICROWAVE
				|| type == StructureType.ASSEMBLY_MACHINE
				|| type == StructureType.ASSEMBLY_FACTORY
				|| type == StructureType.PRECASS
				|| type == StructureType.CHEMICAL_PLANT
				|| type == StructureType.CHEMICAL_FACTORY
				|| type == StructureType.RF_CAVITY
				|| type == StructureType.QUADRUPOLE_MAGNETS
				|| type == StructureType.DIPOLE_MAGNETS
				|| type == StructureType.PARTICLE_DETECTOR
				|| type == StructureType.RBMK_CONSOLE
				|| type == StructureType.RBMK_CRANE_CONSOLE
				|| type == StructureType.RBMK_AUTOLOADER
				|| type == StructureType.RBMK_DEBRIS
				|| type == StructureType.RBMK_DEBRIS_FLAMING
				|| type == StructureType.RBMK_DEBRIS_SMOLDERING
				|| type == StructureType.RBMK_DEBRIS_BLACKENED) {
			renderType = RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS);
		} else {
			renderType = RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS);
		}
		VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
				buffer,
				renderType,
				true,
				stack.hasFoil());
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		if (type == StructureType.CARGO_ELEVATOR) {
			// Match 1.7.10 item assembly: Base + Piston + Guides, then +1 Piston/Guides/Platform, then +1 Guides
			ResourceLocation[] parts = models;
			renderItemModel(itemRenderer, parts[0], stack, packedLight, packedOverlay, poseStack, consumer);
			renderItemModel(itemRenderer, parts[2], stack, packedLight, packedOverlay, poseStack, consumer);
			for (int c = 3; c <= 6; c++) {
				renderItemModel(itemRenderer, parts[c], stack, packedLight, packedOverlay, poseStack, consumer);
			}
			poseStack.pushPose();
			poseStack.translate(0.0D, 1.0D, 0.0D);
			renderItemModel(itemRenderer, parts[2], stack, packedLight, packedOverlay, poseStack, consumer);
			for (int c = 3; c <= 6; c++) {
				renderItemModel(itemRenderer, parts[c], stack, packedLight, packedOverlay, poseStack, consumer);
			}
			renderItemModel(itemRenderer, parts[1], stack, packedLight, packedOverlay, poseStack, consumer);
			poseStack.translate(0.0D, 1.0D, 0.0D);
			for (int c = 3; c <= 6; c++) {
				renderItemModel(itemRenderer, parts[c], stack, packedLight, packedOverlay, poseStack, consumer);
			}
			poseStack.popPose();
		} else if (type == StructureType.BURNER_PRESS) {
			ResourceLocation[] parts = models;
			renderItemModel(itemRenderer, parts[0], stack, packedLight, packedOverlay, poseStack, consumer);
			poseStack.pushPose();
			poseStack.translate(0.0D, 0.5D, 0.0D);
			poseStack.scale(0.99F, 1.0F, 0.99F);
			renderItemModel(itemRenderer, parts[1], stack, packedLight, packedOverlay, poseStack, consumer);
			poseStack.popPose();
		} else if (type == StructureType.ELECTRIC_PRESS) {
			ResourceLocation[] parts = models;
			renderItemModel(itemRenderer, parts[0], stack, packedLight, packedOverlay, poseStack, consumer);
			poseStack.pushPose();
			poseStack.translate(0.0D, 1.5D, 0.0D);
			renderItemModel(itemRenderer, parts[1], stack, packedLight, packedOverlay, poseStack, consumer);
			poseStack.popPose();
		} else if (type == StructureType.RADIATION_POWERED_ENGINE) {
			// Match 1.7.10 ItemRenderLibrary via direct Wavefront path.
			RadGenRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.GEOTHERMAL_HEAT_EXCHANGER) {
			HephaestusRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.CYCLOTRON) {
			CyclotronRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (BarrelRenderer.isBarrel(type)) {
			// 1.7.10 renderInventoryBlock: translate(0, -0.5, 0) then Barrel part.
			poseStack.translate(0.0D, -0.5D, 0.0D);
			BarrelRenderer.render(poseStack, buffer, packedLight, packedOverlay, type);
		} else if (type == StructureType.BATTERY_SOCKET) {
			// 1.7.10 ItemRenderBase: Socket only (no Battery/Capacitor/Supports).
			BatterySocketRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.FENSU) {
			FensuRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.OLD_CAPACITOR) {
			CapacitorRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.CHEMICAL_FACTORY) {
			ChemicalFactoryRenderer.render(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.RBMK_CRANE_CONSOLE) {
			CraneConsoleRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
		} else if (type == StructureType.RBMK_AUTOLOADER) {
			AutoloaderRenderer.renderItem(poseStack, buffer, packedLight, packedOverlay);
		} else {
			for (ResourceLocation modelLoc : models) {
				renderItemModel(itemRenderer, modelLoc, stack, packedLight, packedOverlay, poseStack, consumer);
			}
		}

		poseStack.popPose();
	}

	private static void renderItemModel(ItemRenderer itemRenderer, ResourceLocation modelLoc, ItemStack stack,
			int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer consumer) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (model == null || model == Minecraft.getInstance().getModelManager().getMissingModel()) {
			return;
		}
		itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, consumer);
	}

	/**
	 * RBMK columns: 1.7.10 {@code RenderRBMKReflector} applies its own ×0.35 / Y−0.675.
	 * 1.20 GUI lacks the extra inventory shrink of ISBRH, so add a GUI scale so the
	 * 4-tall column fits a creative slot.
	 */
	private static void applyColumnItemTransforms(ItemDisplayContext ctx, PoseStack pose) {
		if (ctx == ItemDisplayContext.GROUND) {
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.scale(0.4F, 0.4F, 0.4F);
			return;
		}
		boolean gui = ctx == ItemDisplayContext.GUI || ctx == ItemDisplayContext.FIXED;
		if (gui) {
			pose.translate(0.5D, 0.5D, 0.0D);
			pose.mulPose(Axis.XP.rotationDegrees(30));
			pose.mulPose(Axis.YP.rotationDegrees(225));
			// 4 × 0.35 ≈ 1.4 overflows the slot; ×0.55 → ~0.77 tall.
			pose.scale(0.55F, 0.55F, 0.55F);
		} else {
			boolean firstPerson = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
					|| ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
			pose.translate(0.5D, firstPerson ? 0.35D : 0.25D, 0.5D);
			pose.scale(firstPerson ? 0.45F : 0.35F, firstPerson ? 0.45F : 0.35F, firstPerson ? 0.45F : 0.35F);
			if (ctx != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
					&& ctx != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
				pose.mulPose(Axis.YP.rotationDegrees(90));
			}
		}
	}

	private static void applyTransforms(StructureType type, ItemDisplayContext ctx, PoseStack pose) {
		if (ctx == ItemDisplayContext.GROUND) {
			applyGroundTransforms(type, pose);
			return;
		}

		boolean gui = ctx == ItemDisplayContext.GUI || ctx == ItemDisplayContext.FIXED;

		if (gui) {
			pose.translate(0.5D, 0.5D, 0.0D);
			pose.mulPose(Axis.XP.rotationDegrees(30));
			pose.mulPose(Axis.YP.rotationDegrees(225));
			applyGuiScale(type, pose);
			applyCommonYaw(type, pose);
		} else {
			// 1.20 applies display transforms first; keep hand models tiny so they don't fill the view.
			boolean firstPerson = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
					|| ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
			pose.translate(0.5D, firstPerson ? 0.35D : 0.25D, 0.5D);
			applyHandScale(type, pose, firstPerson);
			if (ctx != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
					&& ctx != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
				pose.mulPose(Axis.YP.rotationDegrees(90));
			}
			applyCommonYaw(type, pose);
		}
	}

	/**
	 * Dropped items: upright, centered on the entity shadow, smaller than GUI.
	 * <p>
	 * {@link net.minecraft.client.renderer.entity.ItemRenderer} translates −0.5 before
	 * BEWLR (expects 0…1 block models). Our BER OBJs are origin-centered (−0.5…0.5),
	 * so we add +0.5 to cancel that and sit on the shadow.
	 */
	private static void applyGroundTransforms(StructureType type, PoseStack pose) {
		pose.translate(0.5D, 0.5D, 0.5D);
		float s = switch (type) {
			case CARGO_ELEVATOR -> 0.12F;
			case VT_BLAST_DOOR -> 0.12F;
			case SLIDING_BLAST_DOOR -> 0.08F;
			case SLIDING_BLAST_DOOR_SHORT -> 0.14F;
			case FIRE_DOOR -> 0.12F;
			case SILO_HATCH -> 0.10F;
			case SILO_HATCH_LARGE -> 0.07F;
			case TRANSITION_SEAL -> 0.03F;
			case SECURE_ACCESS_DOOR -> 0.11F;
			case LARGE_VEHICLE_DOOR -> 0.09F;
			case ROUND_AIRLOCK_DOOR -> 0.12F;
			case QE_CONTAINMENT_DOOR, WATER_DOOR -> 0.14F;
			case QE_SLIDING_DOOR, SLIDING_SEAL_DOOR -> 0.18F;
			case BURNER_PRESS, ELECTRIC_PRESS -> 0.18F;
			case HEAT_EXCHANGING_HEATER, IRON_FURNACE, STEEL_FURNACE, COMBINATION_OVEN,
					STIRLING_ENGINE, HEAVY_STIRLING_ENGINE, CREATIVE_STIRLING_ENGINE,
					STIRLING_SAWMILL, CRUCIBLE -> 0.16F;
			case STRAND_CASTER -> 0.08F;
			case BOILER -> 0.10F;
			case INDUSTRIAL_BOILER -> 0.08F;
			case CENTRIFUGE, GAS_CENTRIFUGE, BREEDING_REACTOR, RESEARCH_REACTOR,
					DIESEL_GENERATOR -> 0.14F;
			case SILEX, ROTARY_FURNACE, ORE_ACIDIZER, WOOD_BURNING_GENERATOR -> 0.12F;
			case FEL, ZIRNOX_NUCLEAR_REACTOR, RADIATION_POWERED_ENGINE,
					INDUSTRIAL_COMBUSTION_ENGINE, CYCLOTRON, EXPOSURE_CHAMBER,
					GEOTHERMAL_HEAT_EXCHANGER, PARTICLE_SOURCE, RF_CAVITY,
					QUADRUPOLE_MAGNETS -> 0.08F;
			case RT_GENERATOR, RTGRC, BEAMLINE -> 0.14F;
			case INDUSTRIAL_GENERATOR -> 0.10F;
			default -> 0.12F;
		};
		pose.scale(s, s, s);
		if (type == StructureType.INDUSTRIAL_GENERATOR) {
			pose.scale(0.18F, 0.18F, 0.18F);
		}
		applyCommonYaw(type, pose);
	}

	private static void applyGuiScale(StructureType type, PoseStack pose) {
		// Decorative IGen OBJ is full-size; shrink to ~1-block before slot fitting.
		if (type == StructureType.INDUSTRIAL_GENERATOR) {
			pose.scale(0.18F, 0.18F, 0.18F);
		}

		// Fit each machine into roughly the same GUI slot footprint (~0.9 units).
		float extent = guiExtent(type);
		float s = 0.9F / extent;
		pose.scale(s, s, s);

		// After scale(s), translate is scaled too → keep ~−0.2 GUI units of drop.
		double yNudge = -extent * (0.2D / 0.9D);
		// Door / hatch overrides: tall models need a stronger drop to sit in-slot.
		yNudge = switch (type) {
			case VT_BLAST_DOOR -> -0.85D;
			case SLIDING_BLAST_DOOR -> -1.1D;
			case SLIDING_BLAST_DOOR_SHORT -> -1.5D;
			case TRANSITION_SEAL -> -0.4D;
			case LARGE_VEHICLE_DOOR -> -2.35D;
			case SECURE_ACCESS_DOOR -> -1.55D;
			case QE_CONTAINMENT_DOOR, WATER_DOOR -> -1.15D;
			case ROUND_AIRLOCK_DOOR -> -1.25D;
			case SLIDING_SEAL_DOOR -> -1.05D;
			case QE_SLIDING_DOOR -> -0.85D;
			case BURNER_PRESS, ELECTRIC_PRESS -> -1.15D;
			case STRAND_CASTER -> 0.0D;
			case INDUSTRIAL_GENERATOR -> -0.45D;
			case ROTARY_FURNACE -> -1.85D;
			case ORE_ACIDIZER -> -2.25D;
			case BREEDING_REACTOR -> -1.35D;
			case EXPOSURE_CHAMBER -> -0.5D;
			case GEOTHERMAL_HEAT_EXCHANGER -> -0.9D;
			// fensu2 mesh Y 0…10 — drop so the wheel sits mid-slot (was still top-heavy at −1.9).
			case FENSU -> -3.5D;
			case RT_GENERATOR -> -0.55D;
			case SAFE_BARREL, STEEL_BARREL, TECHNETIUM_STEEL_BARREL, MAGNETIC_ANTIMATTER_CONTAINER -> 0.1D;
			case CONVEYOR_SPLITTER -> -0.2D;
			// Was sitting on the slot floor — lift slightly into center.
			case OLD_CAPACITOR -> 0.05D;
			// Mesh center translated below; default extent nudge would pull it down.
			case MICROWAVE -> 0.0D;
			case ASSEMBLY_MACHINE, PRECASS, CHEMICAL_PLANT -> -0.85D;
			case ASSEMBLY_FACTORY, CHEMICAL_FACTORY -> -1.1D;
			default -> yNudge;
		};
		pose.translate(0.0D, yNudge, 0.0D);
		// Strand caster OBJ AABB is X±1, Y 0…3, Z -6…1 — shift geometric center to origin.
		if (type == StructureType.STRAND_CASTER) {
			pose.translate(0.0D, -1.5D, 2.5D);
		}
		// ICE: Z −5.5…0.625 becomes X after common +90° yaw; center that axis in-slot.
		if (type == StructureType.INDUSTRIAL_COMBUSTION_ENGINE) {
			pose.translate(2.45D, -1.0D, 0.0D);
		}
		// Exposure chamber long axis is model Z (−8.5…+2.5). After common +90° yaw that
		// becomes X; +X seats the tank (negative overshot right). Fine-tuned for slot center.
		if (type == StructureType.EXPOSURE_CHAMBER) {
			pose.translate(4.2D, -1.35D, 0.0D);
		}
		// Telex desk: mesh Z −0.5…1.5 → after +90° yaw that becomes X; pull center into slot.
		if (type == StructureType.TELEX_MACHINE) {
			pose.translate(-0.5D, -0.35D, 0.0D);
		}
		// Microwave OBJ AABB ~[0.11…0.86, 0.79…1.18, −0.76…−0.15].
		// Empirically: +X here slides left on the GUI isometric; −X / −Y = right / down.
		// (0.46,…) sat ~1 slot left of center — pull back and drop into the slot.
		if (type == StructureType.MICROWAVE) {
			pose.translate(-0.55D, -1.2D, 0.5D);
		}
		// Geothermal tower: mesh Y 0…12 — center vertically.
		if (type == StructureType.GEOTHERMAL_HEAT_EXCHANGER) {
			pose.translate(0.0D, -5.5D, 0.0D);
		}
		// PA machines: mesh Y 0…3 with core at +1 — drop so the body sits in the slot.
		if (type == StructureType.PARTICLE_SOURCE
				|| type == StructureType.RF_CAVITY
				|| type == StructureType.QUADRUPOLE_MAGNETS
				|| type == StructureType.DIPOLE_MAGNETS) {
			pose.translate(0.0D, -1.0D, 0.0D);
		}
		if (type == StructureType.PARTICLE_DETECTOR) {
			pose.translate(0.0D, -2.0D, 0.0D);
		}
		if (type == StructureType.BEAMLINE) {
			pose.translate(0.0D, -0.35D, 0.0D);
		}
		// Drain mesh X −2.5…0.5. Common +90° is applied *after* this translate in the
		// PoseStack build order, so it runs *first* on vertices (X → −Z). Nudge in Z to
		// slide along the pipe in-slot (prior +X only shifted thickness / looked unchanged).
		if (type == StructureType.DRAINAGE_PIPE) {
			pose.translate(0.0D, -0.25D, -1.5D);
		}
	}

	/**
	 * Approximate model span in block units (largest axis of footprint / known mesh).
	 * GUI scale is {@code 0.9 / extent} so icons land in a similar on-screen size.
	 */
	private static float guiExtent(StructureType type) {
		return switch (type) {
			case INDUSTRIAL_GENERATOR -> 1.65F; // after ×0.18 model fit → ~0.55 slot scale
			case DIESEL_GENERATOR, BREEDING_REACTOR -> 3.5F;
			case QE_SLIDING_DOOR, SLIDING_SEAL_DOOR -> 4.5F;
			case HEAT_EXCHANGING_HEATER, IRON_FURNACE, STEEL_FURNACE, COMBINATION_OVEN,
					STIRLING_ENGINE, HEAVY_STIRLING_ENGINE, CREATIVE_STIRLING_ENGINE,
					STIRLING_SAWMILL, CRUCIBLE, WOOD_BURNING_GENERATOR, SILEX -> 5.3F;
			case BURNER_PRESS, ELECTRIC_PRESS, CENTRIFUGE, GAS_CENTRIFUGE,
					RESEARCH_REACTOR -> 5.6F;
			case BOILER, FIRE_DOOR, ROUND_AIRLOCK_DOOR, QE_CONTAINMENT_DOOR, WATER_DOOR,
					CARGO_ELEVATOR, VT_BLAST_DOOR, SILO_HATCH -> 6.0F;
			case ROTARY_FURNACE, ORE_ACIDIZER, INDUSTRIAL_BOILER, SECURE_ACCESS_DOOR,
					SLIDING_BLAST_DOOR_SHORT -> 6.4F;
			case FEL, RADIATION_POWERED_ENGINE, INDUSTRIAL_COMBUSTION_ENGINE,
					ZIRNOX_NUCLEAR_REACTOR, SLIDING_BLAST_DOOR, LARGE_VEHICLE_DOOR,
					SILO_HATCH_LARGE, CYCLOTRON -> 6.9F;
			// Long coil arm (~11) — was sharing cyclotron's 6.9 and overflowed the slot.
			case EXPOSURE_CHAMBER -> 11.5F;
			// Tower Y 0…12 — was too large at 7.5.
			case GEOTHERMAL_HEAT_EXCHANGER -> 11.0F;
			// fensu2 / REDD wheel ~10 tall × ~9 wide.
			case FENSU -> 12.0F;
			// Centered mesh span ~0.75 — was 1.4 and looked tiny/off-corner.
			case MICROWAVE -> 1.0F;
			case ASSEMBLY_MACHINE, PRECASS, CHEMICAL_PLANT -> 5.5F;
			case ASSEMBLY_FACTORY, CHEMICAL_FACTORY -> 7.5F;
			case PARTICLE_SOURCE, RF_CAVITY, QUADRUPOLE_MAGNETS, DIPOLE_MAGNETS -> 8.5F;
			case PARTICLE_DETECTOR -> 10.0F;
			case RBMK_CONSOLE, RBMK_CRANE_CONSOLE -> 6.5F;
			case RBMK_AUTOLOADER -> 9.0F;
			case RBMK_DEBRIS, RBMK_DEBRIS_FLAMING, RBMK_DEBRIS_SMOLDERING, RBMK_DEBRIS_BLACKENED -> 1.2F;
			case RT_GENERATOR -> 2.8F;
			case RTGRC, BEAMLINE -> 4.5F;
			case DRAINAGE_PIPE -> 3.6F;
			case TELEX_MACHINE -> 3.2F;
			case CONVEYOR_SPLITTER -> 2.4F;
			case FAN, INSERTER -> 1.55F;
			case SAFE_BARREL, STEEL_BARREL, TECHNETIUM_STEEL_BARREL,
					MAGNETIC_ANTIMATTER_CONTAINER -> 1.55F;
			// 1-block capacitor was too tiny at 3.5; match barrel/fan slot scale.
			case OLD_CAPACITOR -> 1.55F;
			// Socket frame was overflowing the slot; larger extent → smaller icon.
			case BATTERY_SOCKET -> 3.2F;
			case STRAND_CASTER -> 7.5F;
			case TRANSITION_SEAL -> 26.0F;
			default -> {
				int[] d = type.getDims();
				float w = d[4] + d[5] + 1;
				float h = d[0] + d[1] + 1;
				float depth = d[2] + d[3] + 1;
				yield Math.max(4.5F, Math.max(w, Math.max(h, depth)));
			}
		};
	}

	/**
	 * Hand scales are much smaller than 1.7.10's raw 0.25 because modern item display
	 * transforms already enlarge the model in first person.
	 */
	private static void applyHandScale(StructureType type, PoseStack pose, boolean firstPerson) {
		float s = switch (type) {
			case CARGO_ELEVATOR -> 0.055F;
			case VT_BLAST_DOOR -> 0.04F;
			case SLIDING_BLAST_DOOR -> 0.035F;
			case SLIDING_BLAST_DOOR_SHORT -> 0.06F;
			case FIRE_DOOR -> 0.05F;
			case SILO_HATCH -> 0.04F;
			case SILO_HATCH_LARGE -> 0.03F;
			case SECURE_ACCESS_DOOR, LARGE_VEHICLE_DOOR -> 0.04F;
			case ROUND_AIRLOCK_DOOR -> 0.045F;
			case QE_CONTAINMENT_DOOR, WATER_DOOR -> 0.055F;
			case QE_SLIDING_DOOR, SLIDING_SEAL_DOOR -> 0.07F;
			case BURNER_PRESS, ELECTRIC_PRESS -> 0.04F;
			case BOILER, INDUSTRIAL_BOILER, STRAND_CASTER -> 0.03F;
			case HEAT_EXCHANGING_HEATER, IRON_FURNACE, STEEL_FURNACE, COMBINATION_OVEN,
					STIRLING_ENGINE, HEAVY_STIRLING_ENGINE, CREATIVE_STIRLING_ENGINE,
					STIRLING_SAWMILL, CRUCIBLE -> 0.04F;
			case FEL, ZIRNOX_NUCLEAR_REACTOR, RADIATION_POWERED_ENGINE,
					INDUSTRIAL_COMBUSTION_ENGINE, ORE_ACIDIZER, ROTARY_FURNACE,
					CYCLOTRON, EXPOSURE_CHAMBER, GEOTHERMAL_HEAT_EXCHANGER,
					PARTICLE_SOURCE, RF_CAVITY, QUADRUPOLE_MAGNETS -> 0.03F;
			case CENTRIFUGE, GAS_CENTRIFUGE, BREEDING_REACTOR, RESEARCH_REACTOR,
					SILEX, WOOD_BURNING_GENERATOR, DIESEL_GENERATOR,
					RT_GENERATOR, RTGRC, BEAMLINE, DRAINAGE_PIPE, TELEX_MACHINE, CONVEYOR_SPLITTER,
					FAN, INSERTER, SAFE_BARREL, STEEL_BARREL, TECHNETIUM_STEEL_BARREL,
					MAGNETIC_ANTIMATTER_CONTAINER, BATTERY_SOCKET, OLD_CAPACITOR,
					MICROWAVE, ASSEMBLY_MACHINE, ASSEMBLY_FACTORY, PRECASS,
					CHEMICAL_PLANT, CHEMICAL_FACTORY -> 0.04F;
			case FENSU -> 0.03F;
			case INDUSTRIAL_GENERATOR -> 0.05F;
			default -> 0.04F;
		};
		if (firstPerson) {
			s *= 0.75F;
		}
		pose.scale(s, s, s);
		if (type == StructureType.INDUSTRIAL_GENERATOR) {
			pose.scale(0.18F, 0.18F, 0.18F);
		}
	}

	private static void applyCommonYaw(StructureType type, PoseStack pose) {
		switch (type) {
			case FIRE_DOOR, SILO_HATCH, SILO_HATCH_LARGE, SLIDING_BLAST_DOOR_SHORT,
					SECURE_ACCESS_DOOR, LARGE_VEHICLE_DOOR, QE_CONTAINMENT_DOOR, QE_SLIDING_DOOR,
					ROUND_AIRLOCK_DOOR, SLIDING_SEAL_DOOR, WATER_DOOR, BURNER_PRESS, ELECTRIC_PRESS,
					HEAT_EXCHANGING_HEATER, IRON_FURNACE, STEEL_FURNACE, COMBINATION_OVEN,
					STIRLING_ENGINE, HEAVY_STIRLING_ENGINE, CREATIVE_STIRLING_ENGINE,
					STIRLING_SAWMILL, CRUCIBLE, STRAND_CASTER, BOILER, INDUSTRIAL_BOILER,
					CENTRIFUGE, GAS_CENTRIFUGE, FEL, SILEX, ROTARY_FURNACE,
					ORE_ACIDIZER, BREEDING_REACTOR, WOOD_BURNING_GENERATOR, DIESEL_GENERATOR,
					INDUSTRIAL_COMBUSTION_ENGINE, RESEARCH_REACTOR, ZIRNOX_NUCLEAR_REACTOR,
					INDUSTRIAL_GENERATOR, RADIATION_POWERED_ENGINE, CYCLOTRON, EXPOSURE_CHAMBER,
					RT_GENERATOR, RTGRC, GEOTHERMAL_HEAT_EXCHANGER, PARTICLE_SOURCE, BEAMLINE,
					DRAINAGE_PIPE, TELEX_MACHINE,
					FAN, INSERTER, SAFE_BARREL, STEEL_BARREL, TECHNETIUM_STEEL_BARREL,
					MAGNETIC_ANTIMATTER_CONTAINER, BATTERY_SOCKET, OLD_CAPACITOR,
					ASSEMBLY_MACHINE, ASSEMBLY_FACTORY, PRECASS,
					CHEMICAL_PLANT, CHEMICAL_FACTORY,
					RF_CAVITY, QUADRUPOLE_MAGNETS, PARTICLE_DETECTOR, RBMK_CONSOLE,
					RBMK_CRANE_CONSOLE, RBMK_AUTOLOADER, RBMK_DEBRIS, RBMK_DEBRIS_FLAMING,
					RBMK_DEBRIS_SMOLDERING, RBMK_DEBRIS_BLACKENED ->
					pose.mulPose(Axis.YP.rotationDegrees(90));
			// fensu2 / RenderBatteryREDD item: −90 Y (old fensu.obj used +90).
			case FENSU -> pose.mulPose(Axis.YP.rotationDegrees(-90));
			// +90 shows the back; −90 (+180 from that) puts the glass front toward the camera.
			case MICROWAVE -> pose.mulPose(Axis.YP.rotationDegrees(-90));
			// CONVEYOR_SPLITTER: ConveyorSplitterRenderer applies 1.7.10 inventory −90 Y itself.
			default -> {
			}
		}
	}
}
