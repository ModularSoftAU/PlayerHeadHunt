# Easter Egg Hunt

## Dependencies
- `WorldEdit` is **required** since it is used to count all of the eggs in the hunting region.

## Gameplay
When a player right clicks a Player Head, the coordinates are logged in a database and a number incremented in their name.

#### Milestones
When a player finds over an X amount of eggs, a message will broadcasted to all online players that they have reached a milestone.
The milestones are hardcoded and at this time cannot be changed, these milestones are `10, 50, 100, 150, 200, 500`.

##### Milestone Helmets
To symbolise to other people in the Egg Hunt where people are at, every goal achieved in the table below is given a helmet to visualise how much someone has progressed in the Hunt.

| Number | Helmet    |
|--------|-----------|
| 50     | Leather   |
| 100    | Chainmail |
| 150    | Iron      |
| 200    | Gold      |
| 250    | Diamond   |
| 300    | Netherite |

#### Egg Collection Cooldown
To avoid hunters following other players to collect their eggs, eggs will disappear and reappear in a configurable option `EGG.RESPAWNTIMER`

## Commands
| Command      | Description                                   | Permission                |
|--------------|-----------------------------------------------|---------------------------|
| /eggs        | Grab the amount of eggs you have.             |                           |
| /cleareggs   | Clear all eggs from yourself.                 | `easteregghunt.clearegg`  |
| /counteggs   | Recalculates the number of eggs in the world. | `easteregghunt.counteggs` |
| /leaderboard | Show the 5 best egg hunters in the world.     |                           |
