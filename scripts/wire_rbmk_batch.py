from pathlib import Path

cols = [
    "rbmk_fuel_channel", "rbmk_moderated_fuel_channel", "rbmk_fuel_channel_reasim",
    "rbmk_moderated_fuel_channel_reasim", "rbmk_control_rods", "rbmk_moderated_control_rods",
    "rbmk_automatic_control_rods", "rbmk_control_rods_reasim", "rbmk_automatic_control_rods_reasim",
    "rbmk_structural_column", "rbmk_steam_channel", "rbmk_tungsten_carbide_neutron_reflector",
    "rbmk_boron_neutron_absorber", "rbmk_graphite_moderator", "rbmk_irradiation_channel",
    "rbmk_storage_column", "rbmk_cooler", "rbmk_fluid_heater", "rbmk_steam_connector",
    "rbmk_reasim_water_inlet", "rbmk_reasim_steam_outlet",
]
ror = [
    "blank_ror_panel", "rbmk_display_panel", "ror_keypad", "ror_lever", "ror_guage",
    "ror_indicator_lights", "ror_numeric_display", "ror_graph",
]

blocks = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModBlocks.java")
text = blocks.read_text(encoding="utf-8")
needle = (
    '\tpublic static final RegistryObject<Block> QUADRUPOLE_MAGNETS = BLOCKS.register("quadrupole_magnets",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.QUADRUPOLE_MAGNETS));\n'
)
insert = needle + (
    '\tpublic static final RegistryObject<Block> DIPOLE_MAGNETS = BLOCKS.register("dipole_magnets",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.DIPOLE_MAGNETS));\n'
    '\tpublic static final RegistryObject<Block> PARTICLE_DETECTOR = BLOCKS.register("particle_detector",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.PARTICLE_DETECTOR));\n'
    '\tpublic static final RegistryObject<Block> RBMK_CONSOLE = BLOCKS.register("rbmk_console",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CONSOLE));\n'
    '\tpublic static final RegistryObject<Block> RBMK_CRANE_CONSOLE = BLOCKS.register("rbmk_crane_console",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_CRANE_CONSOLE));\n'
    '\tpublic static final RegistryObject<Block> RBMK_AUTOLOADER = BLOCKS.register("rbmk_autoloader",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_AUTOLOADER));\n'
    '\tpublic static final RegistryObject<Block> RBMK_DEBRIS = BLOCKS.register("rbmk_debris",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_DEBRIS));\n'
    '\tpublic static final RegistryObject<Block> RBMK_DEBRIS_FLAMING = BLOCKS.register("rbmk_debris_flaming",\n'
    '\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.RBMK_DEBRIS_FLAMING));\n'
)
for c in cols:
    field = c.upper()
    insert += (
        f'\tpublic static final RegistryObject<Block> {field} = BLOCKS.register("{c}",\n'
        f'\t\t\t() -> new Block(machineProperties(5.0F, 10.0F)));\n'
    )
for c in ror:
    field = c.upper()
    insert += (
        f'\tpublic static final RegistryObject<Block> {field} = BLOCKS.register("{c}",\n'
        f'\t\t\t() -> new FacingMachineBlock(machineProperties(5.0F, 10.0F)));\n'
    )

if "DIPOLE_MAGNETS" not in text:
    if needle not in text:
        raise SystemExit("ModBlocks needle missing")
    blocks.write_text(text.replace(needle, insert), encoding="utf-8")
    print("ModBlocks updated")
else:
    print("ModBlocks already has DIPOLE")

items = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModItems.java")
text = items.read_text(encoding="utf-8")
needle = '\tpublic static final RegistryObject<Item> QUADRUPOLE_MAGNETS = multiblockItem("quadrupole_magnets", ModBlocks.QUADRUPOLE_MAGNETS);\n'
insert = needle
mbs = [
    ("dipole_magnets", "DIPOLE_MAGNETS"),
    ("particle_detector", "PARTICLE_DETECTOR"),
    ("rbmk_console", "RBMK_CONSOLE"),
    ("rbmk_crane_console", "RBMK_CRANE_CONSOLE"),
    ("rbmk_autoloader", "RBMK_AUTOLOADER"),
    ("rbmk_debris", "RBMK_DEBRIS"),
    ("rbmk_debris_flaming", "RBMK_DEBRIS_FLAMING"),
]
for name, field in mbs:
    insert += f'\tpublic static final RegistryObject<Item> {field} = multiblockItem("{name}", ModBlocks.{field});\n'
for c in cols + ror:
    field = c.upper()
    insert += f'\tpublic static final RegistryObject<Item> {field} = blockItem("{c}", ModBlocks.{field});\n'

if "DIPOLE_MAGNETS" not in text:
    if needle not in text:
        raise SystemExit("ModItems needle missing")
    items.write_text(text.replace(needle, insert), encoding="utf-8")
    print("ModItems updated")
else:
    print("ModItems already has DIPOLE")

# ModBlockEntities
be = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModBlockEntities.java")
text = be.read_text(encoding="utf-8")
needle = "\t\tblocks.add(ModBlocks.QUADRUPOLE_MAGNETS.get());\n"
insert = needle + "".join(
    f"\t\tblocks.add(ModBlocks.{f}.get());\n"
    for f in [
        "DIPOLE_MAGNETS", "PARTICLE_DETECTOR", "RBMK_CONSOLE", "RBMK_CRANE_CONSOLE",
        "RBMK_AUTOLOADER", "RBMK_DEBRIS", "RBMK_DEBRIS_FLAMING",
    ]
)
if "DIPOLE_MAGNETS" not in text:
    be.write_text(text.replace(needle, insert), encoding="utf-8")
    print("ModBlockEntities updated")
else:
    print("ModBlockEntities already has DIPOLE")

# Creative tab
tab = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModCreativeTabs.java")
text = tab.read_text(encoding="utf-8")
needle = "\t\t\t\t\t\toutput.accept(ModItems.QUADRUPOLE_MAGNETS.get());\n"
order = [
    "DIPOLE_MAGNETS", "PARTICLE_DETECTOR",
    "RBMK_FUEL_CHANNEL", "RBMK_MODERATED_FUEL_CHANNEL", "RBMK_FUEL_CHANNEL_REASIM",
    "RBMK_MODERATED_FUEL_CHANNEL_REASIM", "RBMK_CONTROL_RODS", "RBMK_MODERATED_CONTROL_RODS",
    "RBMK_AUTOMATIC_CONTROL_RODS", "RBMK_CONTROL_RODS_REASIM", "RBMK_AUTOMATIC_CONTROL_RODS_REASIM",
    "RBMK_STRUCTURAL_COLUMN", "RBMK_STEAM_CHANNEL", "RBMK_TUNGSTEN_CARBIDE_NEUTRON_REFLECTOR",
    "RBMK_BORON_NEUTRON_ABSORBER", "RBMK_GRAPHITE_MODERATOR", "RBMK_IRRADIATION_CHANNEL",
    "RBMK_STORAGE_COLUMN", "RBMK_COOLER", "RBMK_FLUID_HEATER", "RBMK_CONSOLE", "RBMK_CRANE_CONSOLE",
    "BLANK_ROR_PANEL", "RBMK_DISPLAY_PANEL", "ROR_KEYPAD", "ROR_LEVER", "ROR_GUAGE",
    "ROR_INDICATOR_LIGHTS", "ROR_NUMERIC_DISPLAY", "ROR_GRAPH", "RBMK_AUTOLOADER",
    "RBMK_STEAM_CONNECTOR", "RBMK_REASIM_WATER_INLET", "RBMK_REASIM_STEAM_OUTLET",
    "RBMK_DEBRIS", "RBMK_DEBRIS_FLAMING",
]
insert = needle + "".join(f"\t\t\t\t\t\toutput.accept(ModItems.{f}.get());\n" for f in order)
if "DIPOLE_MAGNETS" not in text:
    tab.write_text(text.replace(needle, insert), encoding="utf-8")
    print("ModCreativeTabs updated")
else:
    print("ModCreativeTabs already has DIPOLE")

# Lang
lang = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\resources\assets\hbmr\lang\en_us.json")
import json
data = json.loads(lang.read_text(encoding="utf-8"))
names = {
    "dipole_magnets": "Dipole Magnets",
    "particle_detector": "Particle Detector",
    "rbmk_fuel_channel": "RBMK Fuel Channel",
    "rbmk_moderated_fuel_channel": "RBMK Moderated Fuel Channel",
    "rbmk_fuel_channel_reasim": "RBMK Fuel Channel (ReaSim)",
    "rbmk_moderated_fuel_channel_reasim": "RBMK Moderated Fuel Channel (ReaSim)",
    "rbmk_control_rods": "RBMK Control Rods",
    "rbmk_moderated_control_rods": "RBMK Moderated Control Rods",
    "rbmk_automatic_control_rods": "RBMK Automatic Control Rods",
    "rbmk_control_rods_reasim": "RBMK Control Rods (ReaSim)",
    "rbmk_automatic_control_rods_reasim": "RBMK Automatic Control Rods (ReaSim)",
    "rbmk_structural_column": "RBMK Structural Column",
    "rbmk_steam_channel": "RBMK Steam Channel",
    "rbmk_tungsten_carbide_neutron_reflector": "RBMK Tungsten Carbide Neutron Reflector",
    "rbmk_boron_neutron_absorber": "RBMK Boron Neutron Absorber",
    "rbmk_graphite_moderator": "RBMK Graphite Moderator",
    "rbmk_irradiation_channel": "RBMK Irradiation Channel",
    "rbmk_storage_column": "RBMK Storage Column",
    "rbmk_cooler": "RBMK Cooler",
    "rbmk_fluid_heater": "RBMK Fluid Heater",
    "rbmk_console": "RBMK Console",
    "rbmk_crane_console": "RBMK Crane Console",
    "blank_ror_panel": "Blank Redstone-over-Radio Panel",
    "rbmk_display_panel": "RBMK Display Panel",
    "ror_keypad": "Redstone-over-Radio Keypad",
    "ror_lever": "Redstone-over-Radio Lever",
    "ror_guage": "Redstone-over-Radio Gauge",
    "ror_indicator_lights": "Redstone-over-Radio Indicator Lights",
    "ror_numeric_display": "Redstone-over-Radio Numeric Display",
    "ror_graph": "Redstone-over-Radio Graph",
    "rbmk_autoloader": "RBMK Autoloader",
    "rbmk_steam_connector": "RBMK Steam Connector",
    "rbmk_reasim_water_inlet": "RBMK ReaSim Water Inlet",
    "rbmk_reasim_steam_outlet": "RBMK ReaSim Steam Outlet",
    "rbmk_debris": "RBMK Debris",
    "rbmk_debris_flaming": "Flaming RBMK Debris",
}
for k, v in names.items():
    data[f"block.hbmr.{k}"] = v
lang.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
print("Lang updated", len(names))
