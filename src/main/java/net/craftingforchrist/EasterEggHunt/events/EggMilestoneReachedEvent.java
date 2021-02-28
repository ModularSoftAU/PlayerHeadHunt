package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
        EquipmentSlot EquipSlot = event.getHand();
        Block block = event.getClickedBlock();
        Material blockType = block.getType();

        String MAJORMILESTONESOUND = plugin.getConfig().getString("SOUND.MAJORMILESTONE");
        String MINORMILESTONESOUND = plugin.getConfig().getString("SOUND.MINORMILESTONE");

        // This stops the event from firing twice, since the event fires for each hand.
        if (EquipSlot.equals(EquipmentSlot.OFF_HAND) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

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

                if (blockType.equals(Material.PLAYER_HEAD) || blockType.equals(Material.PLAYER_WALL_HEAD)) {
                    switch(totaleggs) {
                        case 10:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MINORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        case 50:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MINORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        case 100:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MAJORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        case 150:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MINORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        case 200:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MINORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        case 500:
                            MilestoneReachEvent(player, Sound.valueOf(String.valueOf(MINORMILESTONESOUND)), totaleggs);
                            event.setCancelled(true);
                            break;
                        default:
                            // code block
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }

    }

    public void MilestoneReachEvent(Player player, Sound EggSound, int totaleggs) {
        String MILESTONEREACHEDMESSAGE = plugin.getConfig().getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED");

        player.playSound(player.getLocation(), EggSound, 1, 1);

        Bukkit.getServer().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&', MILESTONEREACHEDMESSAGE.replace("%PLAYER%", player.getName()).replace("%NUMBEROFEGGS%", String.valueOf(totaleggs)))));
    }

}
