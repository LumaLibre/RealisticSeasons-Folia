### RealisticSeasons w/ Folia

This is a patcher for the [RealisticSeasons](https://www.spigotmc.org/resources/93275/) plugin,
which adds support for [Folia](https://github.com/PaperMC/Folia).

## Usage

> [!NOTE]
> You need to provide your own copy of the RealisticSeasons plugin jar.

1. Place the RealisticSeasons jar in the `sources/` directory. Your file should be named `RealsticSeasons.jar`.
2. Run `./gradlew setupVineFlower` to download and set up VineFlower.
3. Run `./gradlew installAllSpigot` to download and set up Spigot mappings for every version used.
4. Run `./gradlew decompileAndApplyPatches` to decompile and apply the patches.
5. Run `./gradlew build` to get an executable jar in the `build/libs/` directory.
6. Enjoy! Please make an issue on this repository if you encounter any bugs.


## Licenses

This repository contains original patch files authored by me.
These patch files and the tooling to apply these patch files are licensed under the MIT License which may be found in `PATCHES-LICENSE`.
The underlying software is not included and remains the property of its respective copyright holder.