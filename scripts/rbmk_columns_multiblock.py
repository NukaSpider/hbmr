from pathlib import Path

COLUMNS = [
    "rbmk_fuel_channel",
    "rbmk_moderated_fuel_channel",
    "rbmk_fuel_channel_reasim",
    "rbmk_moderated_fuel_channel_reasim",
    "rbmk_control_rods",
    "rbmk_moderated_control_rods",
    "rbmk_automatic_control_rods",
    "rbmk_control_rods_reasim",
    "rbmk_automatic_control_rods_reasim",
    "rbmk_structural_column",
    "rbmk_steam_channel",
    "rbmk_tungsten_carbide_neutron_reflector",
    "rbmk_boron_neutron_absorber",
    "rbmk_graphite_moderator",
    "rbmk_irradiation_channel",
    "rbmk_storage_column",
    "rbmk_cooler",
    "rbmk_fluid_heater",
]

# --- StructureType: insert before DIPOLE or after RBMK_DEBRIS_FLAMING ---
st = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\block\multiblock\StructureType.java")
text = st.read_text(encoding="utf-8")
if "RBMK_FUEL_CHANNEL(" not in text:
    needle = "\tRBMK_DEBRIS_FLAMING(new int[]{0, 0, 0, 0, 0, 0}, 0, \"rbmk_debris_flaming\", \"hbmr:block/multiblock/rbmk_debris_flaming\", true);\n"
    insert = needle
    for c in COLUMNS:
        field = c.upper()
        insert += (
            f'\t{field}(new int[]{{3, 0, 0, 0, 0, 0}}, 0, "{c}", "hbmr:block/{c}", true),\n'
        )
    # fix trailing comma on last - last should end with ;
    insert = insert.rstrip(",\n") + ";\n"
    # remove the old RBMK_DEBRIS_FLAMING line ending with ; from insert start duplicate
    # Actually needle already has flaming with ; - we need flaming with comma then columns ending ;
    needle_old = needle
    flaming = '\tRBMK_DEBRIS_FLAMING(new int[]{0, 0, 0, 0, 0, 0}, 0, "rbmk_debris_flaming", "hbmr:block/multiblock/rbmk_debris_flaming", true),\n'
    cols = ""
    for i, c in enumerate(COLUMNS):
        field = c.upper()
        end = ";" if i == len(COLUMNS) - 1 else ","
        cols += f'\t{field}(new int[]{{3, 0, 0, 0, 0, 0}}, 0, "{c}", "hbmr:block/{c}", true){end}\n'
    if needle_old not in text:
        raise SystemExit("StructureType flaming needle missing")
    text = text.replace(needle_old, flaming + cols)
    st.write_text(text, encoding="utf-8")
    print("StructureType updated")
else:
    print("StructureType already has columns")

# --- ModBlocks: change Block() to MultiblockControllerBlock ---
mb = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModBlocks.java")
text = mb.read_text(encoding="utf-8")
for c in COLUMNS:
    field = c.upper()
    old = (
        f'\tpublic static final RegistryObject<Block> {field} = BLOCKS.register("{c}",\n'
        f'\t\t\t() -> new Block(machineProperties(5.0F, 10.0F)));\n'
    )
    new = (
        f'\tpublic static final RegistryObject<Block> {field} = BLOCKS.register("{c}",\n'
        f'\t\t\t() -> new MultiblockControllerBlock(machineProperties(5.0F, 10.0F), StructureType.{field}));\n'
    )
    if old in text:
        text = text.replace(old, new)
        print("block", c)
    elif f"StructureType.{field}" in text:
        print("block already", c)
    else:
        print("MISSING block", c)
mb.write_text(text, encoding="utf-8")

# --- ModItems: blockItem -> multiblockItem ---
mi = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModItems.java")
text = mi.read_text(encoding="utf-8")
for c in COLUMNS:
    field = c.upper()
    old = f'\tpublic static final RegistryObject<Item> {field} = blockItem("{c}", ModBlocks.{field});\n'
    new = f'\tpublic static final RegistryObject<Item> {field} = multiblockItem("{c}", ModBlocks.{field});\n'
    if old in text:
        text = text.replace(old, new)
        print("item", c)
    elif f'multiblockItem("{c}"' in text:
        print("item already", c)
    else:
        print("MISSING item", c)
mi.write_text(text, encoding="utf-8")

# --- ModBlockEntities ---
be = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\java\com\hbmr\registry\ModBlockEntities.java")
text = be.read_text(encoding="utf-8")
if "RBMK_FUEL_CHANNEL" not in text:
    needle = "\t\tblocks.add(ModBlocks.RBMK_DEBRIS_FLAMING.get());\n"
    insert = needle + "".join(f"\t\tblocks.add(ModBlocks.{c.upper()}.get());\n" for c in COLUMNS)
    if needle not in text:
        raise SystemExit("BE needle missing")
    be.write_text(text.replace(needle, insert), encoding="utf-8")
    print("ModBlockEntities updated")
else:
    print("ModBlockEntities already")

# --- Item models builtin/entity ---
item_dir = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\resources\assets\hbmr\models\item")
for c in COLUMNS:
    (item_dir / f"{c}.json").write_text(
        '{\n  "parent": "minecraft:builtin/entity",\n  "gui_light": "front",\n'
        f'  "textures": {{ "particle": "hbmr:block/{c}_side" }}\n}}\n',
        encoding="utf-8",
    )
print("item jsons updated")

# --- Block models: air for multiblock controllers ---
blk_dir = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\resources\assets\hbmr\models\block")
bs_dir = Path(r"C:\Users\Joseph\Documents\HBM Port\1.20.1\src\main\resources\assets\hbmr\blockstates")
for c in COLUMNS:
    (blk_dir / f"{c}.json").write_text(
        '{\n  "parent": "minecraft:block/air",\n'
        f'  "textures": {{ "particle": "hbmr:block/{c}_side" }}\n}}\n',
        encoding="utf-8",
    )
    (bs_dir / f"{c}.json").write_text(
        '{\n  "variants": {\n'
        f'    "facing=north": {{ "model": "hbmr:block/{c}" }},\n'
        f'    "facing=south": {{ "model": "hbmr:block/{c}", "y": 180 }},\n'
        f'    "facing=west":  {{ "model": "hbmr:block/{c}", "y": 270 }},\n'
        f'    "facing=east":  {{ "model": "hbmr:block/{c}", "y": 90 }}\n'
        "  }\n}\n",
        encoding="utf-8",
    )
print("block/air + facing blockstates updated")
