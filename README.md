# Metz Client

A client-side utility mod for Minecraft **1.21.11** (Fabric, Yarn mappings, Loom 1.14, Java 21).

## Build

1. Install JDK 21 and open this folder in IntelliJ IDEA (or any Gradle setup, Gradle 8.14+).
2. Run `gradle build` (or `./gradlew build` if you generate a wrapper with `gradle wrapper`).
3. Copy `build/libs/metz-client-1.0.0.jar` into your `.minecraft/mods` folder together with Fabric API 0.141.1+1.21.11.

If Gradle says a version does not exist, update `gradle.properties` with the latest values from https://fabricmc.net/develop.

## Controls

| Key | Action |
| --- | --- |
| Right Shift | Open click GUI |
| G | Flight |
| B | BoatFly |
| H | BaseFinder |
| Z | Speed |
| X | Fullbright |

AutoSprint has no key; enable it in the GUI. Keys are the `defaultKey` argument in each module's constructor.

Click GUI: left click a module to toggle, right click to expand its settings, then left/right click a setting to raise/lower it.

## Modules

- **Flight**: jump goes up, sneak goes down. Settings: Speed, Vertical.
- **BoatFly**: while in a boat, fly with your look direction. Jump goes up, sprint key goes down, otherwise it hovers.
- **BaseFinder**: scans chunks within your render distance for chests, barrels, shulkers, furnaces, hoppers and similar. Chunks over the score threshold are listed on the HUD (nearest first) and announced in chat. It only sees chunks the server has sent you.
- **Speed**: faster ground and air movement.
- **AutoSprint**: sprints whenever you hold forward.
- **Fullbright**: client-side night vision.

Use these on servers where they are allowed. Most servers' anti-cheat will kick or ban for flight and speed.
