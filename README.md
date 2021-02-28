# Easter Egg Hunt
Maintained By: [Ben Robson](https://github.com/benrobson) <br>
<i>The Easter Egg Hunt is an Easter Break Hub event and a collaboration with KEC (Katoomba Easter Convention).</i>

## Gameplay
When a player right clicks a Player Head, the coordinates are logged in a database and a number incremented in their name.

#### Milestones
When a player finds over an X amount of eggs, a message will broadcasted to all online players that they have reached a milestone.
The milestones are hardcoded and at this time cannot be changed, these milestones are `10, 50, 100, 150, 200, 500`.

#### Egg Collection Cooldown
To avoid hunters following other players to collect their eggs, eggs will disappear and reappear in a configurable option `EGG.RESPAWNTIMER`

## Commands
| Command        | Description                            | Permission                |
|----------------|----------------------------------------|---------------------------|
| /egg           | Grab the amount of eggs a hunter has. |                          |
| /clearegg      | Clear all eggs from targeted player.  | `easteregghunt.clearegg` |