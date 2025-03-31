![img](https://github.com/ModularSoftAU/assets/blob/master/playerheadhunt/playerheadhunt-icon-text-256.png?raw=true)

## Installation
* Clone this repo.
* Configure your config.
* Run the dbinit.

## Requirements
* MySQL Database.

## Dependencies
- `WorldEdit` is **required** since it is used to count all the heads in the hunting region.

## Gameplay
When a player right-clicks a Player Head, the coordinates are logged in a database and a number incremented in their name.

#### Milestones
When a player finds over an X amount of heads, a message will broadcast to all online players that they have reached a milestone.
The milestones are hardcoded and at this time cannot be changed, these milestones are `10, 50, 100, 150, 200, 500`.

##### Milestone Helmets
To symbolise to other people in the Head Hunt where people are at, every goal achieved in the table below is given a helmet to visualise how much someone has progressed in the Hunt.

| Number | Helmet    |
|--------|-----------|
| 50     | Leather   |
| 100    | Chainmail |
| 150    | Iron      |
| 200    | Gold      |
| 250    | Diamond   |
| 300    | Netherite |

#### Head Collection Cooldown
To avoid hunters following other players to collect their heads, heads will disappear and reappear in a configurable option `HEAD.RESPAWNTIMER`

## Commands
| Command      | Description                                    | Permission                  |
|--------------|------------------------------------------------|-----------------------------|
| /heads       | Grab the amount of heads you have.             |                             |
| /clearheads  | Clear all heads from yourself.                 | `playerheadhunt.clearhead`  |
| /countheads  | Recalculates the number of heads in the world. | `playerheadhunt.countheads` |
| /leaderboard | Show the 5 best head hunters on the Server.    |                             |