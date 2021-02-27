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
import java.sql.ResultSet;
import java.sql.SQLException;

public class EggMilestoneReachedEvent implements Listener {
    public static EasterEggHuntMain plugin;
    public EggMilestoneReachedEvent(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String Username = player.getName();
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check if the player has already found that Easter Egg before.
        //
        try {
            PreparedStatement findstatement = plugin.getConnection().prepareStatement("select count(*) as 'eastereggs' from eastereggs where playerid = (select id from playerdata where uuid=?)");
            findstatement.setString(1, UserUUID);

            ResultSet results = findstatement.executeQuery();
            if (results.next()) {
                int totaleggs = results.getInt("eastereggs");

                switch(totaleggs) {
                    case 10:
                        MilestoneReachEvent(player, totaleggs);
                        event.setCancelled(true);
                        break;
                    case 30:
                        MilestoneReachEvent(player, totaleggs);
                        event.setCancelled(true);
                        break;
//                    case 50:
//                        // code block
//                        break;
//                    case 60:
//                        // code block
//                        break;
//                    case 80:
//                        // code block
//                        break;
//                    case 90:
//                        // code block
//                        break;
//                    case 100:
//                        // code block
//                        break;
//                    case 150:
//                        // code block
//                        break;
//                    case 200:
//                        // code block
//                        break;
                    default:
                        // code block
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }

    }

    public void MilestoneReachEvent(Player player, int totaleggs) {
        String MILESTONEREACHEDMESSAGE = plugin.getConfig().getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED");

        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 100, 100);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', MILESTONEREACHEDMESSAGE.replace("%PLAYER%", player.getName()).replace("%NUMBEROFEGGS%", String.valueOf(totaleggs))));
    }

}
