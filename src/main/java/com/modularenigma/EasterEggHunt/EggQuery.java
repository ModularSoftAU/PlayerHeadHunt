package com.modularenigma.EasterEggHunt;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EggQuery {
    public record EggHunter(@Getter String name, @Getter int eggsCollected) { }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player to check
     * @return Returns the number of eggs found by the player
     */
    public static int foundEggsCount(EasterEggHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check how many eggs the player has collected.
            PreparedStatement foundEggsCount = plugin.getConnection().prepareStatement(
                    "SELECT eggsCollected AS 'eastereggs' FROM playerdata WHERE uuid=?");
            foundEggsCount.setString(1, playerUUID);
            ResultSet results = foundEggsCount.executeQuery();

            if (results.next()) return results.getInt("eastereggs");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    /**
     * Clears the number of eggs found by the player to 0
     * @param plugin The EasterEggHunt main plugin
     * @param player The player to reset
     * @return Returns true if the clear was successful.
     */
    public static boolean clearEggs(EasterEggHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement clearEggsStatement = plugin.getConnection().prepareStatement(
                    "DELETE FROM eastereggs WHERE playerid=(SELECT id FROM playerdata WHERE uuid=?)");
            clearEggsStatement.setString(1, playerUUID);
            clearEggsStatement.executeUpdate();

            PreparedStatement resetEggCountStatement = plugin.getConnection().prepareStatement(
                    "UPDATE playerdata SET eggsCollected = 0 WHERE uuid = ?");
            resetEggCountStatement.setString(1, playerUUID);
            resetEggCountStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }

        return false;
    }

    /**
     * Checks if the player has found an egg in the specified position before
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who found the egg
     * @param x X position of the egg
     * @param y Y position of the egg
     * @param z Z position of the egg
     * @return True if the egg has already been found
     */
    public static boolean hasAlreadyCollectedEgg(EasterEggHuntMain plugin, Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();

        try {
            // Check if the player has already found that Easter Egg before.
            PreparedStatement hasAlreadyFoundEggStatement = plugin.getConnection().prepareStatement(
                    "SELECT e.* FROM eastereggs e JOIN playerdata p ON e.playerid = p.id WHERE p.uuid = ? AND eggcordx=? AND eggcordy=? AND eggcordz=?");
            hasAlreadyFoundEggStatement.setString(1, playerUUID);
            hasAlreadyFoundEggStatement.setString(2, "" + x);
            hasAlreadyFoundEggStatement.setString(3, "" + y);
            hasAlreadyFoundEggStatement.setString(4, "" + z);
            ResultSet results = hasAlreadyFoundEggStatement.executeQuery();

            // Return's true if we already found the egg.
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return false;
    }

    /**
     * Ties the location of the egg (x, y, z) to the player so that we know in
     * the future that this egg has been found
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who found the egg
     * @param x X position of the egg
     * @param y Y position of the egg
     * @param z Z position of the egg
     */
    public static void insertCollectedEgg(EasterEggHuntMain plugin, Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();

        try {
            // Insert Easter Egg
            PreparedStatement insertCollectedEggStatement = plugin.getConnection().prepareStatement(
                    "INSERT INTO eastereggs (playerid, eggcordx, eggcordy, eggcordz) " +
                            "VALUES ((SELECT id FROM playerdata WHERE uuid=?), ?, ?, ?)");
            insertCollectedEggStatement.setString(1, playerUUID);
            insertCollectedEggStatement.setString(2, String.valueOf(x));
            insertCollectedEggStatement.setString(3, String.valueOf(y));
            insertCollectedEggStatement.setString(4, String.valueOf(z));
            insertCollectedEggStatement.executeUpdate();

            PreparedStatement updatePlayersEggsCollectedStatement = plugin.getConnection().prepareStatement(
                    "UPDATE playerdata SET eggsCollected = eggsCollected + 1 WHERE uuid = ?");
            updatePlayersEggsCollectedStatement.setString(1, "" + player.getUniqueId());
            updatePlayersEggsCollectedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
    }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who joined
     * @return Returns true if the player specified was indeed a new player.
     */
    public static boolean addNewHunter(EasterEggHuntMain plugin, Player player) {
        String playerUUID = player.getUniqueId().toString();
        String username = player.getName();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement findstatement = plugin.getConnection().prepareStatement(
                    "SELECT * FROM playerdata WHERE uuid=?");
            findstatement.setString(1, playerUUID);
            ResultSet results = findstatement.executeQuery();

            // The player already exists
            if (results.next())
                return false;

            PreparedStatement insertstatement = plugin.getConnection().prepareStatement(
                    "INSERT INTO playerdata (uuid, username) VALUES (?, ?)");
            insertstatement.setString(1, playerUUID);
            insertstatement.setString(2, username);
            insertstatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return false;
    }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who issued the command
     * @return Returns a list of the Best Hunters. idx 0 is the best player and so on...
     */
    public static List<EggHunter> getBestHunters(EasterEggHuntMain plugin, Player player, int topHunters) {
        List<EggHunter> bestHunters = new ArrayList<>();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement getEggHuntersStatement = plugin.getConnection().prepareStatement(
                    "SELECT username, eggsCollected, id FROM playerdata ORDER BY eggsCollected DESC LIMIT ?");
            getEggHuntersStatement.setInt(1, topHunters);
            ResultSet results = getEggHuntersStatement.executeQuery();

            // The player already exists
            while (results.next()) {
                String name = results.getString("username");
                int eggsCollected = results.getInt("eggsCollected");
                bestHunters.add(new EggHunter(name, eggsCollected));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return bestHunters;
    }
}
