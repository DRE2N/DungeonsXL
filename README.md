# DungeonsXL 2.0

**Modern rewrite using Paper API 1.21.8 and Java 21**

## Features

- **Dungeon System**: Single-floor and multi-floor dungeons
- **World Management**: Automatic world instantiation and cleanup
- **Player Groups**: Team-based dungeon gameplay with 16 color teams
- **Sign System**: 30+ interactive sign types
- **Trigger System**: 10 different trigger types
- **Mob Management**: Wave-based mob spawning
- **Rewards & Requirements**: Customizable rewards and entry requirements
- **Game Rules**: 60+ configurable game rules

## Requirements

- **Server**: Paper 1.21.8 or higher
- **Java**: Java 21 or higher
- **Optional**: PlaceholderAPI, Vault

## Building

```bash
./gradlew shadowJar
```

Output: `build/libs/DungeonsXL-2.0.0-SNAPSHOT.jar`

## Documentation

- Commands: `/dxl help`
- Configuration: `plugins/DungeonsXL/config.yml`
- Maps: `plugins/DungeonsXL/maps/`

## License

GPL-3.0 License

## Credits

- Original DungeonsXL by Erethon & DRE2N team
- Rewritten for modern Paper API by linghun91
