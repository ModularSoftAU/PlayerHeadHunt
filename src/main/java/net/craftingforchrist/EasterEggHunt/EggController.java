package net.craftingforchrist.EasterEggHunt;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EggController {
    public static EasterEggHuntMain plugin;
    public EggController(EasterEggHuntMain instance) {
        plugin = instance;
    }

    public boolean hasHunterFoundEgg(Player player, int blockx, int blocky, int blockz) {
        String UserUUID = player.getUniqueId().toString();

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

                    player.playSound(player.getLocation(), Sound.valueOf(String.valueOf(EGGFOUNDSOUND)), 1, 1); // Play sound for an Easter Egg that is found.
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGFOUND")));
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
                }
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(String.valueOf(EGGALREADYFOUNDSOUND)), 1, 1); // Play sound for an Easter Egg that is already found.
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGALREADYFOUND")));
                event.setCancelled(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
            event.setCancelled(true);
        }


        return true;
    }
}
