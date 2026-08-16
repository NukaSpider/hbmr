# HBM's Nuclear Tech Mod - Re-Port (`hbmr`)

A **1:1 port** of [Hbm's Nuclear Tech Mod](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT) to modern Minecraft loaders. Work in progress.


|              |                                                                                              |
| ------------ | -------------------------------------------------------------------------------------------- |
| **Mod ID**   | `hbmr`                                                                                       |
| **Version**  | `0.1.0`                                                                                      |
| **License**  | [GPL-3.0](LICENSE)                                                                           |
| **Author**   | NukaSpider                                                                                   |
| **Upstream** | [HbmMods/Hbm-s-Nuclear-Tech-GIT](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT) (1.7.10) |


## Branches


| Branch                                                                 | Status                                                 |
| ---------------------------------------------------------------------- | ------------------------------------------------------ |
| `[forge/1.20.1](https://github.com/NukaSpider/hbmr/tree/forge/1.20.1)` | **Active** (default) — Minecraft 1.20.1, Forge 47.4.10 |
| `forge/1.21.1`                                                         | Planned                                                |
| `neoforge/1.20.1`                                                      | Planned                                                |
| `neoforge/1.21.1`                                                      | Planned                                                |


Clone and work on the branch you care about:

```bash
git clone -b forge/1.20.1 https://github.com/NukaSpider/hbmr.git
```



## Requirements

- **JDK 17+** (Forge 1.20.1)
- Git
- Enough RAM for Gradle/Minecraft (~3 GB heap is configured in `gradle.properties`)



## Build & run

From the repo root (this branch):

```bash
# Generate IDE runs (pick one)
./gradlew genIntellijRuns
./gradlew genEclipseRuns

# Client / server
./gradlew runClient
./gradlew runServer

# Build the mod jar → build/libs/
./gradlew build
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

Forge setup docs: [docs.minecraftforge.net (1.20.1)](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/)

## Status

Early port. Content is being brought over from 1.7.10 with a visuals-first approach (models, textures, multiblocks, machines, networks). Gameplay systems and full feature parity are incomplete - expect missing recipes, GUIs, and mechanics until they are ported.

## Credits

- **Original mod:** **[HBMTheBobcat](https://www.curseforge.com/members/hbmthebobcat)** and contributors - [Hbm's Nuclear Tech](https://github.com/HbmMods/Hbm-s-Nuclear-Tech-GIT)
- **This port:** NukaSpider
- Built on [Minecraft Forge](https://files.minecraftforge.net/)



## License

Distributed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).