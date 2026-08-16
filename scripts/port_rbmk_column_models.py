#!/usr/bin/env python3
"""Copy/fix RBMK column OBJs and extra textures for fuel/control rods."""
from __future__ import annotations

import shutil
from pathlib import Path

ROOT = Path(r"C:\Users\Joseph\Documents\HBM Port")
HBM = ROOT / "1.7.10/src/main/resources/assets/hbm"
HBMR = ROOT / "1.20.1/src/main/resources/assets/hbmr"
TEX = HBMR / "textures/block"
MDL = HBMR / "models/block/rbmk"
MDL.mkdir(parents=True, exist_ok=True)

COLUMNS = {
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
FUEL = {
    "rbmk_fuel_channel",
    "rbmk_moderated_fuel_channel",
    "rbmk_fuel_channel_reasim",
    "rbmk_moderated_fuel_channel_reasim",
}
PIPED = {
    "rbmk_control_rods",
    "rbmk_moderated_control_rods",
    "rbmk_automatic_control_rods",
    "rbmk_control_rods_reasim",
    "rbmk_automatic_control_rods_reasim",
    "rbmk_steam_channel",
    "rbmk_fluid_heater",
}


def prep_obj(src: Path, dest_stem: str) -> None:
    text = src.read_text(encoding="utf-8", errors="ignore").splitlines()
    out: list[str] = ["# hbmr", f"mtllib {dest_stem}.mtl"]
    body: list[str] = []
    for line in text:
        if line.startswith("mtllib "):
            continue
        body.append(line)
    has_usemtl = False
    inserted = False
    for i, line in enumerate(body):
        if line.startswith("usemtl"):
            has_usemtl = True
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
                out.append(f"f {idxs[0]} {idxs[1]} {idxs[2]}")
                out.append(f"f {idxs[0]} {idxs[2]} {idxs[3]}")
                continue
            if len(idxs) > 4:
                for j in range(1, len(idxs) - 1):
                    out.append(f"f {idxs[0]} {idxs[j]} {idxs[j + 1]}")
                continue
        out.append(line)
    (MDL / f"{dest_stem}.obj").write_text("\n".join(out) + "\n", encoding="utf-8")
    (MDL / f"{dest_stem}.mtl").write_text(
        "newmtl Texture\nmap_Kd hbmr:block/rbmk_fuel_channel_side\n", encoding="utf-8"
    )
    print("obj", dest_stem)


def main() -> None:
    src_tex = HBM / "textures/blocks/rbmk"
    for pid, stem in COLUMNS.items():
        for suffix in ("_side", "_top"):
            s = src_tex / f"{stem}{suffix}.png"
            if s.exists():
                shutil.copy2(s, TEX / f"{pid}{suffix}.png")
        if pid in FUEL:
            for suffix in ("_inner", "_fuel"):
                s = src_tex / f"{stem}{suffix}.png"
                if s.exists():
                    shutil.copy2(s, TEX / f"{pid}{suffix}.png")
        if pid in PIPED:
            for suffix in ("_pipe_side", "_pipe_top"):
                s = src_tex / f"{stem}{suffix}.png"
                if s.exists():
                    shutil.copy2(s, TEX / f"{pid}{suffix}.png")
    prep_obj(HBM / "models/rbmk/rbmk_element.obj", "rbmk_element")
    prep_obj(HBM / "models/rbmk/rbmk_element_rods.obj", "rbmk_element_rods")
    prep_obj(HBM / "models/rbmk/rbmk_rods.obj", "rbmk_rods")


if __name__ == "__main__":
    main()
