package com.hbmr.registry;

import com.hbmr.HBMR;
import com.hbmr.block.AnvilBlock;
import com.hbmr.block.ChainlinkFenceBlock;
import com.hbmr.block.CombinatorFunnelBlock;
import com.hbmr.block.CorruptedBroadcasterBlock;
import com.hbmr.block.BlastFurnaceBlock;
import com.hbmr.block.BlastFurnaceExtensionBlock;
import com.hbmr.block.FacingMachineBlock;
import com.hbmr.block.AcidInputPartitionerBlock;
import com.hbmr.block.RoRPanelBlock;
import com.hbmr.block.FoundryBasinBlock;
import com.hbmr.block.FoundryChannelBlock;
import com.hbmr.block.FoundryOutletBlock;
import com.hbmr.block.FoundryStorageBasinBlock;
import com.hbmr.block.GeigerCounterBlock;
import com.hbmr.block.SiloHatchOpenerBlock;
import com.hbmr.block.SuitBatteryBlock;
import com.hbmr.block.multiblock.MultiblockControllerBlock;
import com.hbmr.block.multiblock.StructureDummyBlock;
import com.hbmr.block.multiblock.StructureType;
import com.hbmr.block.network.BoxDuctBlock;
import com.hbmr.block.network.BoxDuctMaterial;
import com.hbmr.block.network.CableNetworkBlock;
import com.hbmr.block.network.EnergyFacingBlock;
import com.hbmr.block.network.EnergyNetworkNodeBlock;
import com.hbmr.block.network.NetworkPylonBlock;
import com.hbmr.block.network.NetworkPylonKind;
import com.hbmr.block.network.FluidDuctMaterial;
import com.hbmr.block.network.FluidFacingBlock;
import com.hbmr.block.network.PipeAnchorBlock;
import com.hbmr.block.network.RttyBlock;
import com.hbmr.block.network.FluidNetworkBlock;
import com.hbmr.block.network.FluidNetworkNodeBlock;
import com.hbmr.block.network.PneumaticNetworkNodeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS =
			DeferredRegister.create(ForgeRegistries.BLOCKS, HBMR.MODID);

	public static final RegistryObject<Block> SIREN = BLOCKS.register("siren",
			() -> new Block(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F, 10.0F)
					.sound(SoundType.METAL)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> CORRUPTED_BROADCASTER = BLOCKS.register("corrupted_broadcaster",
			() -> new CorruptedBroadcasterBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F, 15.0F)
					.sound(SoundType.METAL)
					.noOcclusion()
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> GEIGER_COUNTER = BLOCKS.register("geiger_counter",
			() -> new GeigerCounterBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(15.0F, 0.25F)
					.sound(SoundType.METAL)
					.noOcclusion()
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> SUIT_BATTERY = BLOCKS.register("suit_battery",
			() -> new SuitBatteryBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(0.5F, 0.25F)
					.sound(SoundType.METAL)
					.lightLevel(state -> 10)
					.noOcclusion()));

	public static final RegistryObject<Block> CHAINLINK_FENCE = BLOCKS.register("chainlink_fence",
			() -> new ChainlinkFenceBlock(fenceProperties(), false));

	public static final RegistryObject<Block> CHAINLINK_FENCE_POST = BLOCKS.register("chainlink_fence_post",
			() -> new ChainlinkFenceBlock(fenceProperties(), true));

	public static final RegistryObject<Block> ASH = BLOCKS.register("ash",
			() -> new FallingBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_GRAY)
					.strength(0.5F, 150.0F)
					.sound(SoundType.SAND)));

	public static final RegistryObject<Block> BORON_SAND = BLOCKS.register("boron_sand",
			() -> new FallingBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.SAND)
					.strength(0.5F)
					.sound(SoundType.SAND)));

	public static final RegistryObject<Block> LEAD_SAND = BLOCKS.register("lead_sand",
			() -> new FallingBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_GRAY)
					.strength(0.5F)
					.sound(SoundType.SAND)));

	// --- Machines batch: sands ---
	public static final RegistryObject<Block> URANIUM_SAND = BLOCKS.register("uranium_sand",
			() -> new FallingBlock(sandProperties(MapColor.COLOR_LIGHT_GREEN)));

	public static final RegistryObject<Block> POLONIUM_SAND = BLOCKS.register("polonium_sand",
			() -> new FallingBlock(sandProperties(MapColor.COLOR_YELLOW)));

	public static final RegistryObject<Block> QUARTZ_SAND = BLOCKS.register("quartz_sand",
			() -> new FallingBlock(sandProperties(MapColor.SAND)));

	// --- Glasses ---
	public static final RegistryObject<Block> BORON_GLASS = BLOCKS.register("boron_glass",
			() -> new GlassBlock(glassProperties(0.3F, 0)));

	public static final RegistryObject<Block> LEAD_GLASS = BLOCKS.register("lead_glass",
			() -> new GlassBlock(glassProperties(0.3F, 0)));

	public static final RegistryObject<Block> URANIUM_GLASS = BLOCKS.register("uranium_glass",
			() -> new GlassBlock(glassProperties(0.3F, 5)));

	public static final RegistryObject<Block> TRINITY_GLASS = BLOCKS.register("trinity_glass",
			() -> new GlassBlock(glassProperties(0.3F, 5)));

	public static final RegistryObject<Block> POLONIUM_GLASS = BLOCKS.register("polonium_glass",
			() -> new GlassBlock(glassProperties(0.3F, 5)));

	public static final RegistryObject<Block> ASH_GLASS = BLOCKS.register("ash_glass",
			() -> new GlassBlock(glassProperties(3.0F, 0)));

	public static final RegistryObject<Block> POLARIZED_GLASS = BLOCKS.register("polarized_glass",
			() -> new GlassBlock(glassProperties(0.3F, 0)));

	// --- Silo hatch frame / opener ---
	public static final RegistryObject<Block> SILO_HATCH_FRAME = BLOCKS.register("silo_hatch_frame",
			() -> new Block(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(10.0F, 100.0F)
					.sound(SoundType.METAL)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> SILO_HATCH_OPENER = BLOCKS.register("silo_hatch_opener",
			() -> new SiloHatchOpenerBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(10.0F, 100.0F)
					.sound(SoundType.METAL)
					.requiresCorrectToolForDrops()));

	// --- Multiblock dummy + controllers ---
	public static final RegistryObject<Block> STRUCTURE_DUMMY = BLOCKS.register("structure_dummy",
			() -> new StructureDummyBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F, 10.0F)
					.sound(SoundType.METAL)
					.noOcclusion()
					.dynamicShape()
					.isSuffocating((state, level, pos) -> false)
					.isViewBlocking((state, level, pos) -> false)
					.noLootTable()));

	public static final RegistryObject<Block> CARGO_ELEVATOR = BLOCKS.register("cargo_elevator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CARGO_ELEVATOR));

	public static final RegistryObject<Block> VT_BLAST_DOOR = BLOCKS.register("vt_blast_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.VT_BLAST_DOOR));

	public static final RegistryObject<Block> SLIDING_BLAST_DOOR = BLOCKS.register("sliding_blast_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.SLIDING_BLAST_DOOR));

	public static final RegistryObject<Block> SLIDING_BLAST_DOOR_SHORT = BLOCKS.register("sliding_blast_door_short",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.SLIDING_BLAST_DOOR_SHORT));

	public static final RegistryObject<Block> FIRE_DOOR = BLOCKS.register("fire_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.FIRE_DOOR));

	public static final RegistryObject<Block> TRANSITION_SEAL = BLOCKS.register("transition_seal",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.TRANSITION_SEAL));

	public static final RegistryObject<Block> SILO_HATCH = BLOCKS.register("silo_hatch",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.SILO_HATCH));

	public static final RegistryObject<Block> SILO_HATCH_LARGE = BLOCKS.register("silo_hatch_large",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 100.0F), StructureType.SILO_HATCH_LARGE));

	public static final RegistryObject<Block> SECURE_ACCESS_DOOR = BLOCKS.register("secure_access_door",
			() -> new MultiblockControllerBlock(machineProperties(20.0F, 2000.0F), StructureType.SECURE_ACCESS_DOOR));
	public static final RegistryObject<Block> LARGE_VEHICLE_DOOR = BLOCKS.register("large_vehicle_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 1000.0F), StructureType.LARGE_VEHICLE_DOOR));
	public static final RegistryObject<Block> QE_CONTAINMENT_DOOR = BLOCKS.register("qe_containment_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 1000.0F), StructureType.QE_CONTAINMENT_DOOR));
	public static final RegistryObject<Block> QE_SLIDING_DOOR = BLOCKS.register("qe_sliding_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 1000.0F), StructureType.QE_SLIDING_DOOR));
	public static final RegistryObject<Block> ROUND_AIRLOCK_DOOR = BLOCKS.register("round_airlock_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 1000.0F), StructureType.ROUND_AIRLOCK_DOOR));
	public static final RegistryObject<Block> SLIDING_SEAL_DOOR = BLOCKS.register("sliding_seal_door",
			() -> new MultiblockControllerBlock(machineProperties(10.0F, 1000.0F), StructureType.SLIDING_SEAL_DOOR));
	public static final RegistryObject<Block> WATER_DOOR = BLOCKS.register("water_door",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 50.0F), StructureType.WATER_DOOR));

	public static final RegistryObject<Block> IRON_CRATE = BLOCKS.register("iron_crate",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> STEEL_CRATE = BLOCKS.register("steel_crate",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DESH_CRATE = BLOCKS.register("desh_crate",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> TUNGSTEN_CRATE = BLOCKS.register("tungsten_crate",
			() -> new Block(machineProperties(7.5F, 300.0F)));
	public static final RegistryObject<Block> TEMPLATE_CRATE = BLOCKS.register("template_crate",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> SAFE = BLOCKS.register("safe",
			() -> new SiloHatchOpenerBlock(machineProperties(7.5F, 10000.0F)));
	public static final RegistryObject<Block> MASS_STORAGE_1 = BLOCKS.register("mass_storage_1",
			() -> new SiloHatchOpenerBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> MASS_STORAGE_2 = BLOCKS.register("mass_storage_2",
			() -> new SiloHatchOpenerBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> MASS_STORAGE_3 = BLOCKS.register("mass_storage_3",
			() -> new SiloHatchOpenerBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> MASS_STORAGE_4 = BLOCKS.register("mass_storage_4",
			() -> new SiloHatchOpenerBlock(machineProperties(5.0F, 10.0F)));

	// --- Autocrafter / funnel / anvils / presses ---
	public static final RegistryObject<Block> AUTOMATIC_CRAFTING_TABLE = BLOCKS.register("automatic_crafting_table",
			() -> new Block(machineProperties(10.0F, 20.0F)));
	public static final RegistryObject<Block> COMBINATOR_FUNNEL = BLOCKS.register("combinator_funnel",
			() -> new CombinatorFunnelBlock(machineProperties(10.0F, 20.0F)));

	public static final RegistryObject<Block> IRON_ANVIL = BLOCKS.register("iron_anvil",
			() -> new AnvilBlock(anvilProperties(), 1));
	public static final RegistryObject<Block> LEAD_ANVIL = BLOCKS.register("lead_anvil",
			() -> new AnvilBlock(anvilProperties(), 1));
	public static final RegistryObject<Block> STEEL_ANVIL = BLOCKS.register("steel_anvil",
			() -> new AnvilBlock(anvilProperties(), 2));
	public static final RegistryObject<Block> DESH_ANVIL = BLOCKS.register("desh_anvil",
			() -> new AnvilBlock(anvilProperties(), 3));
	public static final RegistryObject<Block> FERROURANIUM_ANVIL = BLOCKS.register("ferrouranium_anvil",
			() -> new AnvilBlock(anvilProperties(), 4));
	public static final RegistryObject<Block> SATURNITE_ANVIL = BLOCKS.register("saturnite_anvil",
			() -> new AnvilBlock(anvilProperties(), 5));
	public static final RegistryObject<Block> BISMUTH_BRONZE_ANVIL = BLOCKS.register("bismuth_bronze_anvil",
			() -> new AnvilBlock(anvilProperties(), 5));
	public static final RegistryObject<Block> ARSENIC_BRONZE_ANVIL = BLOCKS.register("arsenic_bronze_anvil",
			() -> new AnvilBlock(anvilProperties(), 5));
	public static final RegistryObject<Block> SCHRABIDATE_ANVIL = BLOCKS.register("schrabidate_anvil",
			() -> new AnvilBlock(anvilProperties(), 6));
	public static final RegistryObject<Block> DNT_ANVIL = BLOCKS.register("dnt_anvil",
			() -> new AnvilBlock(anvilProperties(), 7));
	public static final RegistryObject<Block> OSMIRIDIUM_ANVIL = BLOCKS.register("osmiridium_anvil",
			() -> new AnvilBlock(anvilProperties(), 8));
	public static final RegistryObject<Block> MURKY_ANVIL = BLOCKS.register("murky_anvil",
			() -> new AnvilBlock(anvilProperties(), 1916169));

	public static final RegistryObject<Block> BURNER_PRESS_PREHEATER = BLOCKS.register("burner_press_preheater",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> BURNER_PRESS = BLOCKS.register("burner_press",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.BURNER_PRESS));
	public static final RegistryObject<Block> ELECTRIC_PRESS = BLOCKS.register("electric_press",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ELECTRIC_PRESS));

	public static final RegistryObject<Block> HEAT_EXCHANGING_HEATER = BLOCKS.register("heat_exchanging_heater",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.HEAT_EXCHANGING_HEATER));
	public static final RegistryObject<Block> IRON_FURNACE = BLOCKS.register("iron_furnace",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.IRON_FURNACE));
	public static final RegistryObject<Block> STEEL_FURNACE = BLOCKS.register("steel_furnace",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.STEEL_FURNACE));
	public static final RegistryObject<Block> COMBINATION_OVEN = BLOCKS.register("combination_oven",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.COMBINATION_OVEN));
	public static final RegistryObject<Block> STIRLING_ENGINE = BLOCKS.register("stirling_engine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.STIRLING_ENGINE));
	public static final RegistryObject<Block> HEAVY_STIRLING_ENGINE = BLOCKS.register("heavy_stirling_engine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.HEAVY_STIRLING_ENGINE));
	public static final RegistryObject<Block> CREATIVE_STIRLING_ENGINE = BLOCKS.register("creative_stirling_engine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CREATIVE_STIRLING_ENGINE));
	public static final RegistryObject<Block> STIRLING_SAWMILL = BLOCKS.register("stirling_sawmill",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.STIRLING_SAWMILL));
	public static final RegistryObject<Block> CRUCIBLE = BLOCKS.register("crucible",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CRUCIBLE));
	public static final RegistryObject<Block> STRAND_CASTER = BLOCKS.register("strand_caster",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.STRAND_CASTER));
	public static final RegistryObject<Block> BOILER = BLOCKS.register("boiler",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.BOILER));
	public static final RegistryObject<Block> INDUSTRIAL_BOILER = BLOCKS.register("industrial_boiler",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.INDUSTRIAL_BOILER));
	public static final RegistryObject<Block> BLAST_FURNACE = BLOCKS.register("blast_furnace",
			() -> new BlastFurnaceBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CENTRIFUGE = BLOCKS.register("centrifuge",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CENTRIFUGE));
	public static final RegistryObject<Block> GAS_CENTRIFUGE = BLOCKS.register("gas_centrifuge",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.GAS_CENTRIFUGE));
	public static final RegistryObject<Block> FEL = BLOCKS.register("fel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.FEL));
	public static final RegistryObject<Block> SILEX = BLOCKS.register("silex",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.SILEX));
	public static final RegistryObject<Block> ROTARY_FURNACE = BLOCKS.register("rotary_furnace",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ROTARY_FURNACE));
	public static final RegistryObject<Block> ORE_ACIDIZER = BLOCKS.register("ore_acidizer",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ORE_ACIDIZER));
	public static final RegistryObject<Block> BREEDING_REACTOR = BLOCKS.register("breeding_reactor",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.BREEDING_REACTOR));
	public static final RegistryObject<Block> WOOD_BURNING_GENERATOR = BLOCKS.register("wood_burning_generator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.WOOD_BURNING_GENERATOR));
	public static final RegistryObject<Block> DIESEL_GENERATOR = BLOCKS.register("diesel_generator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.DIESEL_GENERATOR));
	public static final RegistryObject<Block> INDUSTRIAL_COMBUSTION_ENGINE = BLOCKS.register("industrial_combustion_engine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.INDUSTRIAL_COMBUSTION_ENGINE));
	public static final RegistryObject<Block> RESEARCH_REACTOR = BLOCKS.register("research_reactor",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RESEARCH_REACTOR));
	public static final RegistryObject<Block> ZIRNOX_NUCLEAR_REACTOR = BLOCKS.register("zirnox_nuclear_reactor",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ZIRNOX_NUCLEAR_REACTOR));
	public static final RegistryObject<Block> INDUSTRIAL_GENERATOR = BLOCKS.register("industrial_generator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.INDUSTRIAL_GENERATOR));
	public static final RegistryObject<Block> RADIATION_POWERED_ENGINE = BLOCKS.register("radiation_powered_engine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RADIATION_POWERED_ENGINE));
	public static final RegistryObject<Block> CYCLOTRON = BLOCKS.register("cyclotron",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CYCLOTRON));
	public static final RegistryObject<Block> EXPOSURE_CHAMBER = BLOCKS.register("exposure_chamber",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.EXPOSURE_CHAMBER));
	public static final RegistryObject<Block> RT_GENERATOR = BLOCKS.register("rt_generator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RT_GENERATOR));
	public static final RegistryObject<Block> RTGRC = BLOCKS.register("rtgrc",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RTGRC));
	public static final RegistryObject<Block> GEOTHERMAL_HEAT_EXCHANGER = BLOCKS.register("geothermal_heat_exchanger",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.GEOTHERMAL_HEAT_EXCHANGER));
	public static final RegistryObject<Block> PARTICLE_SOURCE = BLOCKS.register("particle_source",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.PARTICLE_SOURCE));
	public static final RegistryObject<Block> BEAMLINE = BLOCKS.register("beamline",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.BEAMLINE));
	public static final RegistryObject<Block> RF_CAVITY = BLOCKS.register("rf_cavity",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RF_CAVITY));
	public static final RegistryObject<Block> QUADRUPOLE_MAGNETS = BLOCKS.register("quadrupole_magnets",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.QUADRUPOLE_MAGNETS));
	public static final RegistryObject<Block> DIPOLE_MAGNETS = BLOCKS.register("dipole_magnets",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.DIPOLE_MAGNETS));
	public static final RegistryObject<Block> PARTICLE_DETECTOR = BLOCKS.register("particle_detector",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.PARTICLE_DETECTOR));
	public static final RegistryObject<Block> RBMK_CONSOLE = BLOCKS.register("rbmk_console",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CONSOLE));
	public static final RegistryObject<Block> RBMK_CRANE_CONSOLE = BLOCKS.register("rbmk_crane_console",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CRANE_CONSOLE));
	public static final RegistryObject<Block> RBMK_AUTOLOADER = BLOCKS.register("rbmk_autoloader",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_AUTOLOADER));
	public static final RegistryObject<Block> RBMK_DEBRIS = BLOCKS.register("rbmk_debris",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_DEBRIS));
	public static final RegistryObject<Block> RBMK_DEBRIS_FLAMING = BLOCKS.register("rbmk_debris_flaming",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_DEBRIS_FLAMING));
	public static final RegistryObject<Block> RBMK_DEBRIS_SMOLDERING = BLOCKS.register("rbmk_debris_smoldering",
			() -> new MultiblockControllerBlock(machineProperties(50.0F, 600.0F).lightLevel(s -> 8),
					StructureType.RBMK_DEBRIS_SMOLDERING));
	public static final RegistryObject<Block> RBMK_DEBRIS_BLACKENED = BLOCKS.register("rbmk_debris_blackened",
			() -> new MultiblockControllerBlock(machineProperties(50.0F, 600.0F), StructureType.RBMK_DEBRIS_BLACKENED));

	public static final RegistryObject<Block> RED_COPPER_CABLE = BLOCKS.register("red_copper_cable",
			() -> new CableNetworkBlock(machineProperties(5.0F, 10.0F)));
	/** Full-block facade cable (1.7.10 {@code red_cable_paintable}) — not a thin pipe. */
	public static final RegistryObject<Block> PAINTABLE_RED_COPPER_CABLE = BLOCKS.register("paintable_red_copper_cable",
			() -> new EnergyNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> POWER_GAUGE = BLOCKS.register("power_gauge",
			() -> new EnergyFacingBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> RED_COPPER_BOXCABLE_1 = BLOCKS.register("red_copper_boxcable_1",
			() -> new BoxDuctBlock(machineProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.ENERGY, 0, null));
	public static final RegistryObject<Block> RED_COPPER_BOXCABLE_2 = BLOCKS.register("red_copper_boxcable_2",
			() -> new BoxDuctBlock(machineProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.ENERGY, 1, null));
	public static final RegistryObject<Block> RED_COPPER_BOXCABLE_3 = BLOCKS.register("red_copper_boxcable_3",
			() -> new BoxDuctBlock(machineProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.ENERGY, 2, null));
	public static final RegistryObject<Block> RED_COPPER_BOXCABLE_4 = BLOCKS.register("red_copper_boxcable_4",
			() -> new BoxDuctBlock(machineProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.ENERGY, 3, null));
	public static final RegistryObject<Block> RED_COPPER_BOXCABLE_5 = BLOCKS.register("red_copper_boxcable_5",
			() -> new BoxDuctBlock(machineProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.ENERGY, 4, null));
	public static final RegistryObject<Block> COATED_RED_COPPER_CABLE = BLOCKS.register("coated_red_copper_cable",
			() -> new EnergyNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> ELECTRICITY_CONNECTOR = BLOCKS.register("electricity_connector",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.CONNECTOR));
	public static final RegistryObject<Block> HEAVY_DUTY_ELECTRICITY_CONNECTOR = BLOCKS.register("heavy_duty_electricity_connector",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.CONNECTOR_SUPER));
	public static final RegistryObject<Block> ELECTRICITY_PYLON = BLOCKS.register("electricity_pylon",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.PYLON));
	public static final RegistryObject<Block> MEDIUM_WOODEN_ELECTRICITY_PYLON = BLOCKS.register("medium_wooden_electricity_pylon",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.MEDIUM_WOOD));
	public static final RegistryObject<Block> MEDIUM_WOODEN_ELECTRICITY_PYLON_TRANSFORMER = BLOCKS.register("medium_wooden_electricity_pylon_transformer",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.MEDIUM_WOOD_TRANSFORMER));
	public static final RegistryObject<Block> MEDIUM_STEEL_ELECTRICITY_PYLON = BLOCKS.register("medium_steel_electricity_pylon",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.MEDIUM_STEEL));
	public static final RegistryObject<Block> MEDIUM_STEEL_ELECTRICITY_PYLON_TRANSFORMER = BLOCKS.register("medium_steel_electricity_pylon_transformer",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.MEDIUM_STEEL_TRANSFORMER));
	public static final RegistryObject<Block> LARGE_ELECTRICITY_PYLON = BLOCKS.register("large_electricity_pylon",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.LARGE));
	public static final RegistryObject<Block> SUBSTATION = BLOCKS.register("substation",
			() -> new NetworkPylonBlock(machineProperties(5.0F, 10.0F), NetworkPylonKind.SUBSTATION));
	public static final RegistryObject<Block> POWER_SWITCH = BLOCKS.register("power_switch",
			() -> new EnergyNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> REDSTONE_POWER_SWITCH = BLOCKS.register("redstone_power_switch",
			() -> new EnergyNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> RED_COPPER_DIODE = BLOCKS.register("red_copper_diode",
			() -> new EnergyFacingBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> POWER_DETECTOR = BLOCKS.register("power_detector",
			() -> new EnergyNetworkNodeBlock(machineProperties(5.0F, 10.0F)));

	public static final RegistryObject<Block> UNIVERSAL_FLUID_DUCT_1 = BLOCKS.register("universal_fluid_duct_1",
			() -> new FluidNetworkBlock(pipeProperties(5.0F, 10.0F), FluidDuctMaterial.NEO));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_DUCT_2 = BLOCKS.register("universal_fluid_duct_2",
			() -> new FluidNetworkBlock(pipeProperties(5.0F, 10.0F), FluidDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_DUCT_3 = BLOCKS.register("universal_fluid_duct_3",
			() -> new FluidNetworkBlock(pipeProperties(5.0F, 10.0F), FluidDuctMaterial.COLORED));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_1 = BLOCKS.register("universal_fluid_boxduct_1",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 0, BoxDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_2 = BLOCKS.register("universal_fluid_boxduct_2",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 1, BoxDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_3 = BLOCKS.register("universal_fluid_boxduct_3",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 2, BoxDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_4 = BLOCKS.register("universal_fluid_boxduct_4",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 3, BoxDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_5 = BLOCKS.register("universal_fluid_boxduct_5",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 4, BoxDuctMaterial.SILVER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_6 = BLOCKS.register("universal_fluid_boxduct_6",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 0, BoxDuctMaterial.COPPER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_7 = BLOCKS.register("universal_fluid_boxduct_7",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 1, BoxDuctMaterial.COPPER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_8 = BLOCKS.register("universal_fluid_boxduct_8",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 2, BoxDuctMaterial.COPPER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_9 = BLOCKS.register("universal_fluid_boxduct_9",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 3, BoxDuctMaterial.COPPER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_10 = BLOCKS.register("universal_fluid_boxduct_10",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 4, BoxDuctMaterial.COPPER));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_11 = BLOCKS.register("universal_fluid_boxduct_11",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 0, BoxDuctMaterial.WHITE));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_12 = BLOCKS.register("universal_fluid_boxduct_12",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 1, BoxDuctMaterial.WHITE));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_13 = BLOCKS.register("universal_fluid_boxduct_13",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 2, BoxDuctMaterial.WHITE));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_14 = BLOCKS.register("universal_fluid_boxduct_14",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 3, BoxDuctMaterial.WHITE));
	public static final RegistryObject<Block> UNIVERSAL_FLUID_BOXDUCT_15 = BLOCKS.register("universal_fluid_boxduct_15",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 4, BoxDuctMaterial.WHITE));
	public static final RegistryObject<Block> EXHAUST_PIPE_1 = BLOCKS.register("exhaust_pipe_1",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 0, null));
	public static final RegistryObject<Block> EXHAUST_PIPE_2 = BLOCKS.register("exhaust_pipe_2",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 1, null));
	public static final RegistryObject<Block> EXHAUST_PIPE_3 = BLOCKS.register("exhaust_pipe_3",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 2, null));
	public static final RegistryObject<Block> EXHAUST_PIPE_4 = BLOCKS.register("exhaust_pipe_4",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 3, null));
	public static final RegistryObject<Block> EXHAUST_PIPE_5 = BLOCKS.register("exhaust_pipe_5",
			() -> new BoxDuctBlock(pipeProperties(5.0F, 10.0F), BoxDuctBlock.NetworkKind.FLUID, 4, null));
	public static final RegistryObject<Block> PAINTABLE_COATED_EXHAUST_PIPE = BLOCKS.register("paintable_coated_exhaust_pipe",
			() -> new FluidNetworkNodeBlock(machineProperties(5.0F, 10.0F), true));
	public static final RegistryObject<Block> PAINTABLE_COATED_UNIVERSAL_FLUID_DUCT = BLOCKS.register("paintable_coated_universal_fluid_duct",
			() -> new FluidNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> PIPE_ANCHOR = BLOCKS.register("pipe_anchor",
			() -> new PipeAnchorBlock(pipeProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FLOW_GAUGE = BLOCKS.register("flow_gauge",
			() -> new FluidFacingBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FLUID_VALVE = BLOCKS.register("fluid_valve",
			() -> new FluidNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FLUID_VALVE_COUNTER = BLOCKS.register("fluid_valve_counter",
			() -> new FluidNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> REDSTONE_FLUID_VALVE = BLOCKS.register("redstone_fluid_valve",
			() -> new FluidNetworkNodeBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FLOW_CONTROL_PUMP = BLOCKS.register("flow_control_pump",
			() -> new FluidFacingBlock(machineProperties(5.0F, 10.0F), FluidFacingBlock.ConnectMode.SIDES));
	public static final RegistryObject<Block> DRAINAGE_PIPE = BLOCKS.register("drainage_pipe",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.DRAINAGE_PIPE));
	public static final RegistryObject<Block> ROR_TRANSMITTER = BLOCKS.register("ror_transmitter",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> ROR_RECIEVER = BLOCKS.register("ror_reciever",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> ROR_ITEM_COUNTER = BLOCKS.register("ror_item_counter",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> ROR_LOGIC_RECIEVER = BLOCKS.register("ror_logic_reciever",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> ROR_READER = BLOCKS.register("ror_reader",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> ROR_CONTROLLER = BLOCKS.register("ror_controller",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> TELEX_MACHINE = BLOCKS.register("telex_machine",
			() -> new MultiblockControllerBlock(machineProperties(3.0F, 10.0F), StructureType.TELEX_MACHINE));
	public static final RegistryObject<Block> CONVEYOR_EJECTOR = BLOCKS.register("conveyor_ejector",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_INSERTER = BLOCKS.register("conveyor_inserter",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_GRABBER = BLOCKS.register("conveyor_grabber",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_SORTER = BLOCKS.register("conveyor_sorter",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_BOXER = BLOCKS.register("conveyor_boxer",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_UNBOXER = BLOCKS.register("conveyor_unboxer",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVEYOR_SPLITTER = BLOCKS.register("conveyor_splitter",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CONVEYOR_SPLITTER));
	public static final RegistryObject<Block> ACID_INPUT_PARTITIONER = BLOCKS.register("acid_input_partitioner",
			() -> new AcidInputPartitionerBlock(machineProperties(5.0F, 10.0F)));

	public static final RegistryObject<Block> TRANSPORT_DRONE_WAYPOINT = BLOCKS.register("transport_drone_waypoint",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> TRANSPORT_DRONE_CRATE = BLOCKS.register("transport_drone_crate",
			() -> new Block(machineProperties(0.1F, 10.0F)));
	public static final RegistryObject<Block> LOGISTICS_DRONE_WAYPOINT = BLOCKS.register("logistics_drone_waypoint",
			() -> new RttyBlock(rttyProperties()));
	public static final RegistryObject<Block> LOGISTICS_DRONE_DOCK = BLOCKS.register("logistics_drone_dock",
			() -> new Block(machineProperties(0.1F, 10.0F)));
	public static final RegistryObject<Block> LOGISTICS_PROVIDER_CRATE = BLOCKS.register("logistics_provider_crate",
			() -> new Block(machineProperties(0.1F, 10.0F)));
	public static final RegistryObject<Block> LOGISTICS_REQUESTER_CRATE = BLOCKS.register("logistics_requester_crate",
			() -> new Block(machineProperties(0.1F, 10.0F)));
	public static final RegistryObject<Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
			() -> new BoxDuctBlock(pipeProperties(2.0F, 10.0F), BoxDuctBlock.NetworkKind.PNEUMATIC, 3, null));
	public static final RegistryObject<Block> PAINTABLE_PNEUMATIC_TUBE = BLOCKS.register("paintable_pneumatic_tube",
			() -> new PneumaticNetworkNodeBlock(machineProperties(2.0F, 10.0F)));
	public static final RegistryObject<Block> FAN = BLOCKS.register("fan",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.FAN));
	public static final RegistryObject<Block> INSERTER = BLOCKS.register("inserter",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.INSERTER));
	public static final RegistryObject<Block> SAFE_BARREL = BLOCKS.register("safe_barrel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.SAFE_BARREL));
	public static final RegistryObject<Block> STEEL_BARREL = BLOCKS.register("steel_barrel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.STEEL_BARREL));
	public static final RegistryObject<Block> TECHNETIUM_STEEL_BARREL = BLOCKS.register("technetium_steel_barrel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.TECHNETIUM_STEEL_BARREL));
	public static final RegistryObject<Block> MAGNETIC_ANTIMATTER_CONTAINER = BLOCKS.register("magnetic_antimatter_container",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.MAGNETIC_ANTIMATTER_CONTAINER));
	public static final RegistryObject<Block> BATTERY_SOCKET = BLOCKS.register("battery_socket",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.BATTERY_SOCKET));
	public static final RegistryObject<Block> FENSU = BLOCKS.register("fensu",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.FENSU));
	public static final RegistryObject<Block> OLD_CAPACITOR = BLOCKS.register("old_capacitor",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.OLD_CAPACITOR));
	public static final RegistryObject<Block> CONVERTER_HE_RF = BLOCKS.register("converter_he_rf",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> CONVERTER_RF_HE = BLOCKS.register("converter_rf_he",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> ELECTRIC_FURNACE = BLOCKS.register("electric_furnace",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> MICROWAVE = BLOCKS.register("microwave",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.MICROWAVE));
	public static final RegistryObject<Block> ASSEMBLY_MACHINE = BLOCKS.register("assembly_machine",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ASSEMBLY_MACHINE));
	public static final RegistryObject<Block> ASSEMBLY_FACTORY = BLOCKS.register("assembly_factory",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.ASSEMBLY_FACTORY));
	public static final RegistryObject<Block> PRECASS = BLOCKS.register("precass",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.PRECASS));
	public static final RegistryObject<Block> CHEMICAL_PLANT = BLOCKS.register("chemical_plant",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CHEMICAL_PLANT));
	public static final RegistryObject<Block> CHEMICAL_FACTORY = BLOCKS.register("chemical_factory",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.CHEMICAL_FACTORY));
	public static final RegistryObject<Block> TEN_K_20HZ_TRANSFORMER = BLOCKS.register("10k20hztransformer",
			() -> new Block(machineProperties(5.0F, 10.0F)));

	public static final RegistryObject<Block> RBMK_FUEL_CHANNEL = BLOCKS.register("rbmk_fuel_channel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_FUEL_CHANNEL));
	public static final RegistryObject<Block> RBMK_MODERATED_FUEL_CHANNEL = BLOCKS.register("rbmk_moderated_fuel_channel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_MODERATED_FUEL_CHANNEL));
	public static final RegistryObject<Block> RBMK_FUEL_CHANNEL_REASIM = BLOCKS.register("rbmk_fuel_channel_reasim",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_FUEL_CHANNEL_REASIM));
	public static final RegistryObject<Block> RBMK_MODERATED_FUEL_CHANNEL_REASIM = BLOCKS.register("rbmk_moderated_fuel_channel_reasim",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_MODERATED_FUEL_CHANNEL_REASIM));
	public static final RegistryObject<Block> RBMK_CONTROL_RODS = BLOCKS.register("rbmk_control_rods",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CONTROL_RODS));
	public static final RegistryObject<Block> RBMK_MODERATED_CONTROL_RODS = BLOCKS.register("rbmk_moderated_control_rods",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_MODERATED_CONTROL_RODS));
	public static final RegistryObject<Block> RBMK_AUTOMATIC_CONTROL_RODS = BLOCKS.register("rbmk_automatic_control_rods",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_AUTOMATIC_CONTROL_RODS));
	public static final RegistryObject<Block> RBMK_CONTROL_RODS_REASIM = BLOCKS.register("rbmk_control_rods_reasim",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CONTROL_RODS_REASIM));
	public static final RegistryObject<Block> RBMK_AUTOMATIC_CONTROL_RODS_REASIM = BLOCKS.register("rbmk_automatic_control_rods_reasim",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_AUTOMATIC_CONTROL_RODS_REASIM));
	public static final RegistryObject<Block> RBMK_STRUCTURAL_COLUMN = BLOCKS.register("rbmk_structural_column",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_STRUCTURAL_COLUMN));
	public static final RegistryObject<Block> RBMK_STEAM_CHANNEL = BLOCKS.register("rbmk_steam_channel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_STEAM_CHANNEL));
	public static final RegistryObject<Block> RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR = BLOCKS.register("rbmk_tungsten_carbide_neutron_reflector",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR));
	public static final RegistryObject<Block> RBMK_BORON_NEUTRON_ABSORBER = BLOCKS.register("rbmk_boron_neutron_absorber",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_BORON_NEUTRON_ABSORBER));
	public static final RegistryObject<Block> RBMK_GRAPHITE_MODERATOR = BLOCKS.register("rbmk_graphite_moderator",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_GRAPHITE_MODERATOR));
	public static final RegistryObject<Block> RBMK_IRRADIATION_CHANNEL = BLOCKS.register("rbmk_irradiation_channel",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_IRRADIATION_CHANNEL));
	public static final RegistryObject<Block> RBMK_STORAGE_COLUMN = BLOCKS.register("rbmk_storage_column",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_STORAGE_COLUMN));
	public static final RegistryObject<Block> RBMK_COOLER = BLOCKS.register("rbmk_cooler",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_COOLER));
	public static final RegistryObject<Block> RBMK_FLUID_HEATER = BLOCKS.register("rbmk_fluid_heater",
			() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_FLUID_HEATER));
	public static final RegistryObject<Block> RBMK_STEAM_CONNECTOR = BLOCKS.register("rbmk_steam_connector",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> RBMK_REASIM_WATER_INLET = BLOCKS.register("rbmk_reasim_water_inlet",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> RBMK_REASIM_STEAM_OUTLET = BLOCKS.register("rbmk_reasim_steam_outlet",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> BLANK_ROR_PANEL = BLOCKS.register("blank_ror_panel",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> RBMK_DISPLAY_PANEL = BLOCKS.register("rbmk_display_panel",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_KEYPAD = BLOCKS.register("ror_keypad",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_LEVER = BLOCKS.register("ror_lever",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_GUAGE = BLOCKS.register("ror_guage",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_INDICATOR_LIGHTS = BLOCKS.register("ror_indicator_lights",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_NUMERIC_DISPLAY = BLOCKS.register("ror_numeric_display",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> ROR_GRAPH = BLOCKS.register("ror_graph",
			() -> new RoRPanelBlock(machineProperties(3.0F, 30.0F)));
	public static final RegistryObject<Block> DENSE_SUPERCONDUCTING_COIL = BLOCKS.register("dense_superconducting_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_GOLD_COIL = BLOCKS.register("dense_gold_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_NEODYMIUM_COIL = BLOCKS.register("dense_neodymium_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_4000K_SUPERCONDUCTOR_COIL = BLOCKS.register("dense_4000k_superconductor_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_SCHRABIDIC_COIL = BLOCKS.register("dense_schrabidic_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_SCHRABIDATE_COIL = BLOCKS.register("dense_schrabidate_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_STARMETAL_COIL = BLOCKS.register("dense_starmetal_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_CHLOROPHYTE_COIL = BLOCKS.register("dense_chlorophyte_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> DENSE_MESE_COIL = BLOCKS.register("dense_mese_coil",
			() -> new Block(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> BLAST_FURNACE_EXTENSION = BLOCKS.register("blast_furnace_extension",
			() -> new BlastFurnaceExtensionBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> BRICKED_FURNACE = BLOCKS.register("bricked_furnace",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> NUCLEAR_BLAST_FURNACE = BLOCKS.register("nuclear_blast_furnace",
			() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));

	public static final RegistryObject<Block> SHALLOW_FOUNDRY_BASIN = BLOCKS.register("shallow_foundry_basin",
			() -> new FoundryBasinBlock(machineProperties(5.0F, 10.0F), FoundryBasinBlock.SHAPE_SHALLOW));
	public static final RegistryObject<Block> FOUNDRY_BASIN = BLOCKS.register("foundry_basin",
			() -> new FoundryBasinBlock(machineProperties(5.0F, 10.0F), FoundryBasinBlock.SHAPE_FULL));
	public static final RegistryObject<Block> FOUNDRY_CHANNEL = BLOCKS.register("foundry_channel",
			() -> new FoundryChannelBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FOUNDRY_STORAGE_BASIN = BLOCKS.register("foundry_storage_basin",
			() -> new FoundryStorageBasinBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FOUNDRY_OUTLET = BLOCKS.register("foundry_outlet",
			() -> new FoundryOutletBlock(machineProperties(5.0F, 10.0F)));
	public static final RegistryObject<Block> FOUNDRY_SPILL_OUTLET = BLOCKS.register("foundry_spill_outlet",
			() -> new FoundryOutletBlock(machineProperties(5.0F, 10.0F)));

	private static BlockBehaviour.Properties anvilProperties() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.strength(5.0F, 100.0F)
				.sound(SoundType.ANVIL)
				.noOcclusion()
				.requiresCorrectToolForDrops();
	}

	private static BlockBehaviour.Properties fenceProperties() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.strength(15.0F, 0.25F)
				.sound(SoundType.METAL)
				.noOcclusion()
				.requiresCorrectToolForDrops();
	}

	private static BlockBehaviour.Properties sandProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.strength(0.5F)
				.sound(SoundType.SAND);
	}

	private static BlockBehaviour.Properties glassProperties(float hardness, int light) {
		BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
				.mapColor(MapColor.NONE)
				.strength(hardness)
				.sound(SoundType.GLASS)
				.noOcclusion()
				.isValidSpawn((state, level, pos, type) -> false)
				.isRedstoneConductor((state, level, pos) -> false)
				.isSuffocating((state, level, pos) -> false)
				.isViewBlocking((state, level, pos) -> false);
		if (light > 0) {
			props = props.lightLevel(state -> light);
		}
		return props;
	}

	/** Like {@link #machineProperties} but with 1.7.10 {@code ModSoundTypes.pipe}. */
	private static BlockBehaviour.Properties pipeProperties(float hardness, float resistance) {
		return machineProperties(hardness, resistance).sound(ModSoundTypes.PIPE);
	}

	private static BlockBehaviour.Properties rttyProperties() {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.NONE)
				.strength(0.1F, 10.0F)
				.sound(SoundType.WOOD)
				.noOcclusion()
				.noCollission()
				.instabreak()
				.isSuffocating((state, level, pos) -> false)
				.isViewBlocking((state, level, pos) -> false);
	}

	private static BlockBehaviour.Properties machineProperties(float hardness, float resistance) {
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.strength(hardness, resistance)
				.sound(SoundType.METAL)
				.noOcclusion()
				.dynamicShape()
				.isSuffocating((state, level, pos) -> false)
				.isViewBlocking((state, level, pos) -> false)
				.requiresCorrectToolForDrops();
	}

	private ModBlocks() {
	}
}
