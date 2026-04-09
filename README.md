![img](https://github.com/ModularSoftAU/assets/blob/master/playerheadhunt/playerheadhunt-icon-text-256.png?raw=true)

A Minecraft plugin where players hunt and collect player heads hidden across the server hub. Tracks progress, announces milestones, rewards collectors with helmets, and provides a compass to help find uncollected heads.

## Requirements

- **Paper** 1.20.4+
- **WorldEdit** (required — used to count heads in the hunt region)
- **LuckPerms** (required — used for leaderboard exclusion permissions)

## Installation

1. Drop the compiled `.jar` into your server's `plugins/` folder.
2. Start the server once to generate `plugins/PlayerHeadHunt/config.yml`.
3. Configure the file (see [Configuration](#configuration) below).
4. Restart the server.

No database setup is required. All player data is stored in `plugins/PlayerHeadHunt/player-data.yml`.

## Gameplay

### Collecting Heads

Players right-click a **Player Head** block to collect it. The plugin:

- Records the head's coordinates against the player's profile.
- Temporarily removes the head block and replaces it after the configured respawn timer (default: 1 minute), applying a random skin from the configured skin pool.
- Sends the player a message showing their progress and how many other players have found the same head.

### Milestones

When a player reaches a milestone head count, a server-wide broadcast is sent. Milestones come in two tiers:

| Tier  | Counts |
|-------|--------|
| Minor | 10, 25, 50, 75, 100, 200, 300, 400, 500, 600 |
| Major | 156, 314, 468, 625 |

### Milestone Helmets

At certain counts, a helmet is automatically equipped to display a player's progress to others:

| Count | Helmet    |
|-------|-----------|
| 25    | Leather   |
| 100   | Chainmail |
| 156   | Iron      |
| 314   | Golden    |
| 468   | Diamond   |
| 625   | Netherite |

### Head Compass

Players receive a **Head Compass** locked to hotbar slot 8 (configurable). It cannot be dropped, moved, or swapped to the off-hand.

The compass operates in three modes:

| Mode | Behaviour |
|------|-----------|
| **Death Compass** | Points to spawn / bed spawn (default state) |
| **Head Tracking** | Points toward the nearest uncollected head |
| **Recharging** | Cooling down after a tracked head was collected |

**Logic flow:**

- Every 5 minutes (configurable), the compass scans for the nearest uncollected head in the hunt region (same world only).
- If one is found, the compass switches to **Head Tracking** and points toward it. The player is notified.
- When the player collects the tracked head, the compass enters **Recharging** for 5 minutes (configurable), then automatically scans again.
- If a tracked head is collected by someone else, despawns, or is otherwise removed, the compass reverts to **Death Compass** mode and waits for the next scan.
- If no uncollected heads remain, the compass stays in **Death Compass** mode.
- Compass state (mode, target, cooldown) persists across disconnects.

### Discord Leaderboard Webhook

A daily leaderboard summary can be automatically posted to a Discord channel via webhook. The send time is configurable. This feature can be disabled in `config.yml`.

## Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/heads` | `/eggs`, `/presents` | Show your current head count. | — |
| `/leaderboard` | `/lb` | Display the top 5 head hunters. | — |
| `/debugheadhunt clearheads <player>` | `/dhh` | Clear all collected heads for a player. | `playerheadhunt.debug` + OP |
| `/debugheadhunt countheads` | `/dhh` | Recount all heads in the hunt region. | `playerheadhunt.debug` + OP |
| `/debugheadhunt firewebhook` | `/dhh` | Manually trigger the Discord webhook. | `playerheadhunt.debug` + OP |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `playerheadhunt.debug` | Access to `/debugheadhunt` subcommands (also requires OP). | `false` |
| `playerheadhunt.leaderboard.exclude` | Exclude the player from the leaderboard. | `false` |

## Configuration

```yaml
# Defines the cuboid region within "world" where heads are counted and tracked.
REGION:
  UPPERREGION:
    X: 300
    Y: 255   # Do not change — expands vertically to the top of the world.
    Z: 300
  LOWERREGION:
    X: -300
    Y: 0     # Do not change — expands vertically to the bottom of the world.
    Z: -300

# Feature toggles
FEATURE:
  MILESTONEHAT: TRUE              # Give milestone helmets to players.
  MILESTONEMESSAGE: TRUE          # Broadcast milestone announcements.
  LEADERBOARDDAILYWEBHOOK: TRUE   # Send daily leaderboard to Discord.

# Discord webhook settings
DISCORD:
  WEBHOOKURL: "https://discord.com/api/webhooks/..."
  HOUR: 7      # Hour (24h) to send the daily leaderboard.
  MINUTE: 15   # Minute to send the daily leaderboard.

# Head Compass settings
COMPASS:
  ENABLED: TRUE
  SLOT: 8                     # Hotbar slot (0–8) the compass is locked to.
  COOLDOWN_TICKS: 6000        # Recharge time after collecting tracked head (6000 = 5 min).
  SCAN_INTERVAL_TICKS: 6000   # How often the compass scans for a new target (6000 = 5 min).
  ITEM_NAME: "&bHead Compass"

# Head block settings
HEAD:
  HEADTOTAL:               # Auto-populated at startup — do not edit manually.
  HEADBLOCK: PLAYER_HEAD
  RESPAWNTIMER: 1200       # Ticks before a collected head reappears (1200 = 1 min).
  SKINSMAX: 9              # Number of skins in the pool (0-indexed, so 9 = indices 0–8).
  SKINS:
    0: "<base64>"
    # ...

# Sounds played on various events
SOUND:
  HEADFOUND: "BLOCK_NOTE_BLOCK_SNARE"
  HEADALREADYFOUND: "BLOCK_NOTE_BLOCK_BASS"
  MINORCOLLECTIONMILESTONE: "ENTITY_PLAYER_LEVELUP"
  MAJORCOLLECTIONMILESTONE: "UI_TOAST_CHALLENGE_COMPLETE"

# Milestone thresholds
MILESTONES:
  MINOR:
    - 10
    - 25
    - 50
    - 75
    - 100
    - 200
    - 300
    - 400
    - 500
    - 600
  MAJOR:
    - 156
    - 314
    - 468
    - 625
  LEATHERHELMET: 25
  CHAINMAILHELMET: 100
  IRONHELMET: 156
  GOLDENHELMET: 314
  DIAMONDHELMET: 468
  NETHERITEHELMET: 625   # Should equal the total head count for best results.

# All player-facing messages (supports & colour codes and placeholders)
LANG:
  # ...
```

### Placeholders

| Placeholder | Used in | Meaning |
|-------------|---------|---------|
| `%PLAYER%` | Various | Player's display name |
| `%NUMBEROFHEADS%` | Various | Total heads in the region |
| `%FOUNDHEADS%` | `HEAD.HEADFOUND` | Heads the player has collected |
| `%OTHERPLAYERSFOUNDHEAD%` | Head-find messages | Number of other players who found the same head |
| `%RANKING%` | Leaderboard | Position number |
| `%COLOUR%` | Leaderboard | Colour prefix for ranking |

## Data Storage

Player data is stored in `plugins/PlayerHeadHunt/player-data.yml`. Each entry is keyed by UUID:

```yaml
<player-uuid>:
  username: "PlayerName"
  headsCollected:
    - { x: 10, y: 64, z: 20 }
    - { x: 50, y: 64, z: -30 }
  headsCollectedCount: 2
  compassMode: "DEATH_COMPASS"   # DEATH_COMPASS | HEAD_TRACKING | RECHARGING
  compassTrackedX: null
  compassTrackedY: null
  compassTrackedZ: null
  compassCooldownUntil: 0        # Unix epoch milliseconds
```
