package net.craftingforchrist.EasterEggHunt;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EggHatController {
    private static EasterEggHuntMain plugin;
    public EggHatController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public static void clearEggs(Player player) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement deletestatement = plugin.getConnection().prepareStatement("DELETE from eastereggs where playerid=(select id from playerdata where uuid=?)");
            deletestatement.setString(1, UserUUID);

            deletestatement.executeUpdate();
            player.sendMessage("All eggs have been cleared.");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }
    }

}
