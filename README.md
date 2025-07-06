# Bats N' Berries
![bats and berries](https://github.com/user-attachments/assets/af13a899-ffca-4e10-960b-401529f3e2dc)

A very silly Minecraft server-side mod for Fabric that makes bats and glow berries a little more useful.

## Features
- When a player or mob eats a glow berry, they gain the glowing effect for 10 seconds (200 ticks). The duration is configurable with the `glowBerryEffectDuration` gamerule.
- Feeding a glow berry to a bat will make it "echolocate", giving all players and mobs the glowing effect in a 32-block radius for 7 seconds (140 ticks), configurable with the `batSonarMaxDistance` and `batSonarEffectDuration` gamerules.
- Enables attaching leads to bats.
- Use an empty bucket on a bat to get a Bucket of Bat. Now you have a friend to bring on your adventures!

## Supported Languages
- English (US)
- Shakespearean English (UK)
- Pirate Speak (7 Seas)
- ɥsᴉꞁᵷuƎ (AU)
- LOLCAT (:3)
- French (FR)

## Installation
Bats N' Berries is a *server-side only* mod. When installed on a server, players can connect without installing anything. Installing on the client has no effect.

Assuming your server is already running a compatible version of [Fabric](https://fabricmc.net/use/server/) and the [Fabric API](https://modrinth.com/mod/fabric-api), then you should be able to just drop the jar into your mods folder!

You should also install [Polymer](https://modrinth.com/mod/polymer) to host the resource pack for clients. It's optional, but highly recommended. Otherwise, the Bucket of Bat will use the bat spawn egg texture by default.

> [!NOTE]
> If you use Polymer, make sure to [enable AutoHost](https://polymer.pb4.eu/latest/user/resource-pack-hosting/).
> You can also mark it as required, which prevents players from connecting without downloading the pack.

## License
This mod is licensed under the Unlicense. See [LICENSE](LICENSE).

This project uses [Polymer](https://github.com/Patbox/polymer) under the [LGPL 3.0 License](https://github.com/Patbox/polymer/blob/HEAD/LICENSE).
