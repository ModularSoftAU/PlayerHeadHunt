package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EggFindEvent implements Listener {

    EasterEggHuntMain plugin;

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        player.sendMessage("You just clicked " + event.getClickedBlock().getType());

        if (block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD)) {

            player.sendMessage("This is a Player Head.");
        }
    }

}
