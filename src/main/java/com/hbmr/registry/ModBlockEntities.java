package com.hbmr.registry;

import com.hbmr.HBMR;
import com.hbmr.block.AcidInputPartitionerBlockEntity;
import com.hbmr.block.AnvilBlockEntity;
import com.hbmr.block.multiblock.MultiblockControllerBlockEntity;
import com.hbmr.block.multiblock.StructureDummyBlockEntity;
import com.hbmr.block.network.CableNetworkBlockEntity;
import com.hbmr.block.network.FluidNetworkBlockEntity;
import com.hbmr.block.network.NetworkPylonBlockEntity;
import com.hbmr.block.network.PipeAnchorBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;

public final class ModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HBMR.MODID);

	public static final RegistryObject<BlockEntityType<StructureDummyBlockEntity>> STRUCTURE_DUMMY =
			BLOCK_ENTITIES.register("structure_dummy",
					() -> BlockEntityType.Builder.of(StructureDummyBlockEntity::new, ModBlocks.STRUCTURE_DUMMY.get()).build(null));

	public static final RegistryObject<BlockEntityType<MultiblockControllerBlockEntity>> MULTIBLOCK_CONTROLLER =
			BLOCK_ENTITIES.register("multiblock_controller",
					() -> BlockEntityType.Builder.of(MultiblockControllerBlockEntity::new, controllerBlocks()).build(null));

	public static final RegistryObject<BlockEntityType<AnvilBlockEntity>> ANVIL =
			BLOCK_ENTITIES.register("anvil",
					() -> BlockEntityType.Builder.of(AnvilBlockEntity::new, anvilBlocks()).build(null));

	public static final RegistryObject<BlockEntityType<CableNetworkBlockEntity>> CABLE_NETWORK =
			BLOCK_ENTITIES.register("cable_network",
					() -> BlockEntityType.Builder.of(CableNetworkBlockEntity::new, ModBlocks.RED_COPPER_CABLE.get()).build(null));

	public static final RegistryObject<BlockEntityType<FluidNetworkBlockEntity>> FLUID_NETWORK =
			BLOCK_ENTITIES.register("fluid_network",
					() -> BlockEntityType.Builder.of(FluidNetworkBlockEntity::new,
							ModBlocks.UNIVERSAL_FLUID_DUCT_1.get(),
							ModBlocks.UNIVERSAL_FLUID_DUCT_2.get(),
							ModBlocks.UNIVERSAL_FLUID_DUCT_3.get()).build(null));

	public static final RegistryObject<BlockEntityType<PipeAnchorBlockEntity>> PIPE_ANCHOR =
			BLOCK_ENTITIES.register("pipe_anchor",
					() -> BlockEntityType.Builder.of(PipeAnchorBlockEntity::new, ModBlocks.PIPE_ANCHOR.get()).build(null));

	public static final RegistryObject<BlockEntityType<NetworkPylonBlockEntity>> NETWORK_PYLON =
			BLOCK_ENTITIES.register("network_pylon",
					() -> BlockEntityType.Builder.of(NetworkPylonBlockEntity::new, networkPylonBlocks()).build(null));

	public static final RegistryObject<BlockEntityType<AcidInputPartitionerBlockEntity>> ACID_INPUT_PARTITIONER =
			BLOCK_ENTITIES.register("acid_input_partitioner",
					() -> BlockEntityType.Builder.of(AcidInputPartitionerBlockEntity::new,
							ModBlocks.ACID_INPUT_PARTITIONER.get()).build(null));

	private static Block[] networkPylonBlocks() {
		return new Block[]{
				ModBlocks.ELECTRICITY_CONNECTOR.get(),
				ModBlocks.HEAVY_DUTY_ELECTRICITY_CONNECTOR.get(),
				ModBlocks.ELECTRICITY_PYLON.get(),
				ModBlocks.MEDIUM_WOODEN_ELECTRICITY_PYLON.get(),
				ModBlocks.MEDIUM_WOODEN_ELECTRICITY_PYLON_TRANSFORMER.get(),
				ModBlocks.MEDIUM_STEEL_ELECTRICITY_PYLON.get(),
				ModBlocks.MEDIUM_STEEL_ELECTRICITY_PYLON_TRANSFORMER.get(),
				ModBlocks.LARGE_ELECTRICITY_PYLON.get(),
				ModBlocks.SUBSTATION.get()
		};
	}

	private static Block[] anvilBlocks() {
		return new Block[]{
				ModBlocks.IRON_ANVIL.get(),
				ModBlocks.LEAD_ANVIL.get(),
				ModBlocks.STEEL_ANVIL.get(),
				ModBlocks.DESH_ANVIL.get(),
				ModBlocks.FERROURANIUM_ANVIL.get(),
				ModBlocks.SATURNITE_ANVIL.get(),
				ModBlocks.BISMUTH_BRONZE_ANVIL.get(),
				ModBlocks.ARSENIC_BRONZE_ANVIL.get(),
				ModBlocks.SCHRABIDATE_ANVIL.get(),
				ModBlocks.DNT_ANVIL.get(),
				ModBlocks.OSMIRIDIUM_ANVIL.get(),
				ModBlocks.MURKY_ANVIL.get()
		};
	}

	private static Block[] controllerBlocks() {
		Set<Block> blocks = new HashSet<>();
		blocks.add(ModBlocks.CARGO_ELEVATOR.get());
		blocks.add(ModBlocks.VT_BLAST_DOOR.get());
		blocks.add(ModBlocks.SLIDING_BLAST_DOOR.get());
		blocks.add(ModBlocks.SLIDING_BLAST_DOOR_SHORT.get());
		blocks.add(ModBlocks.FIRE_DOOR.get());
		blocks.add(ModBlocks.TRANSITION_SEAL.get());
		blocks.add(ModBlocks.SILO_HATCH.get());
		blocks.add(ModBlocks.SILO_HATCH_LARGE.get());
		blocks.add(ModBlocks.SECURE_ACCESS_DOOR.get());
		blocks.add(ModBlocks.LARGE_VEHICLE_DOOR.get());
		blocks.add(ModBlocks.QE_CONTAINMENT_DOOR.get());
		blocks.add(ModBlocks.QE_SLIDING_DOOR.get());
		blocks.add(ModBlocks.ROUND_AIRLOCK_DOOR.get());
		blocks.add(ModBlocks.SLIDING_SEAL_DOOR.get());
		blocks.add(ModBlocks.WATER_DOOR.get());
		blocks.add(ModBlocks.BURNER_PRESS.get());
		blocks.add(ModBlocks.ELECTRIC_PRESS.get());
		blocks.add(ModBlocks.HEAT_EXCHANGING_HEATER.get());
		blocks.add(ModBlocks.IRON_FURNACE.get());
		blocks.add(ModBlocks.STEEL_FURNACE.get());
		blocks.add(ModBlocks.COMBINATION_OVEN.get());
		blocks.add(ModBlocks.STIRLING_ENGINE.get());
		blocks.add(ModBlocks.HEAVY_STIRLING_ENGINE.get());
		blocks.add(ModBlocks.CREATIVE_STIRLING_ENGINE.get());
		blocks.add(ModBlocks.STIRLING_SAWMILL.get());
		blocks.add(ModBlocks.CRUCIBLE.get());
		blocks.add(ModBlocks.STRAND_CASTER.get());
		blocks.add(ModBlocks.BOILER.get());
		blocks.add(ModBlocks.INDUSTRIAL_BOILER.get());
		blocks.add(ModBlocks.CENTRIFUGE.get());
		blocks.add(ModBlocks.GAS_CENTRIFUGE.get());
		blocks.add(ModBlocks.FEL.get());
		blocks.add(ModBlocks.SILEX.get());
		blocks.add(ModBlocks.ROTARY_FURNACE.get());
		blocks.add(ModBlocks.ORE_ACIDIZER.get());
		blocks.add(ModBlocks.BREEDING_REACTOR.get());
		blocks.add(ModBlocks.WOOD_BURNING_GENERATOR.get());
		blocks.add(ModBlocks.DIESEL_GENERATOR.get());
		blocks.add(ModBlocks.INDUSTRIAL_COMBUSTION_ENGINE.get());
		blocks.add(ModBlocks.RESEARCH_REACTOR.get());
		blocks.add(ModBlocks.ZIRNOX_NUCLEAR_REACTOR.get());
		blocks.add(ModBlocks.INDUSTRIAL_GENERATOR.get());
		blocks.add(ModBlocks.RADIATION_POWERED_ENGINE.get());
		blocks.add(ModBlocks.CYCLOTRON.get());
		blocks.add(ModBlocks.EXPOSURE_CHAMBER.get());
		blocks.add(ModBlocks.RT_GENERATOR.get());
		blocks.add(ModBlocks.RTGRC.get());
		blocks.add(ModBlocks.GEOTHERMAL_HEAT_EXCHANGER.get());
		blocks.add(ModBlocks.PARTICLE_SOURCE.get());
		blocks.add(ModBlocks.BEAMLINE.get());
		blocks.add(ModBlocks.RF_CAVITY.get());
		blocks.add(ModBlocks.QUADRUPOLE_MAGNETS.get());
		blocks.add(ModBlocks.DIPOLE_MAGNETS.get());
		blocks.add(ModBlocks.PARTICLE_DETECTOR.get());
		blocks.add(ModBlocks.RBMK_CONSOLE.get());
		blocks.add(ModBlocks.RBMK_CRANE_CONSOLE.get());
		blocks.add(ModBlocks.RBMK_AUTOLOADER.get());
		blocks.add(ModBlocks.RBMK_DEBRIS.get());
		blocks.add(ModBlocks.RBMK_DEBRIS_FLAMING.get());
		blocks.add(ModBlocks.RBMK_DEBRIS_SMOLDERING.get());
		blocks.add(ModBlocks.RBMK_DEBRIS_BLACKENED.get());
		blocks.add(ModBlocks.RBMK_FUEL_CHANNEL.get());
		blocks.add(ModBlocks.RBMK_MODERATED_FUEL_CHANNEL.get());
		blocks.add(ModBlocks.RBMK_FUEL_CHANNEL_REASIM.get());
		blocks.add(ModBlocks.RBMK_MODERATED_FUEL_CHANNEL_REASIM.get());
		blocks.add(ModBlocks.RBMK_CONTROL_RODS.get());
		blocks.add(ModBlocks.RBMK_MODERATED_CONTROL_RODS.get());
		blocks.add(ModBlocks.RBMK_AUTOMATIC_CONTROL_RODS.get());
		blocks.add(ModBlocks.RBMK_CONTROL_RODS_REASIM.get());
		blocks.add(ModBlocks.RBMK_AUTOMATIC_CONTROL_RODS_REASIM.get());
		blocks.add(ModBlocks.RBMK_STRUCTURAL_COLUMN.get());
		blocks.add(ModBlocks.RBMK_STEAM_CHANNEL.get());
		blocks.add(ModBlocks.RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR.get());
		blocks.add(ModBlocks.RBMK_BORON_NEUTRON_ABSORBER.get());
		blocks.add(ModBlocks.RBMK_GRAPHITE_MODERATOR.get());
		blocks.add(ModBlocks.RBMK_IRRADIATION_CHANNEL.get());
		blocks.add(ModBlocks.RBMK_STORAGE_COLUMN.get());
		blocks.add(ModBlocks.RBMK_COOLER.get());
		blocks.add(ModBlocks.RBMK_FLUID_HEATER.get());
		blocks.add(ModBlocks.DRAINAGE_PIPE.get());
		blocks.add(ModBlocks.TELEX_MACHINE.get());
		blocks.add(ModBlocks.CONVEYOR_SPLITTER.get());
		blocks.add(ModBlocks.FAN.get());
		blocks.add(ModBlocks.INSERTER.get());
		blocks.add(ModBlocks.SAFE_BARREL.get());
		blocks.add(ModBlocks.STEEL_BARREL.get());
		blocks.add(ModBlocks.TECHNETIUM_STEEL_BARREL.get());
		blocks.add(ModBlocks.MAGNETIC_ANTIMATTER_CONTAINER.get());
		blocks.add(ModBlocks.BATTERY_SOCKET.get());
		blocks.add(ModBlocks.FENSU.get());
		blocks.add(ModBlocks.OLD_CAPACITOR.get());
		blocks.add(ModBlocks.MICROWAVE.get());
		blocks.add(ModBlocks.ASSEMBLY_MACHINE.get());
		blocks.add(ModBlocks.ASSEMBLY_FACTORY.get());
		blocks.add(ModBlocks.PRECASS.get());
		blocks.add(ModBlocks.CHEMICAL_PLANT.get());
		blocks.add(ModBlocks.CHEMICAL_FACTORY.get());
		return blocks.toArray(Block[]::new);
	}

	private ModBlockEntities() {
	}
}
