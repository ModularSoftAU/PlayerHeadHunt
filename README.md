# PlayerHeadHunt

## About

PlayerHeadHunt is a Minecraft minigame plugin that allows server administrators to set up a head-hunting game. Players can search for hidden heads within a defined region and compete to find them all. The plugin is highly configurable, allowing for custom messages, sounds, and rewards.

## Features

*   **Customizable Hunt Region:** Define a specific area for the head hunt using WorldEdit.
*   **Configurable Heads:** Use any block as a "head," not just player heads. Customize the skins of player heads.
*   **Milestone Rewards:** Reward players with items (like helmets) and broadcast messages when they reach certain milestones.
*   **Leaderboard:** Display the top head hunters on the server.
*   **Customizable Messages and Sounds:** Configure all user-facing messages and sounds to match your server's theme.
*   **Database Support:** Player data is stored in a `player-data.yml` file.

## Commands

| Command             | Description                                       | Usage                                     | Aliases         |
| ------------------- | ------------------------------------------------- | ----------------------------------------- | --------------- |
| `/heads`            | Shows the number of heads you have found.         | `/heads`                                  | `eggs`, `presents` |
| `/leaderboard`      | Shows the top 5 head hunters on the server.       | `/leaderboard`                            | `lb`            |
| `/debugheadhunt`    | Debug command for developers.                      | `/debugheadhunt <clearheads|countheads>` | `dhh`           |

## Installation

1.  Install [WorldEdit](https://dev.bukkit.org/projects/worldedit).
2.  Download the latest version of PlayerHeadHunt.
3.  Place the `PlayerHeadHunt.jar` file in your server's `plugins` directory.
4.  Restart your server.
5.  Configure the plugin by editing the `config.yml` file in the `plugins/PlayerHeadHunt` directory.

## Configuration

The `config.yml` file is heavily commented and allows you to customize many aspects of the plugin.

### `REGION`

This section defines the area where heads will be placed. You need to set the coordinates for the upper and lower corners of the region.

```yaml
REGION:
  UPPERREGION:
    X: -54
    Y: 255 # Do not change this, this expands vertically.
    Z: 48
  LOWERREGION:
    X: -33
    Y: 0 # Do not change this, this expands vertically.
    Z: 4
```

### `FEATURE`

Enable or disable certain features of the plugin.

```yaml
FEATURE:
  MILESTONEHAT: TRUE
  MILESTONEMESSAGE: TRUE
```

*   `MILESTONEHAT`: If `true`, players will receive a helmet as a reward for reaching a milestone.
*   `MILESTONEMESSAGE`: If `true`, a message will be broadcasted when a player reaches a milestone.

### `HEAD`

Configure the heads themselves.

```yaml
HEAD:
  HEADTOTAL:
  HEADBLOCK: PLAYER_HEAD
  RESPAWNTIMER: 1200 # Default is 1200 (1 minute)
  SKINSMAX: 9
  SKINS:
    0: "..."
    1: "..."
    # ...
```

*   `HEADTOTAL`: The total number of heads in the region. This is calculated automatically.
*   `HEADBLOCK`: The material of the block to be used as a head. A list of materials can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).
*   `RESPAWNTIMER`: The time in seconds it takes for a head to respawn after being found.
*   `SKINSMAX`: The number of skins to use for the heads.
*   `SKINS`: A list of base64 encoded skins for the player heads.

### `SOUND`

Customize the sounds played by the plugin. A list of sounds can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).

```yaml
SOUND:
  HEADFOUND: "BLOCK_NOTE_BLOCK_SNARE"
  HEADALREADYFOUND: "BLOCK_NOTE_BLOCK_BASS"
  MINORCOLLECTIONMILESTONE: "ENTITY_PLAYER_LEVELUP"
  MAJORCOLLECTIONMILESTONE: "UI_TOAST_CHALLENGE_COMPLETE"
```

### `MILESTONES`

Define the milestones for the head hunt.

```yaml
MILESTONES:
  MINOR:
    - 16
    - 32
    # ...
  MAJOR:
    - 128
    - 256
  # ...
  LEATHERHELMET: 16
  CHAINMAILHELMET: 32
  # ...
```

*   `MINOR` and `MAJOR`: Lists of head counts that trigger milestone events.
*   `LEATHERHELMET`, `CHAINMAILHELMET`, etc.: The number of heads a player must find to receive the corresponding helmet.

### `LANG`

Customize all user-facing messages.

```yaml
LANG:
  DATABASE:
    CONNECTIONERROR: "&cA database error has occurred, please contact an Administrator."
    # ...
  COMMAND:
    # ...
  HEAD:
    # ...
  LEADERBOARD:
    # ...
```

## For Developers

The source code is available on [GitHub](https://github.com/ModularSoftAU/PlayerHeadHunt). Feel free to contribute to the project by creating issues or pull requests.