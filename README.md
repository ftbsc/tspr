# Tiramisuper
[![Build](https://github.com/ftbsc/tspr/actions/workflows/build.yml/badge.svg?branch=dev)](https://github.com/ftbsc/tspr/actions/workflows/build.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/ftbsc/tspr)
![GitHub repo file or directory count](https://img.shields.io/github/directory-file-count/ftbsc/tspr)

![TiramiSuPeR](https://cdn.alemi.dev/proj/tspr.png)

> modern (1.21.10), neoforge, low-expectations minecraft utility client built from scratch

> [!IMPORTANT]
> currently very barebones but we'll get there

# structure

this mod is split into sub-modules, mostly independent among each-other: they have their own configs and (optionally) keybinds, so that it's possible to include (or exclude) on demand

modifications to the core game jars are made using [lillero](https://github.com/zaaarf/lillero) (and its processor + loader), attempting to avoid invasive changes which could break with other mods

# features

currently user-facing features are very limited:
 * auto-fish
 * auto-walk
 * chat timestamps
 * auto-disconnect (_somewhat..._)

there's no GUI or command system (_yet_), but all configs are editable with neoforge mod config GUI (_or editing the .toml file..._)

to potential developers, inner features include:
 * a scheduler
 * base and togglable module bases (handling config and keybinds)
 * packet events (via asm patch)
 * incoming and outgoing chat events (via asm patch)

don't expect these lists to stay up-to-date, but they should give you a rough idea of what you can expect

# credits

a continuation of [BSCV](https://github.com/ftbsc/boscovicino), since upgrading it to versions past 1.16 proved just too much hassle
