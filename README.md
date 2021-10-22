![DungeonsXL](https://erethon.de/resources/logos/DungeonsXL.png)

[![Builds](https://erethon.de/resources/buttons/Builds.png)](http://erethon.de/repo/de/erethon/dungeonsxl/dungeonsxl-dist)
[![Wiki](https://erethon.de/resources/buttons/Wiki.png)](../../wiki/)
[![Issues](https://erethon.de/resources/buttons/Issues.png)](../../issues/)
[![JavaDocs](https://erethon.de/resources/buttons/JavaDocs.png)](http://erethon.de/javadocs/dungeonsxl/)
[![MCStats](https://erethon.de/resources/buttons/MCStats.png)](http://bstats.org/plugin/bukkit/DungeonsXL/)

[![Build Status](https://travis-ci.com/DRE2N/DungeonsXL.svg?branch=master)](https://travis-ci.com/DRE2N/DungeonsXL) [![codebeat badge](https://codebeat.co/badges/5c57507f-084b-4945-8159-06bf5cf17794)](https://codebeat.co/projects/github-com-dre2n-dungeonsxl-master)


DungeonsXL is a server mod that allows you to instantiate worlds.

Its main goal is to offer a way to use a world in a set state multiple times by a player (like for a jump'n'run), a group of players (e.g. for a quest dungeon, an adventure map or a PvE arena) or even by groups of groups of players (e.g. for PvP arenas).
DungeonsXL also provides custom game mechanics to make these worlds interesting. It might also be helpful if you want players to build something in creative mode quickly and uncomplicated without any influence on their main world data (inventory, levels etc.).

## Features
* Create as many dungeons as you wish!
* The instantiation system allows dungeons to be played by multiple groups of players at the same time without clashes.
* Dungeons are accessable through portals in one of your main worlds. [Read more...](../../wiki/getting-started#entering-the-dungeon)
* Invite players to edit single dungeons without the need to give them any further permissions. [Read more...](../../wiki/getting-started#editing-the-map)
* Allow players to build in creative mode safely without any influence to their game progress in the main worlds!
* Set checkpoints, breakable blocks, triggers, messages and much more through signs in the edit mode. [Read more...](../../wiki/signs)
* Per dungeon configuration (you should try that after you became familiar with the basics of this plugin). [Read more...](../../wiki/dungeon-configuration)
* Link multiple floors together to create large dungeons with multiple levels. [Read more...](../../wiki/getting-started#advanced-multi-floor-dungeons-mfds)
* Use a dungeon as a tutorial and give them a PEX group when they finish it. [Read more...](../../wiki/main-configuration)
* Players can play the dungeon with their own items or with configurable classes.
* _The classes support doges!_
* Mob waves: [Read more...](../../wiki/signs#wave)
* PvP
* Time limits
* A built-in custom mob system and support for MythicMobs. [Read more...](../../wiki/signs#mob)
* A powerful API: [Read more...](../../wiki/api-tutorial)
* Different game types allow you to use your maps dynamically for different purposes. [Read more...](../../wiki/game-types)
* Announcements sothat users can join the next match easily. [Read more...](../../wiki/announcements)
* Per dungeon resource packs
* ...and many more!


## The concept

If you want to learn how to use DungeonsXL step by step, please have a look at the [wiki](../../wiki) page [getting started](../../wiki/getting-started).

## Compatibility
### Server
DungeonsXL works with Spigot 1.8 and higher. However, support for 1.13-1.17 has a higher priority than support for 1.8-1.12. Old builds that support older versions are unusable for production environments. See [here](../../wiki/legacy-support) for detailed information. DungeonsXL only works with Spigot and does not support CraftBukkit builds.

### ItemsXL
DungeonsXL requires [ItemsXL](https://www.spigotmc.org/resources/itemsxl.14472/) 1.0.2 to run.

### Building information and dependencies
Building DungeonsXL from source requires [Apache Maven](https://maven.apache.org/).
Maven automatically fetches all dependencies and builds DungeonsXL; just run _build.bat_ or enter the command _mvn clean install_.

#### DRECommons
[DRECommons](https://github.com/DRE2N/DRECommons) is a util library for common tasks.

#### Caliburn API
[Caliburn](https://github.com/DRE2N/CaliburnAPI) is an API to read custom items and mobs from config files.

#### Vignette
[Vignette](https://github.com/DRE2N/Vignette) is a Bukkit GUI Framework.

### Java
Make sure that your server uses Java 8 or higher.

### UUIDs
Supported.

### Known incompatibilities
* Corpses
* PerWorldInventory

Many incompatibilities can be fixed with [PerWorldPlugins](http://dev.bukkit.org/bukkit-plugins/perworldplugins/) ([fork for 1.8+](https://www.spigotmc.org/resources/perworldplugins-unofficial-update-version.6454/)).
Try to add the incompatible plugins only to the worlds where you need them.
