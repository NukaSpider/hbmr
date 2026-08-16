#!/usr/bin/env python3
"""Prep assets for PA dipole/detector + RBMK batch into hbmr."""
from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(r"C:\Users\Joseph\Documents\HBM Port")
HBM = ROOT / "1.7.10/src/main/resources/assets/hbm"
HBMR = ROOT / "1.20.1/src/main/resources/assets/hbmr"

MB = HBMR / "models/block/multiblock"
TEX_MB = HBMR / "textures/block/multiblock"
TEX_BLK = HBMR / "textures/block"
MODELS_BLK = HBMR / "models/block"
MODELS_ITEM = HBMR / "models/item"
BLOCKSTATES = HBMR / "blockstates"


def prep_obj(src: Path, dest_stem: str, texture_path: str) -> None:
    """Copy OBJ, triangulate quads, ensure usemtl Texture, write MTL."""
    MB.mkdir(parents=True, exist_ok=True)
    lines_out: list[str] = ["# converted for hbmr", f"mtllib {dest_stem}.mtl"]
    has_usemtl = False
    text = src.read_text(encoding="utf-8", errors="ignore").splitlines()
    # Drop old mtllib
    body: list[str] = []
    for line in text:
        if line.startswith("mtllib "):
            continue
        body.append(line)

    # Ensure usemtl Texture near top (after comments/objects start)
    inserted = False
    for i, line in enumerate(body):
        if line.startswith("usemtl"):
            has_usemtl = True
            # normalize material name
            body[i] = "usemtl Texture"
        if not inserted and (line.startswith("o ") or line.startswith("g ") or line.startswith("v ")):
            if not has_usemtl:
                body.insert(i, "usemtl Texture")
                has_usemtl = True
            inserted = True

    for line in body:
        if line.startswith("usemtl None") or line.startswith("usemtl none"):
            line = "usemtl Texture"
        if line.startswith("f "):
            parts = line.split()
            idxs = parts[1:]
            if len(idxs) == 4:
                # fan triangulate
                lines_out.append(f"f {idxs[0]} {idxs[1]} {idxs[2]}")
                lines_out.append(f"f {idxs[0]} {idxs[2]} {idxs[3]}")
                continue
            if len(idxs) > 4:
                for j in range(1, len(idxs) - 1):
                    lines_out.append(f"f {idxs[0]} {idxs[j]} {idxs[j + 1]}")
                continue
        lines_out.append(line)

    (MB / f"{dest_stem}.obj").write_text("\n".join(lines_out) + "\n", encoding="utf-8")
    (MB / f"{dest_stem}.mtl").write_text(
        f"newmtl Texture\nmap_Kd {texture_path}\n", encoding="utf-8"
    )


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")


def multiblock_assets(id_: str, particle: str | None = None) -> None:
    p = particle or f"hbmr:block/multiblock/{id_}"
    write(
        MB / f"{id_}_ber.json",
        f"""{{
  "loader": "forge:obj",
  "flip_v": true,
  "automatic_culling": false,
  "model": "hbmr:models/block/multiblock/{id_}.obj",
  "textures": {{
    "particle": "{p}",
    "Texture": "{p}"
  }}
}}""",
    )
    write(
        MODELS_BLK / f"{id_}.json",
        f"""{{
  "parent": "minecraft:block/air",
  "textures": {{ "particle": "{p}" }}
}}""",
    )
    write(
        MODELS_ITEM / f"{id_}.json",
        f"""{{
  "parent": "minecraft:builtin/entity",
  "gui_light": "front",
  "textures": {{ "particle": "{p}" }}
}}""",
    )
    write(
        BLOCKSTATES / f"{id_}.json",
        f"""{{
  "variants": {{
    "facing=north": {{ "model": "hbmr:block/{id_}" }},
    "facing=south": {{ "model": "hbmr:block/{id_}", "y": 180 }},
    "facing=west":  {{ "model": "hbmr:block/{id_}", "y": 270 }},
    "facing=east":  {{ "model": "hbmr:block/{id_}", "y": 90 }}
  }}
}}""",
    )


def cube_column(id_: str, side: str, top: str) -> None:
    write(
        MODELS_BLK / f"{id_}.json",
        f"""{{
  "parent": "minecraft:block/cube_column",
  "textures": {{
    "side": "{side}",
    "end": "{top}"
  }}
}}""",
    )
    write(MODELS_ITEM / f"{id_}.json", f'{{ "parent": "hbmr:block/{id_}" }}')
    write(
        BLOCKSTATES / f"{id_}.json",
        f"""{{
  "variants": {{
    "": {{ "model": "hbmr:block/{id_}" }}
  }}
}}""",
    )


def cube_all(id_: str, tex: str) -> None:
    write(
        MODELS_BLK / f"{id_}.json",
        f"""{{
  "parent": "minecraft:block/cube_all",
  "textures": {{ "all": "{tex}" }}
}}""",
    )
    write(MODELS_ITEM / f"{id_}.json", f'{{ "parent": "hbmr:block/{id_}" }}')
    write(
        BLOCKSTATES / f"{id_}.json",
        f"""{{
  "variants": {{
    "": {{ "model": "hbmr:block/{id_}" }}
  }}
}}""",
    )


def facing_cube(id_: str, tex: str) -> None:
    write(
        MODELS_BLK / f"{id_}.json",
        f"""{{
  "parent": "minecraft:block/orientable",
  "textures": {{
    "top": "{tex}",
    "front": "{tex}",
    "side": "{tex}"
  }}
}}""",
    )
    write(MODELS_ITEM / f"{id_}.json", f'{{ "parent": "hbmr:block/{id_}" }}')
    write(
        BLOCKSTATES / f"{id_}.json",
        f"""{{
  "variants": {{
    "facing=north": {{ "model": "hbmr:block/{id_}" }},
    "facing=south": {{ "model": "hbmr:block/{id_}", "y": 180 }},
    "facing=west":  {{ "model": "hbmr:block/{id_}", "y": 270 }},
    "facing=east":  {{ "model": "hbmr:block/{id_}", "y": 90 }}
  }}
}}""",
    )


def copy_tex(src: Path, dest: Path) -> None:
    dest.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dest)


def main() -> None:
    # --- PA dipole / detector ---
    copy_tex(
        HBM / "textures/models/particleaccelerator/dipole.png",
        TEX_MB / "dipole_magnets.png",
    )
    copy_tex(
        HBM / "textures/models/particleaccelerator/detector.png",
        TEX_MB / "particle_detector.png",
    )
    prep_obj(
        HBM / "models/particleaccelerator/dipole.obj",
        "dipole_magnets",
        "hbmr:block/multiblock/dipole_magnets",
    )
    prep_obj(
        HBM / "models/particleaccelerator/detector.obj",
        "particle_detector",
        "hbmr:block/multiblock/particle_detector",
    )
    multiblock_assets("dipole_magnets")
    multiblock_assets("particle_detector")

    # --- RBMK OBJ machines ---
    obj_machines = [
        ("rbmk_console", "models/rbmk/rbmk_console.obj", "textures/models/machines/rbmk_control.png"),
        ("rbmk_crane_console", "models/rbmk/crane_console.obj", "textures/models/machines/crane_console.png"),
        ("rbmk_autoloader", "models/rbmk/autoloader.obj", "textures/models/machines/rbmk_autoloader.png"),
        ("rbmk_debris", "models/rbmk/debris.obj", "textures/blocks/rbmk/rbmk_debris.png"),
        ("rbmk_debris_flaming", "models/rbmk/debris.obj", "textures/blocks/rbmk/rbmk_debris_burning.png"),
    ]
    for dest, obj, tex in obj_machines:
        copy_tex(HBM / tex, TEX_MB / f"{dest}.png")
        prep_obj(HBM / obj, dest, f"hbmr:block/multiblock/{dest}")
        multiblock_assets(dest)

    # --- RBMK columns (cube_column) ---
    # id -> (side_stem under textures/blocks/rbmk without _side)
    columns = {
        "rbmk_fuel_channel": "rbmk_element",
        "rbmk_moderated_fuel_channel": "rbmk_element_mod",
        "rbmk_fuel_channel_reasim": "rbmk_element_reasim",
        "rbmk_moderated_fuel_channel_reasim": "rbmk_element_reasim_mod",
        "rbmk_control_rods": "rbmk_control",
        "rbmk_moderated_control_rods": "rbmk_control_mod",
        "rbmk_automatic_control_rods": "rbmk_control_auto",
        "rbmk_control_rods_reasim": "rbmk_control_reasim",
        "rbmk_automatic_control_rods_reasim": "rbmk_control_reasim_auto",
        "rbmk_structural_column": "rbmk_blank",
        "rbmk_steam_channel": "rbmk_boiler",
        "rbmk_tungsten_carbide_neutron_reflector": "rbmk_reflector",
        "rbmk_boron_neutron_absorber": "rbmk_absorber",
        "rbmk_graphite_moderator": "rbmk_moderator",
        "rbmk_irradiation_channel": "rbmk_outgasser",
        "rbmk_storage_column": "rbmk_storage",
        "rbmk_cooler": "rbmk_cooler",
        "rbmk_fluid_heater": "rbmk_heater",
    }
    for id_, stem in columns.items():
        side_src = HBM / f"textures/blocks/rbmk/{stem}_side.png"
        top_src = HBM / f"textures/blocks/rbmk/{stem}_top.png"
        if not top_src.exists():
            # control rods sometimes use side as fallback
            top_src = side_src
        copy_tex(side_src, TEX_BLK / f"{id_}_side.png")
        copy_tex(top_src, TEX_BLK / f"{id_}_top.png")
        cube_column(id_, f"hbmr:block/{id_}_side", f"hbmr:block/{id_}_top")

    # --- Simple cubes ---
    simples = {
        "rbmk_steam_connector": "textures/blocks/rbmk_loader.png",
        "rbmk_reasim_water_inlet": "textures/blocks/rbmk_steam_inlet.png",
        "rbmk_reasim_steam_outlet": "textures/blocks/rbmk_steam_outlet.png",
    }
    for id_, tex in simples.items():
        copy_tex(HBM / tex, TEX_BLK / f"{id_}.png")
        cube_all(id_, f"hbmr:block/{id_}")

    # --- RoR / display panels (facing) ---
    display_tex = HBM / "textures/blocks/rbmk/rbmk_display.png"
    copy_tex(display_tex, TEX_BLK / "rbmk_display.png")
    ror_panels = [
        "blank_ror_panel",
        "rbmk_display_panel",
        "ror_keypad",
        "ror_lever",
        "ror_guage",
        "ror_indicator_lights",
        "ror_numeric_display",
        "ror_graph",
    ]
    for id_ in ror_panels:
        facing_cube(id_, "hbmr:block/rbmk_display")

    print("Asset prep complete.")


if __name__ == "__main__":
    main()
