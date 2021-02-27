package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

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
        String BlockMaterial = block.getType().name();
        int blockx = block.getX();
        int blocky = block.getY();
        int blockz = block.getZ();

        // This is to ensure that the event doesn't get fired twice, event is always fired twice for both hands.
        if (EquipSlot.equals(EquipmentSlot.OFF_HAND) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        Iterator EggBlocks = plugin.getConfig().getStringList("EGG.EGGBLOCK").iterator();

        while (EggBlocks.hasNext()) {
            String EggBlock = (String)EggBlocks.next();

            if (BlockMaterial.equalsIgnoreCase(EggBlock)) {
                //
                // Database Query
                // Check if the player has already found that Easter Egg before.
                //
                try {
                    PreparedStatement findstatement = plugin.getConnection().prepareStatement("SELECT * FROM eastereggs WHERE playerid=(select id from playerdata where uuid=?) AND eggcordx=? AND eggcordy=? AND eggcordz=?");
                    findstatement.setString(1, UserUUID);
                    findstatement.setString(2, String.valueOf(blockx));
                    findstatement.setString(3, String.valueOf(blocky));
                    findstatement.setString(4, String.valueOf(blockz));

                    ResultSet results = findstatement.executeQuery();
                    if (!results.next()) {
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

                            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 100, 100); // Play sound for an Easter Egg that is found.
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGFOUND")));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 100, 100); // Play sound for an Easter Egg that is already found.
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGALREADYFOUND")));
                        event.setCancelled(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
                    event.setCancelled(true);
                }
            }
        }
    }
}
