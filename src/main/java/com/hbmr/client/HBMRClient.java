package com.hbmr.client;

import com.hbmr.HBMR;
import com.hbmr.block.multiblock.StructureType;
import com.hbmr.client.model.BoxCableModelLoader;
import com.hbmr.registry.ModBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HBMR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HBMRClient {
	private HBMRClient() {
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			BlockEntityRenderers.register(ModBlockEntities.MULTIBLOCK_CONTROLLER.get(), MultiblockBER::new);
			BlockEntityRenderers.register(ModBlockEntities.ANVIL.get(), AnvilBER::new);
			BlockEntityRenderers.register(ModBlockEntities.CABLE_NETWORK.get(), CableNeoBER::new);
			BlockEntityRenderers.register(ModBlockEntities.FLUID_NETWORK.get(), PipeNeoBER::new);
			BlockEntityRenderers.register(ModBlockEntities.PIPE_ANCHOR.get(), PipeAnchorBER::new);
			BlockEntityRenderers.register(ModBlockEntities.NETWORK_PYLON.get(), NetworkPylonBER::new);
			BlockEntityRenderers.register(ModBlockEntities.ACID_INPUT_PARTITIONER.get(), AcidInputPartitionerBER::new);
		});
	}

	@SubscribeEvent
	public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register("box_cable", BoxCableModelLoader.INSTANCE);
	}

	@SubscribeEvent
	public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		// Replace vanilla so ENTITYBLOCK_ANIMATED anvils still draw while falling.
		event.registerEntityRenderer(EntityType.FALLING_BLOCK, HbmrFallingBlockRenderer::new);
	}

	@SubscribeEvent
	public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
		TransitionSealClientAnim.invalidate();
		for (StructureType type : StructureType.values()) {
			for (ResourceLocation loc : type.getAllBerModelLocations()) {
				event.register(loc);
			}
			for (ResourceLocation itemLoc : type.getAllItemBerModelLocations()) {
				event.register(itemLoc);
			}
			if (type.isRbmkColumn()) {
				event.register(ResourceLocation.fromNamespaceAndPath(HBMR.MODID,
						"block/" + type.name().toLowerCase() + "_cube"));
			}
		}
	}
}
