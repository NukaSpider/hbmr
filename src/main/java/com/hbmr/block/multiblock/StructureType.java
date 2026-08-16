package com.hbmr.block.multiblock;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Multiblock footprints using HBM [U, D, N, S, W, E] dims (authored for SOUTH; rotated by {@link MultiblockHelper}).
 */
public enum StructureType {
	CARGO_ELEVATOR(new int[]{0, 0, 1, 1, 1, 1}, 1, "cargo_elevator", "hbmr:block/multiblock/cargo_elevator", true),
	VT_BLAST_DOOR(new int[]{4, 0, 0, 0, 2, 2}, 0, "vt_blast_door", "hbmr:block/multiblock/vault/vault_door_3", true,
			new int[][]{{0, 0, 1, -1, 2, 2}}),
	SLIDING_BLAST_DOOR(new int[]{3, 0, 0, 0, 3, 3}, 0, "sliding_blast_door", "hbmr:block/multiblock/sliding_blast_door", true),
	/** Classic 1×7 blast door ({@code ModBlocks.blast_door} in 1.7.10). */
	SLIDING_BLAST_DOOR_SHORT(new int[]{6, 0, 0, 0, 0, 0}, 0, "sliding_blast_door_short", "hbmr:block/multiblock/sliding_blast_door_short_base", true),
	FIRE_DOOR(new int[]{2, 0, 0, 0, 2, 1}, 0, "fire_door", "hbmr:block/multiblock/fire_door", true),
	TRANSITION_SEAL(new int[]{23, 0, 0, 0, 13, 12}, 0, "transition_seal", "hbmr:block/multiblock/transition_seal", true),
	SILO_HATCH(new int[]{0, 0, 2, 2, 2, 2}, 2, "silo_hatch", "hbmr:block/multiblock/silo_hatch", true),
	SILO_HATCH_LARGE(new int[]{0, 0, 3, 3, 3, 3}, 3, "silo_hatch_large", "hbmr:block/multiblock/silo_hatch_large", true),
	/** Visuals-first DoorDecl ports (static closed pose). */
	SECURE_ACCESS_DOOR(new int[]{4, 0, 0, 0, 2, 2}, 0, "secure_access_door", "hbmr:block/multiblock/secure_access_door", true),
	LARGE_VEHICLE_DOOR(new int[]{5, 0, 0, 0, 3, 3}, 0, "large_vehicle_door", "hbmr:block/multiblock/large_vehicle_door", true),
	QE_CONTAINMENT_DOOR(new int[]{2, 0, 0, 0, 1, 1}, 0, "qe_containment_door", "hbmr:block/multiblock/qe_containment_door", true),
	QE_SLIDING_DOOR(new int[]{1, 0, 0, 0, 1, 0}, 0, "qe_sliding_door", "hbmr:block/multiblock/qe_sliding_door", true),
	ROUND_AIRLOCK_DOOR(new int[]{3, 0, 0, 0, 2, 1}, 0, "round_airlock_door", "hbmr:block/multiblock/round_airlock_door", true),
	SLIDING_SEAL_DOOR(new int[]{1, 0, 0, 0, 0, 0}, 0, "sliding_seal_door", "hbmr:block/multiblock/sliding_seal_door", true),
	WATER_DOOR(new int[]{2, 0, 0, 0, 1, 1}, 0, "water_door", "hbmr:block/multiblock/water_door", true),
	/** 3-tall burner press ({@code MachinePress} in 1.7.10). */
	BURNER_PRESS(new int[]{2, 0, 0, 0, 0, 0}, 0, "burner_press", "hbmr:block/multiblock/press_body", true),
	/** 3-tall electric press ({@code MachineEPress} in 1.7.10). */
	ELECTRIC_PRESS(new int[]{2, 0, 0, 0, 0, 0}, 0, "electric_press", "hbmr:block/multiblock/epress_body", true),
	/** Heat exchanging heater ({@code heater_heatex} in 1.7.10). */
	HEAT_EXCHANGING_HEATER(new int[]{0, 0, 1, 1, 1, 1}, 1, "heat_exchanging_heater", "hbmr:block/multiblock/heat_exchanging_heater", true),
	IRON_FURNACE(new int[]{1, 0, 1, 0, 1, 0}, 0, "iron_furnace", "hbmr:block/multiblock/iron_furnace", true),
	STEEL_FURNACE(new int[]{1, 0, 1, 1, 1, 1}, 1, "steel_furnace", "hbmr:block/multiblock/steel_furnace", true),
	COMBINATION_OVEN(new int[]{1, 0, 1, 1, 1, 1}, 1, "combination_oven", "hbmr:block/multiblock/combination_oven", true),
	STIRLING_ENGINE(new int[]{1, 0, 1, 1, 1, 1}, 1, "stirling_engine", "hbmr:block/multiblock/stirling_engine", true),
	HEAVY_STIRLING_ENGINE(new int[]{1, 0, 1, 1, 1, 1}, 1, "heavy_stirling_engine", "hbmr:block/multiblock/heavy_stirling_engine", true),
	CREATIVE_STIRLING_ENGINE(new int[]{1, 0, 1, 1, 1, 1}, 1, "creative_stirling_engine", "hbmr:block/multiblock/creative_stirling_engine", true),
	STIRLING_SAWMILL(new int[]{1, 0, 1, 1, 1, 1}, 1, "stirling_sawmill", "hbmr:block/multiblock/stirling_sawmill", true),
	CRUCIBLE(new int[]{0, 0, 1, 1, 1, 1}, 1, "crucible", "hbmr:block/multiblock/crucible", true),
	STRAND_CASTER(new int[]{0, 0, 6, 0, 1, 0}, 0, "strand_caster", "hbmr:block/multiblock/strand_caster", true,
			new int[][]{{2, 0, 1, 0, 1, 0}}),
	BOILER(new int[]{3, 0, 1, 1, 1, 1}, 1, "boiler", "hbmr:block/multiblock/boiler", true),
	INDUSTRIAL_BOILER(new int[]{4, 0, 1, 1, 1, 1}, 1, "industrial_boiler", "hbmr:block/multiblock/industrial_boiler", true),
	CENTRIFUGE(new int[]{3, 0, 0, 0, 0, 0}, 0, "centrifuge", "hbmr:block/multiblock/centrifuge", true),
	GAS_CENTRIFUGE(new int[]{3, 0, 0, 0, 0, 0}, 0, "gas_centrifuge", "hbmr:block/multiblock/gas_centrifuge", true),
	FEL(new int[]{2, 0, 4, 2, 1, 1}, 2, "fel", "hbmr:block/multiblock/fel", true),
	SILEX(new int[]{2, 0, 1, 1, 1, 1}, 1, "silex", "hbmr:block/multiblock/silex", true),
	ROTARY_FURNACE(new int[]{4, 0, 1, 1, 2, 2}, 1, "rotary_furnace", "hbmr:block/multiblock/rotary_furnace", true),
	ORE_ACIDIZER(new int[]{5, 0, 1, 1, 1, 1}, 1, "ore_acidizer", "hbmr:block/multiblock/ore_acidizer", true),
	BREEDING_REACTOR(new int[]{2, 0, 0, 0, 0, 0}, 0, "breeding_reactor", "hbmr:block/multiblock/breeding_reactor", true),
	WOOD_BURNING_GENERATOR(new int[]{1, 0, 1, 0, 1, 0}, 0, "wood_burning_generator", "hbmr:block/multiblock/wood_burning_generator", true),
	DIESEL_GENERATOR(new int[]{0, 0, 0, 0, 0, 0}, 0, "diesel_generator", "hbmr:block/multiblock/diesel_generator", true),
	INDUSTRIAL_COMBUSTION_ENGINE(new int[]{1, 0, 1, 0, 3, 2}, 0, "industrial_combustion_engine", "hbmr:block/multiblock/industrial_combustion_engine", true),
	RESEARCH_REACTOR(new int[]{2, 0, 0, 0, 0, 0}, 0, "research_reactor", "hbmr:block/multiblock/research_reactor", true),
	ZIRNOX_NUCLEAR_REACTOR(new int[]{1, 0, 2, 2, 2, 2}, 2, "zirnox_nuclear_reactor", "hbmr:block/multiblock/zirnox_nuclear_reactor", true,
			new int[][]{
					{4, -2, 1, 1, 1, 1},
					{4, -2, 0, 0, 2, -2},
					{4, -2, 0, 0, -2, 2}
			}),
	INDUSTRIAL_GENERATOR(new int[]{0, 0, 0, 0, 0, 0}, 0, "industrial_generator", "hbmr:block/multiblock/industrial_generator", true),
	RADIATION_POWERED_ENGINE(new int[]{2, 0, 3, 2, 1, 1}, 2, "radiation_powered_engine", "hbmr:block/multiblock/radiation_powered_engine", true),
	CYCLOTRON(new int[]{2, 0, 2, 2, 2, 2}, 2, "cyclotron", "hbmr:block/multiblock/cyclotron", true),
	EXPOSURE_CHAMBER(new int[]{4, 0, 2, 2, 2, 2}, 2, "exposure_chamber", "hbmr:block/multiblock/exposure_chamber", true,
			new int[][]{
					// Coil arm + rails + green end (MachineExposureChamber#fillSpace / getAllDimensions)
					{3, 0, 0, 0, -3, 8},
					{2, -2, 1, -1, -3, 6},
					{2, -2, -1, 1, -3, 6},
					{3, 0, 1, -1, -7, 8},
					{3, 0, -1, 1, -7, 8},
					// Full green base pad 2×3×1 (includes center cells makeExtra only partly covered)
					{0, 0, 1, 1, -7, 8}
			}),
	RT_GENERATOR(new int[]{0, 0, 0, 0, 0, 0}, 0, "rt_generator", "hbmr:block/multiblock/rt_generator", true),
	RTGRC(new int[]{2, 0, 1, 1, 1, 1}, 0, "rtgrc", "hbmr:block/multiblock/rtgrc", true),
	GEOTHERMAL_HEAT_EXCHANGER(new int[]{11, 0, 1, 1, 1, 1}, 1, "geothermal_heat_exchanger", "hbmr:block/multiblock/geothermal_heat_exchanger", true),
	PARTICLE_SOURCE(new int[]{1, 1, 1, 1, 4, 4}, 0, "particle_source", "hbmr:block/multiblock/particle_source", true),
	BEAMLINE(new int[]{0, 0, 0, 0, 1, 1}, 0, "beamline", "hbmr:block/multiblock/beamline", true),
	RF_CAVITY(new int[]{1, 1, 1, 1, 4, 4}, 0, "rf_cavity", "hbmr:block/multiblock/rf_cavity", true),
	QUADRUPOLE_MAGNETS(new int[]{1, 1, 1, 1, 1, 1}, 0, "quadrupole_magnets", "hbmr:block/multiblock/quadrupole_magnets", true),
	DIPOLE_MAGNETS(new int[]{1, 1, 1, 1, 1, 1}, 0, "dipole_magnets", "hbmr:block/multiblock/dipole_magnets", true),
	PARTICLE_DETECTOR(new int[]{2, 2, 2, 2, 4, 4}, 0, "particle_detector", "hbmr:block/multiblock/particle_detector", true),
	RBMK_CONSOLE(new int[]{3, 0, 0, 0, 2, 2}, 1, "rbmk_console", "hbmr:block/multiblock/rbmk_console", true,
			new int[][]{{0, 0, 0, 1, 2, 2}}),
	RBMK_CRANE_CONSOLE(new int[]{1, 0, 0, 0, 1, 1}, 1, "rbmk_crane_console", "hbmr:block/multiblock/rbmk_crane_console", true,
			new int[][]{{0, 0, 0, 1, 1, 1}}),
	RBMK_AUTOLOADER(new int[]{8, 0, 0, 0, 0, 0}, 0, "rbmk_autoloader", "hbmr:block/multiblock/rbmk_autoloader", true),
	RBMK_DEBRIS(new int[]{0, 0, 0, 0, 0, 0}, 0, "rbmk_debris", "hbmr:block/multiblock/rbmk_debris", true),
	RBMK_DEBRIS_FLAMING(new int[]{0, 0, 0, 0, 0, 0}, 0, "rbmk_debris_flaming", "hbmr:block/multiblock/rbmk_debris_flaming", true),
	/** Same crushed debris.obj as plain; radiating texture (1.7.10 pribris_radiating). */
	RBMK_DEBRIS_SMOLDERING(new int[]{0, 0, 0, 0, 0, 0}, 0, "rbmk_debris_smoldering", "hbmr:block/multiblock/rbmk_debris_smoldering", true),
	/** Same crushed debris.obj as plain; digamma texture (1.7.10 pribris_digamma). */
	RBMK_DEBRIS_BLACKENED(new int[]{0, 0, 0, 0, 0, 0}, 0, "rbmk_debris_blackened", "hbmr:block/multiblock/rbmk_debris_blackened", true),
	// RBMK columns: Dummyable {3,0,0,0,0,0} = 4 tall; rendered as stacked cubes (not OBJ).
	RBMK_FUEL_CHANNEL(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_MODERATED_FUEL_CHANNEL(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_FUEL_CHANNEL_REASIM(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_MODERATED_FUEL_CHANNEL_REASIM(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_CONTROL_RODS(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_MODERATED_CONTROL_RODS(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_AUTOMATIC_CONTROL_RODS(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_CONTROL_RODS_REASIM(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_AUTOMATIC_CONTROL_RODS_REASIM(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_STRUCTURAL_COLUMN(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_STEAM_CHANNEL(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_BORON_NEUTRON_ABSORBER(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_GRAPHITE_MODERATOR(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_IRRADIATION_CHANNEL(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_STORAGE_COLUMN(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_COOLER(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	RBMK_FLUID_HEATER(new int[]{3, 0, 0, 0, 0, 0}, 0, null, null, true),
	/** Drainage pipe ({@code machine_drain} in 1.7.10): intake cube + 2-block pipe. */
	DRAINAGE_PIPE(new int[]{0, 0, 2, 0, 0, 0}, 0, "drainage_pipe", "hbmr:block/multiblock/drainage_pipe", true),
	/** Telex desk ({@code radio_telex} in 1.7.10): 2×1×1. */
	TELEX_MACHINE(new int[]{0, 0, 0, 0, 1, 0}, 0, "telex_machine", "hbmr:block/multiblock/telex_machine", true),
	/** Conveyor splitter ({@code crane_splitter} in 1.7.10): 2×1×1 with per-part textures + belt. */
	CONVEYOR_SPLITTER(new int[]{0, 0, 0, 0, 0, 1}, 0, "conveyor_splitter", "hbmr:block/crane_splitter_front_left", true),
	FAN(new int[]{0, 0, 0, 0, 0, 0}, 0, "fan", "hbmr:block/multiblock/fan", true),
	INSERTER(new int[]{0, 0, 0, 0, 0, 0}, 0, "inserter", "hbmr:block/multiblock/inserter", true),
	SAFE_BARREL(new int[]{0, 0, 0, 0, 0, 0}, 0, "safe_barrel", "hbmr:block/multiblock/safe_barrel", true),
	STEEL_BARREL(new int[]{0, 0, 0, 0, 0, 0}, 0, "steel_barrel", "hbmr:block/multiblock/steel_barrel", true),
	TECHNETIUM_STEEL_BARREL(new int[]{0, 0, 0, 0, 0, 0}, 0, "technetium_steel_barrel", "hbmr:block/multiblock/technetium_steel_barrel", true),
	MAGNETIC_ANTIMATTER_CONTAINER(new int[]{0, 0, 0, 0, 0, 0}, 0, "magnetic_antimatter_container", "hbmr:block/multiblock/magnetic_antimatter_container", true),
	BATTERY_SOCKET(new int[]{1, 0, 1, 0, 1, 0}, 0, "battery_socket", "hbmr:block/multiblock/battery_socket", true),
	/** Modern FEnSU mesh ({@code fensu2.obj} / Battery REDD footprint in 1.7.10). */
	FENSU(new int[]{9, 0, 2, 2, 4, 4}, 2, "fensu", "hbmr:block/multiblock/fensu", true),
	OLD_CAPACITOR(new int[]{0, 0, 0, 0, 0, 0}, 0, "old_capacitor", "hbmr:block/capacitor_copper_side", true),
	MICROWAVE(new int[]{0, 0, 0, 0, 0, 0}, 0, "microwave", "hbmr:block/multiblock/microwave", true),
	ASSEMBLY_MACHINE(new int[]{2, 0, 1, 1, 1, 1}, 1, "assembly_machine", "hbmr:block/multiblock/assembly_machine", true),
	ASSEMBLY_FACTORY(new int[]{2, 0, 2, 2, 2, 2}, 2, "assembly_factory", "hbmr:block/multiblock/assembly_factory", true),
	PRECASS(new int[]{2, 0, 1, 1, 1, 1}, 1, "precass", "hbmr:block/multiblock/precass", true),
	CHEMICAL_PLANT(new int[]{2, 0, 1, 1, 1, 1}, 1, "chemical_plant", "hbmr:block/multiblock/chemical_plant", true),
	CHEMICAL_FACTORY(new int[]{2, 0, 2, 2, 2, 2}, 2, "chemical_factory", "hbmr:block/multiblock/chemical_factory", true);

	private final int[] dims;
	private final int offset;
	@Nullable
	private final String objName;
	@Nullable
	private final String modelTexture;
	private final boolean useObjBer;
	@Nullable
	private final int[][] extraDims;

	StructureType(int[] dims, int offset, @Nullable String objName, @Nullable String modelTexture, boolean useObjBer) {
		this(dims, offset, objName, modelTexture, useObjBer, null);
	}

	StructureType(int[] dims, int offset, @Nullable String objName, @Nullable String modelTexture, boolean useObjBer,
			@Nullable int[][] extraDims) {
		this.dims = dims;
		this.offset = offset;
		this.objName = objName;
		this.modelTexture = modelTexture;
		this.useObjBer = useObjBer;
		this.extraDims = extraDims;
	}

	/** [U, D, N, S, W, E] */
	public int[] getDims() {
		return dims;
	}

	/** Extra footprints filled alongside the main dims (vault door depth slab). */
	@Nullable
	public int[][] getExtraDims() {
		return extraDims;
	}

	public int getOffset() {
		return offset;
	}

	/** Extra Y lift for the core when horizontal offset is 0 (PA machines). */
	public int getHeightOffset() {
		return switch (this) {
			case PARTICLE_SOURCE, RF_CAVITY, QUADRUPOLE_MAGNETS, DIPOLE_MAGNETS -> 1;
			case PARTICLE_DETECTOR -> 2;
			default -> 0;
		};
	}

	public boolean useObjBer() {
		return useObjBer;
	}

	/** 4-tall Dummyable column ({@code dims U=3}); stacked-cube BER, not OBJ. */
	public boolean isRbmkColumn() {
		return switch (this) {
			case RBMK_FUEL_CHANNEL, RBMK_MODERATED_FUEL_CHANNEL, RBMK_FUEL_CHANNEL_REASIM,
					RBMK_MODERATED_FUEL_CHANNEL_REASIM, RBMK_CONTROL_RODS, RBMK_MODERATED_CONTROL_RODS,
					RBMK_AUTOMATIC_CONTROL_RODS, RBMK_CONTROL_RODS_REASIM, RBMK_AUTOMATIC_CONTROL_RODS_REASIM,
					RBMK_STRUCTURAL_COLUMN, RBMK_STEAM_CHANNEL, RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR,
					RBMK_BORON_NEUTRON_ABSORBER, RBMK_GRAPHITE_MODERATOR, RBMK_IRRADIATION_CHANNEL,
					RBMK_STORAGE_COLUMN, RBMK_COOLER, RBMK_FLUID_HEATER -> true;
			default -> false;
		};
	}

	@Nullable
	public ResourceLocation getBerModelLocation() {
		if (this == SLIDING_BLAST_DOOR_SHORT) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_short_base_ber");
		}
		if (this == SLIDING_BLAST_DOOR) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_frame_ber");
		}
		if (this == FIRE_DOOR) {
			return DoorSkins.fireFrame(0);
		}
		if (this == SECURE_ACCESS_DOOR) {
			return DoorSkins.secureFrame(0);
		}
		if (this == LARGE_VEHICLE_DOOR) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/large_vehicle_door_frame_ber");
		}
		if (this == QE_CONTAINMENT_DOOR) {
			return DoorSkins.containmentFrame(0);
		}
		if (this == QE_SLIDING_DOOR) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/qe_sliding_door_frame_ber");
		}
		if (this == ROUND_AIRLOCK_DOOR) {
			return DoorSkins.airlockFrame(0);
		}
		if (this == SLIDING_SEAL_DOOR) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_seal_door_frame_ber");
		}
		if (this == WATER_DOOR) {
			return DoorSkins.waterFrame(0);
		}
		if (this == BURNER_PRESS) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/burner_press_body_ber");
		}
		if (this == ELECTRIC_PRESS) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/electric_press_body_ber");
		}
		if (this == VT_BLAST_DOOR) {
			return DoorSkins.vaultFrame(0);
		}
		if (this == SILO_HATCH) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_frame_ber");
		}
		if (this == SILO_HATCH_LARGE) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_large_frame_ber");
		}
		if (this == CARGO_ELEVATOR) {
			return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_base_ber");
		}
		if (objName == null) {
			return null;
		}
		if (this == CONVEYOR_SPLITTER) {
			// Per-part Wavefront via ConveyorSplitterRenderer (no single baked BER).
			return null;
		}
		return ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/" + objName + "_ber");
	}

	/**
	 * All BER models for this structure (classic short blast door is 4 separate OBJs;
	 * animated doors use one OBJ with part visibility models).
	 */
	public ResourceLocation[] getAllBerModelLocations() {
		if (this == SLIDING_BLAST_DOOR_SHORT) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_short_base_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_short_tooth_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_short_slider_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_short_block_ber")
			};
		}
		if (this == SLIDING_BLAST_DOOR) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_leftdoor_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_rightdoor_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_leftlock_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_blast_door_rightlock_ber")
			};
		}
		if (this == FIRE_DOOR) {
			return DoorSkins.allFireBerModels();
		}
		if (this == SECURE_ACCESS_DOOR) {
			return DoorSkins.allSecureBerModels();
		}
		if (this == LARGE_VEHICLE_DOOR) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/large_vehicle_door_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/large_vehicle_door_left_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/large_vehicle_door_right_ber")
			};
		}
		if (this == QE_CONTAINMENT_DOOR) {
			return DoorSkins.allContainmentBerModels();
		}
		if (this == QE_SLIDING_DOOR) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/qe_sliding_door_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/qe_sliding_door_left_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/qe_sliding_door_right_ber")
			};
		}
		if (this == ROUND_AIRLOCK_DOOR) {
			return DoorSkins.allAirlockBerModels();
		}
		if (this == SLIDING_SEAL_DOOR) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_seal_door_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/sliding_seal_door_door_ber")
			};
		}
		if (this == WATER_DOOR) {
			return DoorSkins.allWaterBerModels();
		}
		if (this == BURNER_PRESS) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/burner_press_body_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/burner_press_head_ber")
			};
		}
		if (this == ELECTRIC_PRESS) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/electric_press_body_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/electric_press_head_ber")
			};
		}
		if (this == VT_BLAST_DOOR) {
			return DoorSkins.allVaultBerModels();
		}
		if (this == SILO_HATCH) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_hatch_ber")
			};
		}
		if (this == SILO_HATCH_LARGE) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_large_frame_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/silo_hatch_large_hatch_ber")
			};
		}
		if (this == TRANSITION_SEAL) {
			return TransitionSealParts.allBerModels();
		}
		if (this == CARGO_ELEVATOR) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_base_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_platform_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_piston_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_guide_m1_m1_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_guide_m1_p1_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_guide_p1_m1_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/cargo_elevator_guide_p1_p1_ber")
			};
		}
		if (this == RADIATION_POWERED_ENGINE) {
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_base_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_rotor_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_light_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_glass_tint_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_glass_ber")
			};
		}
		ResourceLocation single = getBerModelLocation();
		return single == null ? new ResourceLocation[0] : new ResourceLocation[]{single};
	}

	/**
	 * Inventory icon model. Vault uses Door+Label only (matches 1.7.10 item render).
	 */
	@Nullable
	public ResourceLocation getItemBerModelLocation() {
		if (objName == null && this != SLIDING_BLAST_DOOR_SHORT) {
			return null;
		}
		if (this == VT_BLAST_DOOR) {
			return DoorSkins.vaultDoor(0);
		}
		return getBerModelLocation();
	}

	/** Inventory may need every part (animated doors). */
	public ResourceLocation[] getAllItemBerModelLocations() {
		if (this == SLIDING_BLAST_DOOR_SHORT || this == SLIDING_BLAST_DOOR
				|| this == SILO_HATCH || this == SILO_HATCH_LARGE || this == CARGO_ELEVATOR) {
			return getAllBerModelLocations();
		}
		if (this == FIRE_DOOR) {
			return new ResourceLocation[]{DoorSkins.fireFrame(0), DoorSkins.fireDoor(0)};
		}
		if (this == SECURE_ACCESS_DOOR) {
			return new ResourceLocation[]{DoorSkins.secureFrame(0), DoorSkins.secureDoor(0)};
		}
		if (this == LARGE_VEHICLE_DOOR) {
			return getAllBerModelLocations();
		}
		if (this == QE_CONTAINMENT_DOOR) {
			return new ResourceLocation[]{DoorSkins.containmentFrame(0), DoorSkins.containmentDoor(0)};
		}
		if (this == QE_SLIDING_DOOR) {
			return getAllBerModelLocations();
		}
		if (this == ROUND_AIRLOCK_DOOR) {
			return new ResourceLocation[]{DoorSkins.airlockFrame(0), DoorSkins.airlockLeft(0), DoorSkins.airlockRight(0)};
		}
		if (this == SLIDING_SEAL_DOOR) {
			return getAllBerModelLocations();
		}
		if (this == WATER_DOOR) {
			return new ResourceLocation[]{
					DoorSkins.waterFrame(0), DoorSkins.waterDoor(0), DoorSkins.waterBolts(0),
					DoorSkins.waterTop(0), DoorSkins.waterBottom(0)
			};
		}
		if (this == BURNER_PRESS || this == ELECTRIC_PRESS) {
			return getAllBerModelLocations();
		}
		if (this == RADIATION_POWERED_ENGINE) {
			// Match 1.7.10 item render: Base + Rotor + green Light (no glass).
			return new ResourceLocation[]{
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_base_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_rotor_ber"),
					ResourceLocation.fromNamespaceAndPath("hbmr", "block/multiblock/radiation_powered_engine_light_ber")
			};
		}
		if (this == VT_BLAST_DOOR) {
			return new ResourceLocation[]{DoorSkins.vaultDoor(0), DoorSkins.vaultLabel(0)};
		}
		ResourceLocation single = getItemBerModelLocation();
		return single == null ? new ResourceLocation[0] : new ResourceLocation[]{single};
	}

	@Nullable
	public String getObjPath() {
		return objName == null ? null : "hbmr:models/block/multiblock/" + objName + ".obj";
	}

	@Nullable
	public String getModelTexture() {
		return modelTexture;
	}

	public VoxelShape defaultCellShape() {
		return Shapes.block();
	}
}
