# Homestead Player Animations v0.5 (experimental)

Fabric Minecraft 1.20.1, Java 17. Source-only project for GitHub Actions.

## Fixes
- Swing-progress-driven mining/chopping/digging follow-through for vanilla pickaxes, axes, shovels and hoes; sword swing accent.
- Removed torso lean/roll and exaggerated walking leg offsets.
- Two-page Mod Menu config with reset defaults, movement and tools sections.
- Offhand-sensitive two-handed follow-through and main-arm handedness support.
- Existing armor/accessory model pivots preserved; compatibility with custom renderers is unverified.

## Install/build
Upload all extracted project files into the root of the GitHub repository (including `.github/workflows/build.yml`). Run the Build Fabric Mod action, download artifact and extract the normal (non-sources) JAR. Remove previous HPA versions from `mods` before installing. Back up your world and config.

## Limits
Not tested in game. No guaranteed compatibility with Better Combat, Not Enough Animations or custom tool classes. This is not an official Actions & Stuff port. GitHub Actions compilation must succeed before use.
