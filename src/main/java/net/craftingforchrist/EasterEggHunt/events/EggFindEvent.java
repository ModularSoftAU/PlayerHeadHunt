package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EggFindEvent implements Listener {
    public static EasterEggHuntMain plugin;
    public EggFindEvent(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String Username = player.getName();
        String UserUUID = player.getUniqueId().toString();
        EquipmentSlot EquipSlot = event.getHand();

        Block block = event.getClickedBlock();
        Material blockType = block.getType();
        int blockx = block.getX();
        int blocky = block.getY();
        int blockz = block.getZ();

        if (EquipSlot.equals(EquipmentSlot.OFF_HAND) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        player.sendMessage("You just clicked " + event.getClickedBlock().getType());

        if (blockType.equals(Material.PLAYER_HEAD) || blockType.equals(Material.PLAYER_WALL_HEAD)) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 100, 100);
            player.sendMessage("This is a Player Head.");

            //
            // Database Query
            // Insert Easter Egg
            //
            try {
                PreparedStatement insertstatement = plugin.getConnection().prepareStatement("INSERT INTO eastereggs (playerid, eggcordx, eggcordy, eggcordz) VALUES ((select id from playerdata where uuid=?), ?, ?, ?)");

                insertstatement.setString(1, UserUUID);
                insertstatement.setString(2, String.valueOf(blockx));
                insertstatement.setString(3, String.valueOf(blocky));
                insertstatement.setString(4, String.valueOf(blockz));

                insertstatement.executeUpdate();

                player.sendMessage(ChatColor.GREEN + "You found an Easter Egg!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
