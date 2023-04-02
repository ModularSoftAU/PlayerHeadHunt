package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HeadQuery {
    public record HeadHunter(@Getter String name, @Getter int headsCollected) { }

    /**
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player to check
     * @return Returns the number of heads found by the player
     */
    public static int foundHeadsCount(PlayerHeadHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check how many heads the player has collected.
            PreparedStatement foundHeadsCount = plugin.getConnection().prepareStatement(
                    "SELECT headsCollected AS 'heads' FROM playerdata WHERE uuid=?");
            foundHeadsCount.setString(1, playerUUID);
            ResultSet results = foundHeadsCount.executeQuery();

            if (results.next()) return results.getInt("heads");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    /**
     * Clears the number of heads found by the player to 0
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player to reset
     * @return Returns true if the clear was successful.
     */
    public static boolean clearHeads(PlayerHeadHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        //
        // Database Query
        // Check how many heads the player has collected.
        //
        try {
            PreparedStatement clearHeadsStatement = plugin.getConnection().prepareStatement(
                    "DELETE FROM heads WHERE playerid=(SELECT id FROM playerdata WHERE uuid=?)");
            clearHeadsStatement.setString(1, playerUUID);
            clearHeadsStatement.executeUpdate();

            PreparedStatement resetHeadCountStatement = plugin.getConnection().prepareStatement(
                    "UPDATE playerdata SET headsCollected = 0 WHERE uuid = ?");
            resetHeadCountStatement.setString(1, playerUUID);
            resetHeadCountStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }

        return false;
    }

    /**
     * Checks if the player has found a head in the specified position before
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player who found the head
     * @param x X position of the head
     * @param y Y position of the head
     * @param z Z position of the head
     * @return True if the head has already been found
     */
    public static boolean hasAlreadyCollectedHead(PlayerHeadHuntMain plugin, Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();

        try {
            // Check if the player has already found that Player Head before.
            PreparedStatement hasAlreadyFoundHeadStatement = plugin.getConnection().prepareStatement(
                    "SELECT e.* FROM heads e JOIN playerdata p ON e.playerid = p.id WHERE p.uuid = ? AND headcordx=? AND headcordy=? AND headcordz=?");
            hasAlreadyFoundHeadStatement.setString(1, playerUUID);
            hasAlreadyFoundHeadStatement.setString(2, "" + x);
            hasAlreadyFoundHeadStatement.setString(3, "" + y);
            hasAlreadyFoundHeadStatement.setString(4, "" + z);
            ResultSet results = hasAlreadyFoundHeadStatement.executeQuery();

            // Return's true if we already found the head.
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return false;
    }

    /**
     * Ties the location of the head (x, y, z) to the player so that we know in
     * the future that this head has been found
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player who found the head
     * @param x X position of the head
     * @param y Y position of the head
     * @param z Z position of the head
     */
    public static void insertCollectedHead(PlayerHeadHuntMain plugin, Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();

        try {
            // Insert Player Head
            PreparedStatement insertCollectedHeadStatement = plugin.getConnection().prepareStatement(
                    "INSERT INTO heads (playerid, headcordx, headcordy, headcordz) " +
                            "VALUES ((SELECT id FROM playerdata WHERE uuid=?), ?, ?, ?)");
            insertCollectedHeadStatement.setString(1, playerUUID);
            insertCollectedHeadStatement.setString(2, String.valueOf(x));
            insertCollectedHeadStatement.setString(3, String.valueOf(y));
            insertCollectedHeadStatement.setString(4, String.valueOf(z));
            insertCollectedHeadStatement.executeUpdate();

            PreparedStatement updatePlayersHeadsCollectedStatement = plugin.getConnection().prepareStatement(
                    "UPDATE heads SET headsCollected = headsCollected + 1 WHERE uuid = ?");
            updatePlayersHeadsCollectedStatement.setString(1, "" + player.getUniqueId());
            updatePlayersHeadsCollectedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
    }

    /**
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player who joined
     * @return Returns true if the player specified was indeed a new player.
     */
    public static boolean addNewHunter(PlayerHeadHuntMain plugin, Player player) {
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
     * @param plugin The PlayerHeadHunt main plugin
     * @param player The player who issued the command
     * @return Returns a list of the Best Hunters. idx 0 is the best player and so on...
     */
    public static List<HeadHunter> getBestHunters(PlayerHeadHuntMain plugin, Player player, int topHunters) {
        List<HeadHunter> bestHunters = new ArrayList<>();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement getHeadHuntersStatement = plugin.getConnection().prepareStatement(
                    "SELECT username, headsCollected, id FROM playerdata ORDER BY headsCollected DESC LIMIT ?");
            getHeadHuntersStatement.setInt(1, topHunters);
            ResultSet results = getHeadHuntersStatement.executeQuery();

            // The player already exists
            while (results.next()) {
                String name = results.getString("username");
                int headsCollected = results.getInt("headsCollected");
                bestHunters.add(new HeadHunter(name, headsCollected));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return bestHunters;
    }
}
