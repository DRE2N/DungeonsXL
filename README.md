![DungeonsXL](http://feuerstern.bplaced.net/ressourcen/logos/DungeonsXL.png)

[![Builds](http://feuerstern.bplaced.net/ressourcen/buttons/Builds.png)](http://feuerstern.bplaced.net/repo/io/github/dre2n/dungeonsxl)
[![Wiki](http://feuerstern.bplaced.net/ressourcen/buttons/Wiki.png)](../../wiki/)
[![Issues](http://feuerstern.bplaced.net/ressourcen/buttons/Issues.png)](../../issues/)
[![JavaDocs](http://feuerstern.bplaced.net/ressourcen/buttons/JavaDocs.png)](http://feuerstern.bplaced.net/javadocs/dxl/)
[![MCStats](http://feuerstern.bplaced.net/ressourcen/buttons/MCStats.png)](http://mcstats.org/plugin/DungeonsXL/)

![Doge](https://i.imgflip.com/vtpyi.jpg)

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
* ...and many more!


## The concept

This outline may help you to understand the concept of DungeonsXL:

![Concept](http://feuerstern.bplaced.net/ressourcen/DXLSigns/concept.png)

If you want to learn how to use DungeonsXL step by step, please have a look at the [wiki](../../wiki) page [getting started](../../wiki/getting-started).


## Compatibility
### Server
DungeonsXL works with 1.7.8 and higher. However, support for 1.9.x has a higher priority than support for 1.8.x and lower.
Older versions of DungeonsXL support versions since Minecraft 1.3.x, but of course, they are completely unsupported.
* [1.7.8-1.9.4](../../tree/master)
* [1.7.5](../../tree/50f772d14281bfe278dba2559d1758cc459c1a30)
* [1.7.2](../../tree/eccf82b7335dfb0723e3cd37a57df1a968ea7842)
* [1.6.4](../../tree/780145cf783ea76fe1bfee04cf89216bd4f92e1d)
* [1.6.2](../../tree/fcc27ca35caccb2b849c8f9de4ae212d875dc9a9)
* [1.5.2](../../tree/08a8b31da0c88e6c4d4f3f4cce5b38cd0f72f447)
* [1.5.1](../../tree/992542ec0f307ddfd48861c5391feb40610c4f20)
* [1.4.7](../../tree/90a625cae0acc8a2ce57d0856a8e731a81f02729)
* [1.4.6](../../tree/bf34312f30ccab48d64bce03ed7979863a8151cf)
* [1.4.x](../../tree/3cf96c5f25eada06a434db7753ec22a34ffa4d78)
* [1.3.x](../../tree/15effb1071b3c36bca68352c5ddb6469bcbead10)

### Building information and dependencies
Building DungeonsXL from source requires [Apache Maven](https://maven.apache.org/).
Maven automatically fetches all dependencies and builds DungeonsXL; just run _build.bat_ or enter the command _mvn clean install_.

#### BRCommons
Instead of referencing the internals of the implementation directly, DungeonsXL uses [BRCommons](https://github.com/DRE2N/BRCommons).
The shaded version of DXL (standard version) contains this library, while the original version needs it as an external plugin.
Have a look at the [installation instructions](../../wiki/getting-started#installation) for detailed information.

### Java
7 and higher

### UUIDs
Supported.

### Known incompatibilities
* Towny
* Corpses

Many incompatibilities can be fixed with [PerWorldPlugins](http://dev.bukkit.org/bukkit-plugins/perworldplugins/) ([fork for 1.8+](https://www.spigotmc.org/resources/perworldplugins-unofficial-update-version.6454/)).
Try to add the incompatible plugins only to the worlds where you need them.
