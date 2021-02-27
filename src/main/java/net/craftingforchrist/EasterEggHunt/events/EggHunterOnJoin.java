package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EggHunterOnJoin implements Listener {
    EasterEggHuntMain plugin;
    public EggHunterOnJoin(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String UserUUID = player.getUniqueId().toString();
        String Username = player.getName();

        //
        // Database Query
        // Check if a player has been added into the database already.
        //
        try {
            PreparedStatement findstatement = plugin.getConnection().prepareStatement("SELECT * FROM playerdata WHERE uuid=?");
            findstatement.setString(1, UserUUID);

            ResultSet results = findstatement.executeQuery();
            if (!results.next()) {
                plugin.getServer().getConsoleSender().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', Username + " is a new player, creating a player profile.")));
                PreparedStatement insertstatement = plugin.getConnection().prepareStatement("INSERT INTO playerdata (uuid, username) VALUES (?, ?)");

                insertstatement.setString(1, UserUUID);
                insertstatement.setString(2, Username);

                insertstatement.executeUpdate();
                plugin.getServer().getConsoleSender().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "Added a new hunter, " + Username + ".")));
                newHunterApproaches(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }

    }

    public void newHunterApproaches(Player player) {
        player.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN + "Welcome Egg Hunter.");
        player.sendMessage("Welcome Egg Hunter to the Easter Egg Hunt. explore our Hub and the fields outside and collect as many eggs as you can.");
        player.sendMessage("Right Click to collect an Easter Egg and you will hear a ding when it is collected.");
        player.sendMessage();
        player.sendMessage(ChatColor.YELLOW + "Happy Easter and happy hunting.\nFrom Crafting For Christ and Katoomba Easter Convention");
    }

}
