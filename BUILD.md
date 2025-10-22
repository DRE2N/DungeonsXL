# DungeonsXL Build Instructions

## Requirements

- **Java 21** or higher
- **Gradle 8.5** or higher (included via Gradle Wrapper)
- Internet connection (for downloading dependencies)

## Building the Project

This project uses **Gradle** as its build system and is configured for **Java 21** with **Paper API 1.21.8**.

### Quick Build

```bash
./gradlew build
```

The compiled JAR files will be available in:
- **Main plugin**: `dist/build/libs/DungeonsXL-0.18-SNAPSHOT.jar`
- **Addon**: `addon/dist/build/libs/DungeonsXXL-0.18-SNAPSHOT.jar`

### Build Commands

| Command | Description |
|---------|-------------|
| `./gradlew clean` | Clean build directories |
| `./gradlew build` | Compile and build all modules |
| `./gradlew test` | Run tests |
| `./gradlew shadowJar` | Build shaded distribution JARs |
| `./gradlew :dist:shadowJar` | Build only main plugin |
| `./gradlew :addon:dist:shadowJar` | Build only addon |
| `./gradlew tasks` | List all available tasks |

### Project Structure

```
DungeonsXL/
├── api/              - Public API interfaces
├── adapter/          - Block adapter interface
├── bukkit_blockdata/ - Modern BlockData implementation (Paper 1.21.8)
├── core/             - Main plugin implementation
├── dist/             - Distribution module (shaded JAR)
└── addon/
    ├── core/         - DungeonsXXL addon implementation
    └── dist/         - Addon distribution (shaded JAR)
```

## Technical Details

### API Version
- **Paper API**: 1.21.8-R0.1-SNAPSHOT
- **Java Version**: 21 (toolchain)
- **Gradle**: 8.5

### Key Features
- ✅ No NMS (net.minecraft.server) code
- ✅ No CraftBukkit reflection
- ✅ Modern Paper API 1.21.8 only
- ✅ Java 21 with modern language features
- ✅ Gradle build system with Kotlin DSL
- ✅ Shadow plugin for dependency shading

### Dependencies

The project depends on:
- Paper API 1.21.8
- Erethon Libraries (Caliburn, Vignette)
- Optional third-party plugins:
  - Vault API
  - Citizens
  - PlaceholderAPI
  - LWC
  - Parties API
  - BossShop
  - HolographicDisplays

All dependencies are automatically resolved by Gradle from configured repositories.

## Development

### IDE Setup

**IntelliJ IDEA** (Recommended):
1. Open the project root directory
2. IntelliJ will auto-detect Gradle configuration
3. Wait for Gradle sync to complete
4. Java 21 toolchain will be configured automatically

**Eclipse**:
1. Install Buildship Gradle plugin
2. Import as Gradle project
3. Configure Java 21 in project settings

### Module Dependencies

```
dist (shadowJar)
 ├── core
 │   ├── api
 │   ├── adapter
 │   └── bukkit_blockdata
 ├── adapter
 ├── api
 └── bukkit_blockdata
```

## Troubleshooting

### Gradle Daemon Issues
```bash
./gradlew --stop
./gradlew build
```

### Clean Build
```bash
./gradlew clean build --refresh-dependencies
```

### Dependency Cache Issues
```bash
rm -rf ~/.gradle/caches/
./gradlew build
```

## Migration from Maven

This project was previously built with Maven. The Gradle configuration provides equivalent functionality:

| Maven | Gradle |
|-------|--------|
| `mvn clean` | `./gradlew clean` |
| `mvn compile` | `./gradlew classes` |
| `mvn package` | `./gradlew build` |
| `mvn install` | `./gradlew publishToMavenLocal` |

## License

This project is licensed under GNU General Public License v3.0.
See the individual source files for copyright information.
