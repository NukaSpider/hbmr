package com.hbmr.registry;

import com.hbmr.HBMR;
import com.hbmr.item.CableNeoBlockItem;
import com.hbmr.item.PipeAnchorBlockItem;
import com.hbmr.item.AcidInputPartitionerBlockItem;
import com.hbmr.item.PipeNeoBlockItem;
import com.hbmr.item.MultiblockBlockItem;
import com.hbmr.item.NetworkPylonBlockItem;
import com.hbmr.item.RoRBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, HBMR.MODID);

	public static final RegistryObject<Item> SIREN = blockItem("siren", ModBlocks.SIREN);
	public static final RegistryObject<Item> CORRUPTED_BROADCASTER = blockItem("corrupted_broadcaster", ModBlocks.CORRUPTED_BROADCASTER);
	public static final RegistryObject<Item> GEIGER_COUNTER = blockItem("geiger_counter", ModBlocks.GEIGER_COUNTER);
	public static final RegistryObject<Item> SUIT_BATTERY = blockItem("suit_battery", ModBlocks.SUIT_BATTERY);
	public static final RegistryObject<Item> CHAINLINK_FENCE = blockItem("chainlink_fence", ModBlocks.CHAINLINK_FENCE);
	public static final RegistryObject<Item> CHAINLINK_FENCE_POST = blockItem("chainlink_fence_post", ModBlocks.CHAINLINK_FENCE_POST);
	public static final RegistryObject<Item> ASH = blockItem("ash", ModBlocks.ASH);
	public static final RegistryObject<Item> BORON_SAND = blockItem("boron_sand", ModBlocks.BORON_SAND);
	public static final RegistryObject<Item> LEAD_SAND = blockItem("lead_sand", ModBlocks.LEAD_SAND);

	public static final RegistryObject<Item> URANIUM_SAND = blockItem("uranium_sand", ModBlocks.URANIUM_SAND);
	public static final RegistryObject<Item> POLONIUM_SAND = blockItem("polonium_sand", ModBlocks.POLONIUM_SAND);
	public static final RegistryObject<Item> QUARTZ_SAND = blockItem("quartz_sand", ModBlocks.QUARTZ_SAND);
	public static final RegistryObject<Item> BORON_GLASS = blockItem("boron_glass", ModBlocks.BORON_GLASS);
	public static final RegistryObject<Item> LEAD_GLASS = blockItem("lead_glass", ModBlocks.LEAD_GLASS);
	public static final RegistryObject<Item> URANIUM_GLASS = blockItem("uranium_glass", ModBlocks.URANIUM_GLASS);
	public static final RegistryObject<Item> TRINITY_GLASS = blockItem("trinity_glass", ModBlocks.TRINITY_GLASS);
	public static final RegistryObject<Item> POLONIUM_GLASS = blockItem("polonium_glass", ModBlocks.POLONIUM_GLASS);
	public static final RegistryObject<Item> ASH_GLASS = blockItem("ash_glass", ModBlocks.ASH_GLASS);
	public static final RegistryObject<Item> POLARIZED_GLASS = blockItem("polarized_glass", ModBlocks.POLARIZED_GLASS);
	public static final RegistryObject<Item> SILO_HATCH_FRAME = blockItem("silo_hatch_frame", ModBlocks.SILO_HATCH_FRAME);
	public static final RegistryObject<Item> SILO_HATCH_OPENER = blockItem("silo_hatch_opener", ModBlocks.SILO_HATCH_OPENER);
	public static final RegistryObject<Item> CARGO_ELEVATOR = multiblockItem("cargo_elevator", ModBlocks.CARGO_ELEVATOR);
	public static final RegistryObject<Item> VT_BLAST_DOOR = multiblockItem("vt_blast_door", ModBlocks.VT_BLAST_DOOR);
	public static final RegistryObject<Item> SLIDING_BLAST_DOOR = multiblockItem("sliding_blast_door", ModBlocks.SLIDING_BLAST_DOOR);
	public static final RegistryObject<Item> SLIDING_BLAST_DOOR_SHORT = multiblockItem("sliding_blast_door_short", ModBlocks.SLIDING_BLAST_DOOR_SHORT);
	public static final RegistryObject<Item> FIRE_DOOR = multiblockItem("fire_door", ModBlocks.FIRE_DOOR);
	/** No OBJ — flat generated icon from {@code textures/item/transition_seal.png}. */
	public static final RegistryObject<Item> TRANSITION_SEAL = blockItem("transition_seal", ModBlocks.TRANSITION_SEAL);
	public static final RegistryObject<Item> SILO_HATCH = multiblockItem("silo_hatch", ModBlocks.SILO_HATCH);
	public static final RegistryObject<Item> SILO_HATCH_LARGE = multiblockItem("silo_hatch_large", ModBlocks.SILO_HATCH_LARGE);
	public static final RegistryObject<Item> SECURE_ACCESS_DOOR = multiblockItem("secure_access_door", ModBlocks.SECURE_ACCESS_DOOR);
	public static final RegistryObject<Item> LARGE_VEHICLE_DOOR = multiblockItem("large_vehicle_door", ModBlocks.LARGE_VEHICLE_DOOR);
	public static final RegistryObject<Item> QE_CONTAINMENT_DOOR = multiblockItem("qe_containment_door", ModBlocks.QE_CONTAINMENT_DOOR);
	public static final RegistryObject<Item> QE_SLIDING_DOOR = multiblockItem("qe_sliding_door", ModBlocks.QE_SLIDING_DOOR);
	public static final RegistryObject<Item> ROUND_AIRLOCK_DOOR = multiblockItem("round_airlock_door", ModBlocks.ROUND_AIRLOCK_DOOR);
	public static final RegistryObject<Item> SLIDING_SEAL_DOOR = multiblockItem("sliding_seal_door", ModBlocks.SLIDING_SEAL_DOOR);
	public static final RegistryObject<Item> WATER_DOOR = multiblockItem("water_door", ModBlocks.WATER_DOOR);
	public static final RegistryObject<Item> IRON_CRATE = blockItem("iron_crate", ModBlocks.IRON_CRATE);
	public static final RegistryObject<Item> STEEL_CRATE = blockItem("steel_crate", ModBlocks.STEEL_CRATE);
	public static final RegistryObject<Item> DESH_CRATE = blockItem("desh_crate", ModBlocks.DESH_CRATE);
	public static final RegistryObject<Item> TUNGSTEN_CRATE = blockItem("tungsten_crate", ModBlocks.TUNGSTEN_CRATE);
	public static final RegistryObject<Item> TEMPLATE_CRATE = blockItem("template_crate", ModBlocks.TEMPLATE_CRATE);
	public static final RegistryObject<Item> SAFE = blockItem("safe", ModBlocks.SAFE);
	public static final RegistryObject<Item> MASS_STORAGE_1 = blockItem("mass_storage_1", ModBlocks.MASS_STORAGE_1);
	public static final RegistryObject<Item> MASS_STORAGE_2 = blockItem("mass_storage_2", ModBlocks.MASS_STORAGE_2);
	public static final RegistryObject<Item> MASS_STORAGE_3 = blockItem("mass_storage_3", ModBlocks.MASS_STORAGE_3);
	public static final RegistryObject<Item> MASS_STORAGE_4 = blockItem("mass_storage_4", ModBlocks.MASS_STORAGE_4);

	public static final RegistryObject<Item> AUTOMATIC_CRAFTING_TABLE = blockItem("automatic_crafting_table", ModBlocks.AUTOMATIC_CRAFTING_TABLE);
	public static final RegistryObject<Item> COMBINATOR_FUNNEL = blockItem("combinator_funnel", ModBlocks.COMBINATOR_FUNNEL);
	public static final RegistryObject<Item> IRON_ANVIL = blockItem("iron_anvil", ModBlocks.IRON_ANVIL);
	public static final RegistryObject<Item> LEAD_ANVIL = blockItem("lead_anvil", ModBlocks.LEAD_ANVIL);
	public static final RegistryObject<Item> STEEL_ANVIL = blockItem("steel_anvil", ModBlocks.STEEL_ANVIL);
	public static final RegistryObject<Item> DESH_ANVIL = blockItem("desh_anvil", ModBlocks.DESH_ANVIL);
	public static final RegistryObject<Item> FERROURANIUM_ANVIL = blockItem("ferrouranium_anvil", ModBlocks.FERROURANIUM_ANVIL);
	public static final RegistryObject<Item> SATURNITE_ANVIL = blockItem("saturnite_anvil", ModBlocks.SATURNITE_ANVIL);
	public static final RegistryObject<Item> BISMUTH_BRONZE_ANVIL = blockItem("bismuth_bronze_anvil", ModBlocks.BISMUTH_BRONZE_ANVIL);
	public static final RegistryObject<Item> ARSENIC_BRONZE_ANVIL = blockItem("arsenic_bronze_anvil", ModBlocks.ARSENIC_BRONZE_ANVIL);
	public static final RegistryObject<Item> SCHRABIDATE_ANVIL = blockItem("schrabidate_anvil", ModBlocks.SCHRABIDATE_ANVIL);
	public static final RegistryObject<Item> DNT_ANVIL = blockItem("dnt_anvil", ModBlocks.DNT_ANVIL);
	public static final RegistryObject<Item> OSMIRIDIUM_ANVIL = blockItem("osmiridium_anvil", ModBlocks.OSMIRIDIUM_ANVIL);
	public static final RegistryObject<Item> MURKY_ANVIL = blockItem("murky_anvil", ModBlocks.MURKY_ANVIL);
	public static final RegistryObject<Item> BURNER_PRESS_PREHEATER = blockItem("burner_press_preheater", ModBlocks.BURNER_PRESS_PREHEATER);
	public static final RegistryObject<Item> BURNER_PRESS = multiblockItem("burner_press", ModBlocks.BURNER_PRESS);
	public static final RegistryObject<Item> ELECTRIC_PRESS = multiblockItem("electric_press", ModBlocks.ELECTRIC_PRESS);

	public static final RegistryObject<Item> HEAT_EXCHANGING_HEATER = multiblockItem("heat_exchanging_heater", ModBlocks.HEAT_EXCHANGING_HEATER);
	public static final RegistryObject<Item> IRON_FURNACE = multiblockItem("iron_furnace", ModBlocks.IRON_FURNACE);
	public static final RegistryObject<Item> STEEL_FURNACE = multiblockItem("steel_furnace", ModBlocks.STEEL_FURNACE);
	public static final RegistryObject<Item> COMBINATION_OVEN = multiblockItem("combination_oven", ModBlocks.COMBINATION_OVEN);
	public static final RegistryObject<Item> STIRLING_ENGINE = multiblockItem("stirling_engine", ModBlocks.STIRLING_ENGINE);
	public static final RegistryObject<Item> HEAVY_STIRLING_ENGINE = multiblockItem("heavy_stirling_engine", ModBlocks.HEAVY_STIRLING_ENGINE);
	public static final RegistryObject<Item> CREATIVE_STIRLING_ENGINE = multiblockItem("creative_stirling_engine", ModBlocks.CREATIVE_STIRLING_ENGINE);
	public static final RegistryObject<Item> STIRLING_SAWMILL = multiblockItem("stirling_sawmill", ModBlocks.STIRLING_SAWMILL);
	public static final RegistryObject<Item> CRUCIBLE = multiblockItem("crucible", ModBlocks.CRUCIBLE);
	public static final RegistryObject<Item> STRAND_CASTER = multiblockItem("strand_caster", ModBlocks.STRAND_CASTER);
	public static final RegistryObject<Item> BOILER = multiblockItem("boiler", ModBlocks.BOILER);
	public static final RegistryObject<Item> INDUSTRIAL_BOILER = multiblockItem("industrial_boiler", ModBlocks.INDUSTRIAL_BOILER);
	public static final RegistryObject<Item> BLAST_FURNACE = blockItem("blast_furnace", ModBlocks.BLAST_FURNACE);
	public static final RegistryObject<Item> CENTRIFUGE = multiblockItem("centrifuge", ModBlocks.CENTRIFUGE);
	public static final RegistryObject<Item> GAS_CENTRIFUGE = multiblockItem("gas_centrifuge", ModBlocks.GAS_CENTRIFUGE);
	public static final RegistryObject<Item> FEL = multiblockItem("fel", ModBlocks.FEL);
	public static final RegistryObject<Item> SILEX = multiblockItem("silex", ModBlocks.SILEX);
	public static final RegistryObject<Item> ROTARY_FURNACE = multiblockItem("rotary_furnace", ModBlocks.ROTARY_FURNACE);
	public static final RegistryObject<Item> ORE_ACIDIZER = multiblockItem("ore_acidizer", ModBlocks.ORE_ACIDIZER);
	public static final RegistryObject<Item> BREEDING_REACTOR = multiblockItem("breeding_reactor", ModBlocks.BREEDING_REACTOR);
	public static final RegistryObject<Item> WOOD_BURNING_GENERATOR = multiblockItem("wood_burning_generator", ModBlocks.WOOD_BURNING_GENERATOR);
	public static final RegistryObject<Item> DIESEL_GENERATOR = multiblockItem("diesel_generator", ModBlocks.DIESEL_GENERATOR);
	public static final RegistryObject<Item> INDUSTRIAL_COMBUSTION_ENGINE = multiblockItem("industrial_combustion_engine", ModBlocks.INDUSTRIAL_COMBUSTION_ENGINE);
	public static final RegistryObject<Item> RESEARCH_REACTOR = multiblockItem("research_reactor", ModBlocks.RESEARCH_REACTOR);
	public static final RegistryObject<Item> ZIRNOX_NUCLEAR_REACTOR = multiblockItem("zirnox_nuclear_reactor", ModBlocks.ZIRNOX_NUCLEAR_REACTOR);
	public static final RegistryObject<Item> INDUSTRIAL_GENERATOR = multiblockItem("industrial_generator", ModBlocks.INDUSTRIAL_GENERATOR);
	public static final RegistryObject<Item> RADIATION_POWERED_ENGINE = multiblockItem("radiation_powered_engine", ModBlocks.RADIATION_POWERED_ENGINE);
	public static final RegistryObject<Item> CYCLOTRON = multiblockItem("cyclotron", ModBlocks.CYCLOTRON);
	public static final RegistryObject<Item> EXPOSURE_CHAMBER = multiblockItem("exposure_chamber", ModBlocks.EXPOSURE_CHAMBER);
	public static final RegistryObject<Item> RT_GENERATOR = multiblockItem("rt_generator", ModBlocks.RT_GENERATOR);
	public static final RegistryObject<Item> RTGRC = multiblockItem("rtgrc", ModBlocks.RTGRC);
	public static final RegistryObject<Item> GEOTHERMAL_HEAT_EXCHANGER = multiblockItem("geothermal_heat_exchanger", ModBlocks.GEOTHERMAL_HEAT_EXCHANGER);
	public static final RegistryObject<Item> PARTICLE_SOURCE = multiblockItem("particle_source", ModBlocks.PARTICLE_SOURCE);
	public static final RegistryObject<Item> BEAMLINE = multiblockItem("beamline", ModBlocks.BEAMLINE);
	public static final RegistryObject<Item> RF_CAVITY = multiblockItem("rf_cavity", ModBlocks.RF_CAVITY);
	public static final RegistryObject<Item> QUADRUPOLE_MAGNETS = multiblockItem("quadrupole_magnets", ModBlocks.QUADRUPOLE_MAGNETS);
	public static final RegistryObject<Item> DIPOLE_MAGNETS = multiblockItem("dipole_magnets", ModBlocks.DIPOLE_MAGNETS);
	public static final RegistryObject<Item> PARTICLE_DETECTOR = multiblockItem("particle_detector", ModBlocks.PARTICLE_DETECTOR);
	public static final RegistryObject<Item> RBMK_CONSOLE = multiblockItem("rbmk_console", ModBlocks.RBMK_CONSOLE);
	public static final RegistryObject<Item> RBMK_CRANE_CONSOLE = multiblockItem("rbmk_crane_console", ModBlocks.RBMK_CRANE_CONSOLE);
	public static final RegistryObject<Item> RBMK_AUTOLOADER = multiblockItem("rbmk_autoloader", ModBlocks.RBMK_AUTOLOADER);
	public static final RegistryObject<Item> RBMK_DEBRIS = multiblockItem("rbmk_debris", ModBlocks.RBMK_DEBRIS);
	public static final RegistryObject<Item> RBMK_DEBRIS_FLAMING = multiblockItem("rbmk_debris_flaming", ModBlocks.RBMK_DEBRIS_FLAMING);
	public static final RegistryObject<Item> RBMK_DEBRIS_SMOLDERING = multiblockItem("rbmk_debris_smoldering", ModBlocks.RBMK_DEBRIS_SMOLDERING);
	public static final RegistryObject<Item> RBMK_DEBRIS_BLACKENED = multiblockItem("rbmk_debris_blackened", ModBlocks.RBMK_DEBRIS_BLACKENED);

	public static final RegistryObject<Item> RED_COPPER_CABLE = ITEMS.register("red_copper_cable",
			() -> new CableNeoBlockItem(ModBlocks.RED_COPPER_CABLE.get(), new Item.Properties()));
	public static final RegistryObject<Item> PAINTABLE_RED_COPPER_CABLE = blockItem("paintable_red_copper_cable", ModBlocks.PAINTABLE_RED_COPPER_CABLE);
	public static final RegistryObject<Item> POWER_GAUGE = blockItem("power_gauge", ModBlocks.POWER_GAUGE);
	public static final RegistryObject<Item> RED_COPPER_BOXCABLE_1 = blockItem("red_copper_boxcable_1", ModBlocks.RED_COPPER_BOXCABLE_1);
	public static final RegistryObject<Item> RED_COPPER_BOXCABLE_2 = blockItem("red_copper_boxcable_2", ModBlocks.RED_COPPER_BOXCABLE_2);
	public static final RegistryObject<Item> RED_COPPER_BOXCABLE_3 = blockItem("red_copper_boxcable_3", ModBlocks.RED_COPPER_BOXCABLE_3);
	public static final RegistryObject<Item> RED_COPPER_BOXCABLE_4 = blockItem("red_copper_boxcable_4", ModBlocks.RED_COPPER_BOXCABLE_4);
	public static final RegistryObject<Item> RED_COPPER_BOXCABLE_5 = blockItem("red_copper_boxcable_5", ModBlocks.RED_COPPER_BOXCABLE_5);
	public static final RegistryObject<Item> COATED_RED_COPPER_CABLE = blockItem("coated_red_copper_cable", ModBlocks.COATED_RED_COPPER_CABLE);
	public static final RegistryObject<Item> ELECTRICITY_CONNECTOR = ITEMS.register("electricity_connector",
			() -> new NetworkPylonBlockItem(ModBlocks.ELECTRICITY_CONNECTOR.get(), new Item.Properties()));
	public static final RegistryObject<Item> HEAVY_DUTY_ELECTRICITY_CONNECTOR = ITEMS.register("heavy_duty_electricity_connector",
			() -> new NetworkPylonBlockItem(ModBlocks.HEAVY_DUTY_ELECTRICITY_CONNECTOR.get(), new Item.Properties()));
	public static final RegistryObject<Item> ELECTRICITY_PYLON = ITEMS.register("electricity_pylon",
			() -> new NetworkPylonBlockItem(ModBlocks.ELECTRICITY_PYLON.get(), new Item.Properties()));
	public static final RegistryObject<Item> MEDIUM_WOODEN_ELECTRICITY_PYLON = ITEMS.register("medium_wooden_electricity_pylon",
			() -> new NetworkPylonBlockItem(ModBlocks.MEDIUM_WOODEN_ELECTRICITY_PYLON.get(), new Item.Properties()));
	public static final RegistryObject<Item> MEDIUM_WOODEN_ELECTRICITY_PYLON_TRANSFORMER = ITEMS.register("medium_wooden_electricity_pylon_transformer",
			() -> new NetworkPylonBlockItem(ModBlocks.MEDIUM_WOODEN_ELECTRICITY_PYLON_TRANSFORMER.get(), new Item.Properties()));
	public static final RegistryObject<Item> MEDIUM_STEEL_ELECTRICITY_PYLON = ITEMS.register("medium_steel_electricity_pylon",
			() -> new NetworkPylonBlockItem(ModBlocks.MEDIUM_STEEL_ELECTRICITY_PYLON.get(), new Item.Properties()));
	public static final RegistryObject<Item> MEDIUM_STEEL_ELECTRICITY_PYLON_TRANSFORMER = ITEMS.register("medium_steel_electricity_pylon_transformer",
			() -> new NetworkPylonBlockItem(ModBlocks.MEDIUM_STEEL_ELECTRICITY_PYLON_TRANSFORMER.get(), new Item.Properties()));
	public static final RegistryObject<Item> LARGE_ELECTRICITY_PYLON = ITEMS.register("large_electricity_pylon",
			() -> new NetworkPylonBlockItem(ModBlocks.LARGE_ELECTRICITY_PYLON.get(), new Item.Properties()));
	public static final RegistryObject<Item> SUBSTATION =
			ITEMS.register("substation",
					() -> new NetworkPylonBlockItem(ModBlocks.SUBSTATION.get(), new Item.Properties()));
	public static final RegistryObject<Item> POWER_SWITCH = blockItem("power_switch", ModBlocks.POWER_SWITCH);
	public static final RegistryObject<Item> REDSTONE_POWER_SWITCH = blockItem("redstone_power_switch", ModBlocks.REDSTONE_POWER_SWITCH);
	public static final RegistryObject<Item> RED_COPPER_DIODE = blockItem("red_copper_diode", ModBlocks.RED_COPPER_DIODE);
	public static final RegistryObject<Item> POWER_DETECTOR = blockItem("power_detector", ModBlocks.POWER_DETECTOR);

	public static final RegistryObject<Item> UNIVERSAL_FLUID_DUCT_1 = ITEMS.register("universal_fluid_duct_1",
			() -> new PipeNeoBlockItem(ModBlocks.UNIVERSAL_FLUID_DUCT_1.get(), new Item.Properties()));
	public static final RegistryObject<Item> UNIVERSAL_FLUID_DUCT_2 = ITEMS.register("universal_fluid_duct_2",
			() -> new PipeNeoBlockItem(ModBlocks.UNIVERSAL_FLUID_DUCT_2.get(), new Item.Properties()));
	public static final RegistryObject<Item> UNIVERSAL_FLUID_DUCT_3 = ITEMS.register("universal_fluid_duct_3",
			() -> new PipeNeoBlockItem(ModBlocks.UNIVERSAL_FLUID_DUCT_3.get(), new Item.Properties()));
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_1 = blockItem("universal_fluid_boxduct_1", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_1);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_2 = blockItem("universal_fluid_boxduct_2", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_2);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_3 = blockItem("universal_fluid_boxduct_3", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_3);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_4 = blockItem("universal_fluid_boxduct_4", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_4);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_5 = blockItem("universal_fluid_boxduct_5", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_5);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_6 = blockItem("universal_fluid_boxduct_6", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_6);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_7 = blockItem("universal_fluid_boxduct_7", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_7);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_8 = blockItem("universal_fluid_boxduct_8", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_8);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_9 = blockItem("universal_fluid_boxduct_9", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_9);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_10 = blockItem("universal_fluid_boxduct_10", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_10);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_11 = blockItem("universal_fluid_boxduct_11", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_11);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_12 = blockItem("universal_fluid_boxduct_12", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_12);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_13 = blockItem("universal_fluid_boxduct_13", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_13);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_14 = blockItem("universal_fluid_boxduct_14", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_14);
	public static final RegistryObject<Item> UNIVERSAL_FLUID_BOXDUCT_15 = blockItem("universal_fluid_boxduct_15", ModBlocks.UNIVERSAL_FLUID_BOXDUCT_15);
	public static final RegistryObject<Item> EXHAUST_PIPE_1 = blockItem("exhaust_pipe_1", ModBlocks.EXHAUST_PIPE_1);
	public static final RegistryObject<Item> EXHAUST_PIPE_2 = blockItem("exhaust_pipe_2", ModBlocks.EXHAUST_PIPE_2);
	public static final RegistryObject<Item> EXHAUST_PIPE_3 = blockItem("exhaust_pipe_3", ModBlocks.EXHAUST_PIPE_3);
	public static final RegistryObject<Item> EXHAUST_PIPE_4 = blockItem("exhaust_pipe_4", ModBlocks.EXHAUST_PIPE_4);
	public static final RegistryObject<Item> EXHAUST_PIPE_5 = blockItem("exhaust_pipe_5", ModBlocks.EXHAUST_PIPE_5);
	public static final RegistryObject<Item> PAINTABLE_COATED_EXHAUST_PIPE = blockItem("paintable_coated_exhaust_pipe", ModBlocks.PAINTABLE_COATED_EXHAUST_PIPE);
	public static final RegistryObject<Item> PAINTABLE_COATED_UNIVERSAL_FLUID_DUCT = blockItem("paintable_coated_universal_fluid_duct", ModBlocks.PAINTABLE_COATED_UNIVERSAL_FLUID_DUCT);
	public static final RegistryObject<Item> PIPE_ANCHOR = ITEMS.register("pipe_anchor",
			() -> new PipeAnchorBlockItem(ModBlocks.PIPE_ANCHOR.get(), new Item.Properties()));
	public static final RegistryObject<Item> FLOW_GAUGE = blockItem("flow_gauge", ModBlocks.FLOW_GAUGE);
	public static final RegistryObject<Item> FLUID_VALVE = blockItem("fluid_valve", ModBlocks.FLUID_VALVE);
	public static final RegistryObject<Item> FLUID_VALVE_COUNTER = blockItem("fluid_valve_counter", ModBlocks.FLUID_VALVE_COUNTER);
	public static final RegistryObject<Item> REDSTONE_FLUID_VALVE = blockItem("redstone_fluid_valve", ModBlocks.REDSTONE_FLUID_VALVE);
	public static final RegistryObject<Item> FLOW_CONTROL_PUMP = blockItem("flow_control_pump", ModBlocks.FLOW_CONTROL_PUMP);
	public static final RegistryObject<Item> DRAINAGE_PIPE = multiblockItem("drainage_pipe", ModBlocks.DRAINAGE_PIPE);
	public static final RegistryObject<Item> ROR_TRANSMITTER = blockItem("ror_transmitter", ModBlocks.ROR_TRANSMITTER);
	public static final RegistryObject<Item> ROR_RECIEVER = blockItem("ror_reciever", ModBlocks.ROR_RECIEVER);
	public static final RegistryObject<Item> ROR_ITEM_COUNTER = blockItem("ror_item_counter", ModBlocks.ROR_ITEM_COUNTER);
	public static final RegistryObject<Item> ROR_LOGIC_RECIEVER = blockItem("ror_logic_reciever", ModBlocks.ROR_LOGIC_RECIEVER);
	public static final RegistryObject<Item> ROR_READER = blockItem("ror_reader", ModBlocks.ROR_READER);
	public static final RegistryObject<Item> ROR_CONTROLLER = blockItem("ror_controller", ModBlocks.ROR_CONTROLLER);
	public static final RegistryObject<Item> TELEX_MACHINE = multiblockItem("telex_machine", ModBlocks.TELEX_MACHINE);
	public static final RegistryObject<Item> CONVEYOR_EJECTOR = blockItem("conveyor_ejector", ModBlocks.CONVEYOR_EJECTOR);
	public static final RegistryObject<Item> CONVEYOR_INSERTER = blockItem("conveyor_inserter", ModBlocks.CONVEYOR_INSERTER);
	public static final RegistryObject<Item> CONVEYOR_GRABBER = blockItem("conveyor_grabber", ModBlocks.CONVEYOR_GRABBER);
	public static final RegistryObject<Item> CONVEYOR_SORTER = blockItem("conveyor_sorter", ModBlocks.CONVEYOR_SORTER);
	public static final RegistryObject<Item> CONVEYOR_BOXER = blockItem("conveyor_boxer", ModBlocks.CONVEYOR_BOXER);
	public static final RegistryObject<Item> CONVEYOR_UNBOXER = blockItem("conveyor_unboxer", ModBlocks.CONVEYOR_UNBOXER);
	public static final RegistryObject<Item> CONVEYOR_SPLITTER = multiblockItem("conveyor_splitter", ModBlocks.CONVEYOR_SPLITTER);
	public static final RegistryObject<Item> ACID_INPUT_PARTITIONER = ITEMS.register("acid_input_partitioner",
			() -> new AcidInputPartitionerBlockItem(ModBlocks.ACID_INPUT_PARTITIONER.get(), new Item.Properties()));

	public static final RegistryObject<Item> TRANSPORT_DRONE_WAYPOINT = blockItem("transport_drone_waypoint", ModBlocks.TRANSPORT_DRONE_WAYPOINT);
	public static final RegistryObject<Item> TRANSPORT_DRONE_CRATE = blockItem("transport_drone_crate", ModBlocks.TRANSPORT_DRONE_CRATE);
	public static final RegistryObject<Item> LOGISTICS_DRONE_WAYPOINT = blockItem("logistics_drone_waypoint", ModBlocks.LOGISTICS_DRONE_WAYPOINT);
	public static final RegistryObject<Item> LOGISTICS_DRONE_DOCK = blockItem("logistics_drone_dock", ModBlocks.LOGISTICS_DRONE_DOCK);
	public static final RegistryObject<Item> LOGISTICS_PROVIDER_CRATE = blockItem("logistics_provider_crate", ModBlocks.LOGISTICS_PROVIDER_CRATE);
	public static final RegistryObject<Item> LOGISTICS_REQUESTER_CRATE = blockItem("logistics_requester_crate", ModBlocks.LOGISTICS_REQUESTER_CRATE);
	public static final RegistryObject<Item> PNEUMATIC_TUBE = blockItem("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE);
	public static final RegistryObject<Item> PAINTABLE_PNEUMATIC_TUBE = blockItem("paintable_pneumatic_tube", ModBlocks.PAINTABLE_PNEUMATIC_TUBE);
	public static final RegistryObject<Item> FAN = multiblockItem("fan", ModBlocks.FAN);
	public static final RegistryObject<Item> INSERTER = multiblockItem("inserter", ModBlocks.INSERTER);
	public static final RegistryObject<Item> SAFE_BARREL = multiblockItem("safe_barrel", ModBlocks.SAFE_BARREL);
	public static final RegistryObject<Item> STEEL_BARREL = multiblockItem("steel_barrel", ModBlocks.STEEL_BARREL);
	public static final RegistryObject<Item> TECHNETIUM_STEEL_BARREL = multiblockItem("technetium_steel_barrel", ModBlocks.TECHNETIUM_STEEL_BARREL);
	public static final RegistryObject<Item> MAGNETIC_ANTIMATTER_CONTAINER = multiblockItem("magnetic_antimatter_container", ModBlocks.MAGNETIC_ANTIMATTER_CONTAINER);
	public static final RegistryObject<Item> BATTERY_SOCKET = multiblockItem("battery_socket", ModBlocks.BATTERY_SOCKET);
	public static final RegistryObject<Item> FENSU = multiblockItem("fensu", ModBlocks.FENSU);
	public static final RegistryObject<Item> OLD_CAPACITOR = multiblockItem("old_capacitor", ModBlocks.OLD_CAPACITOR);
	public static final RegistryObject<Item> CONVERTER_HE_RF = blockItem("converter_he_rf", ModBlocks.CONVERTER_HE_RF);
	public static final RegistryObject<Item> CONVERTER_RF_HE = blockItem("converter_rf_he", ModBlocks.CONVERTER_RF_HE);
	public static final RegistryObject<Item> ELECTRIC_FURNACE = blockItem("electric_furnace", ModBlocks.ELECTRIC_FURNACE);
	public static final RegistryObject<Item> MICROWAVE = multiblockItem("microwave", ModBlocks.MICROWAVE);
	public static final RegistryObject<Item> ASSEMBLY_MACHINE = multiblockItem("assembly_machine", ModBlocks.ASSEMBLY_MACHINE);
	public static final RegistryObject<Item> ASSEMBLY_FACTORY = multiblockItem("assembly_factory", ModBlocks.ASSEMBLY_FACTORY);
	public static final RegistryObject<Item> PRECASS = multiblockItem("precass", ModBlocks.PRECASS);
	public static final RegistryObject<Item> CHEMICAL_PLANT = multiblockItem("chemical_plant", ModBlocks.CHEMICAL_PLANT);
	public static final RegistryObject<Item> CHEMICAL_FACTORY = multiblockItem("chemical_factory", ModBlocks.CHEMICAL_FACTORY);
	public static final RegistryObject<Item> TEN_K_20HZ_TRANSFORMER = blockItem("10k20hztransformer", ModBlocks.TEN_K_20HZ_TRANSFORMER);

	public static final RegistryObject<Item> RBMK_FUEL_CHANNEL = multiblockItem("rbmk_fuel_channel", ModBlocks.RBMK_FUEL_CHANNEL);
	public static final RegistryObject<Item> RBMK_MODERATED_FUEL_CHANNEL = multiblockItem("rbmk_moderated_fuel_channel", ModBlocks.RBMK_MODERATED_FUEL_CHANNEL);
	public static final RegistryObject<Item> RBMK_FUEL_CHANNEL_REASIM = multiblockItem("rbmk_fuel_channel_reasim", ModBlocks.RBMK_FUEL_CHANNEL_REASIM);
	public static final RegistryObject<Item> RBMK_MODERATED_FUEL_CHANNEL_REASIM = multiblockItem("rbmk_moderated_fuel_channel_reasim", ModBlocks.RBMK_MODERATED_FUEL_CHANNEL_REASIM);
	public static final RegistryObject<Item> RBMK_CONTROL_RODS = multiblockItem("rbmk_control_rods", ModBlocks.RBMK_CONTROL_RODS);
	public static final RegistryObject<Item> RBMK_MODERATED_CONTROL_RODS = multiblockItem("rbmk_moderated_control_rods", ModBlocks.RBMK_MODERATED_CONTROL_RODS);
	public static final RegistryObject<Item> RBMK_AUTOMATIC_CONTROL_RODS = multiblockItem("rbmk_automatic_control_rods", ModBlocks.RBMK_AUTOMATIC_CONTROL_RODS);
	public static final RegistryObject<Item> RBMK_CONTROL_RODS_REASIM = multiblockItem("rbmk_control_rods_reasim", ModBlocks.RBMK_CONTROL_RODS_REASIM);
	public static final RegistryObject<Item> RBMK_AUTOMATIC_CONTROL_RODS_REASIM = multiblockItem("rbmk_automatic_control_rods_reasim", ModBlocks.RBMK_AUTOMATIC_CONTROL_RODS_REASIM);
	public static final RegistryObject<Item> RBMK_STRUCTURAL_COLUMN = multiblockItem("rbmk_structural_column", ModBlocks.RBMK_STRUCTURAL_COLUMN);
	public static final RegistryObject<Item> RBMK_STEAM_CHANNEL = multiblockItem("rbmk_steam_channel", ModBlocks.RBMK_STEAM_CHANNEL);
	public static final RegistryObject<Item> RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR = multiblockItem("rbmk_tungsten_carbide_neutron_reflector", ModBlocks.RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR);
	public static final RegistryObject<Item> RBMK_BORON_NEUTRON_ABSORBER = multiblockItem("rbmk_boron_neutron_absorber", ModBlocks.RBMK_BORON_NEUTRON_ABSORBER);
	public static final RegistryObject<Item> RBMK_GRAPHITE_MODERATOR = multiblockItem("rbmk_graphite_moderator", ModBlocks.RBMK_GRAPHITE_MODERATOR);
	public static final RegistryObject<Item> RBMK_IRRADIATION_CHANNEL = multiblockItem("rbmk_irradiation_channel", ModBlocks.RBMK_IRRADIATION_CHANNEL);
	public static final RegistryObject<Item> RBMK_STORAGE_COLUMN = multiblockItem("rbmk_storage_column", ModBlocks.RBMK_STORAGE_COLUMN);
	public static final RegistryObject<Item> RBMK_COOLER = multiblockItem("rbmk_cooler", ModBlocks.RBMK_COOLER);
	public static final RegistryObject<Item> RBMK_FLUID_HEATER = multiblockItem("rbmk_fluid_heater", ModBlocks.RBMK_FLUID_HEATER);
	public static final RegistryObject<Item> RBMK_STEAM_CONNECTOR = blockItem("rbmk_steam_connector", ModBlocks.RBMK_STEAM_CONNECTOR);
	public static final RegistryObject<Item> RBMK_REASIM_WATER_INLET = blockItem("rbmk_reasim_water_inlet", ModBlocks.RBMK_REASIM_WATER_INLET);
	public static final RegistryObject<Item> RBMK_REASIM_STEAM_OUTLET = blockItem("rbmk_reasim_steam_outlet", ModBlocks.RBMK_REASIM_STEAM_OUTLET);
	public static final RegistryObject<Item> BLANK_ROR_PANEL = rorItem("blank_ror_panel", ModBlocks.BLANK_ROR_PANEL, RoRBlockItem.RoRKind.BLANK);
	public static final RegistryObject<Item> RBMK_DISPLAY_PANEL = rorItem("rbmk_display_panel", ModBlocks.RBMK_DISPLAY_PANEL, RoRBlockItem.RoRKind.DISPLAY);
	public static final RegistryObject<Item> ROR_KEYPAD = rorItem("ror_keypad", ModBlocks.ROR_KEYPAD, RoRBlockItem.RoRKind.KEYPAD);
	public static final RegistryObject<Item> ROR_LEVER = rorItem("ror_lever", ModBlocks.ROR_LEVER, RoRBlockItem.RoRKind.LEVER);
	public static final RegistryObject<Item> ROR_GUAGE = rorItem("ror_guage", ModBlocks.ROR_GUAGE, RoRBlockItem.RoRKind.GAUGE);
	public static final RegistryObject<Item> ROR_INDICATOR_LIGHTS = rorItem("ror_indicator_lights", ModBlocks.ROR_INDICATOR_LIGHTS, RoRBlockItem.RoRKind.INDICATOR);
	public static final RegistryObject<Item> ROR_NUMERIC_DISPLAY = rorItem("ror_numeric_display", ModBlocks.ROR_NUMERIC_DISPLAY, RoRBlockItem.RoRKind.NUMERIC);
	public static final RegistryObject<Item> ROR_GRAPH = rorItem("ror_graph", ModBlocks.ROR_GRAPH, RoRBlockItem.RoRKind.GRAPH);
	public static final RegistryObject<Item> DENSE_SUPERCONDUCTING_COIL = blockItem("dense_superconducting_coil", ModBlocks.DENSE_SUPERCONDUCTING_COIL);
	public static final RegistryObject<Item> DENSE_GOLD_COIL = blockItem("dense_gold_coil", ModBlocks.DENSE_GOLD_COIL);
	public static final RegistryObject<Item> DENSE_NEODYMIUM_COIL = blockItem("dense_neodymium_coil", ModBlocks.DENSE_NEODYMIUM_COIL);
	public static final RegistryObject<Item> DENSE_4000K_SUPERCONDUCTOR_COIL = blockItem("dense_4000k_superconductor_coil", ModBlocks.DENSE_4000K_SUPERCONDUCTOR_COIL);
	public static final RegistryObject<Item> DENSE_SCHRABIDIC_COIL = blockItem("dense_schrabidic_coil", ModBlocks.DENSE_SCHRABIDIC_COIL);
	public static final RegistryObject<Item> DENSE_SCHRABIDATE_COIL = blockItem("dense_schrabidate_coil", ModBlocks.DENSE_SCHRABIDATE_COIL);
	public static final RegistryObject<Item> DENSE_STARMETAL_COIL = blockItem("dense_starmetal_coil", ModBlocks.DENSE_STARMETAL_COIL);
	public static final RegistryObject<Item> DENSE_CHLOROPHYTE_COIL = blockItem("dense_chlorophyte_coil", ModBlocks.DENSE_CHLOROPHYTE_COIL);
	public static final RegistryObject<Item> DENSE_MESE_COIL = blockItem("dense_mese_coil", ModBlocks.DENSE_MESE_COIL);
	public static final RegistryObject<Item> BLAST_FURNACE_EXTENSION = blockItem("blast_furnace_extension", ModBlocks.BLAST_FURNACE_EXTENSION);
	public static final RegistryObject<Item> BRICKED_FURNACE = blockItem("bricked_furnace", ModBlocks.BRICKED_FURNACE);
	public static final RegistryObject<Item> NUCLEAR_BLAST_FURNACE = blockItem("nuclear_blast_furnace", ModBlocks.NUCLEAR_BLAST_FURNACE);

	public static final RegistryObject<Item> SHALLOW_FOUNDRY_BASIN = blockItem("shallow_foundry_basin", ModBlocks.SHALLOW_FOUNDRY_BASIN);
	public static final RegistryObject<Item> FOUNDRY_BASIN = blockItem("foundry_basin", ModBlocks.FOUNDRY_BASIN);
	public static final RegistryObject<Item> FOUNDRY_CHANNEL = blockItem("foundry_channel", ModBlocks.FOUNDRY_CHANNEL);
	public static final RegistryObject<Item> FOUNDRY_STORAGE_BASIN = blockItem("foundry_storage_basin", ModBlocks.FOUNDRY_STORAGE_BASIN);
	public static final RegistryObject<Item> FOUNDRY_OUTLET = blockItem("foundry_outlet", ModBlocks.FOUNDRY_OUTLET);
	public static final RegistryObject<Item> FOUNDRY_SPILL_OUTLET = blockItem("foundry_spill_outlet", ModBlocks.FOUNDRY_SPILL_OUTLET);

	private static RegistryObject<Item> blockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
		return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static RegistryObject<Item> rorItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block,
			RoRBlockItem.RoRKind kind) {
		return ITEMS.register(name, () -> new RoRBlockItem(block.get(), new Item.Properties(), kind));
	}

	private static RegistryObject<Item> multiblockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
		return ITEMS.register(name, () -> new MultiblockBlockItem(block.get(), new Item.Properties()));
	}

	private ModItems() {
	}
}
