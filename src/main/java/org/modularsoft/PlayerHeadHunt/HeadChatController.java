package org.modularsoft.PlayerHeadHunt;

import org.modularsoft.PlayerHeadHunt.helpers.DefaultFontInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class HeadChatController {
    private final PlayerHeadHuntMain plugin;

    public HeadChatController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
    }

    public void headAlreadyFoundResponse(Player player) {
        // Play sound for a Player Head that is already found.
        player.playSound(player.getLocation(), plugin.config().getHeadAlreadyFoundSound(), 1, 1);
        player.sendMessage(plugin.config().getLangHeadAlreadyFound());
    }

    public void headFoundResponse(Player player, int headCount) {
        String message = plugin.config().getLangHeadFound()
                .replace("%FOUNDHEADS%", headCount + "")
                .replace("%NUMBEROFHEADS%", "" + plugin.config().getTotalHeads());

        // Play sound for a Player Head that is found.
        player.playSound(player.getLocation(), plugin.config().getHeadFoundSound(), 1, 1);
        player.sendMessage(message);
    }

    public void headMilestoneReachedEvent(Player player, boolean isMajorSound, int heads) {
        if (!plugin.config().isMilestoneMessageFeatureEnabled())
            return;

        Sound majorSound = plugin.config().getMajorCollectionSound();
        Sound minorSound = plugin.config().getMinorCollectionSound();

        if (isMajorSound)
            player.playSound(player.getLocation(), majorSound, 1, 1);
        else
            player.playSound(player.getLocation(), minorSound, 1, 1);

        // Congratulate the player on their first head but don't tell everyone
        if (heads == 1) {
            player.sendMessage(plugin.config().getLangFirstHeadFound()
                    .replace("%PLAYER%", player.getName()));
            return;
        }

        // Tell other players about the milestone
        String broadcastMessage;
        if (heads == plugin.config().getTotalHeads()) {
            broadcastMessage = plugin.config().getLangLastHeadFound();
        } else {
            broadcastMessage = plugin.config().getLangHeadCollectionMilestoneReached();
        }
        broadcastMessage = broadcastMessage
                .replace("%PLAYER%", player.getName())
                .replace("%NUMBEROFHEADS%", String.valueOf(heads));

        World world = player.getWorld();
        for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
            otherPlayers.sendMessage(broadcastMessage);
            world.playSound(player.getLocation(), minorSound, 1, 1);
        }
    }

    public void newPlayerJoinsTheHunt(Player player) {
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Welcome Head Hunter.");
        player.sendMessage("Welcome Head Hunter to the Player Head Hunt. Explore the area and the fields outside and collect as many heads as you can.");
        player.sendMessage("Right Click to collect an Player Head and you will hear a ding when it is collected.");
        player.sendMessage(" ");
        player.sendMessage(ChatColor.YELLOW + "Happy Easter and happy hunting.\nFrom Crafting For Christ");
    }

    public void playersOwnHeadCountResponse(Player player) {
        // Players wants to see their own head count
        player.sendMessage(plugin.config().getLangHeadCount()
                .replace("%FOUNDHEADS%", "" + HeadQuery.foundHeadsCount(plugin, player))
                .replace("%NUMBEROFHEADS%", "" + plugin.config().getTotalHeads()));
    }

    public void playerClearedTheirHeadsResponse(Player player) {
        player.sendMessage("All heads have been cleared.");
    }

    /**
     * From: https://stackoverflow.com/a/6810409
     * This function converts a rank to its ordinal representation
     * @param rank Rank to convert to ordinal
     * @return The oridinal String
     */
    private static String rankToOrdinal(int rank) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        return switch (rank % 100) {
            case 11, 12, 13 -> rank + "th";
            default -> rank + suffixes[rank % 10];
        };
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * @param message The message to measure its length
     * @return The length of the message in pixels (" " characters)
     */
    private static int minecraftMessageLengthInPixels(String message) {
        if (message == null || message.equals(""))
            return 0;

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§'){
                previousCode = true;
            } else if (previousCode){
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        return messagePxSize;
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * @param pixels The number of pixels to pad
     * @return The padded String
     */
    private static String getPixelPadding(int pixels) {
        StringBuilder sb = new StringBuilder();
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        while(compensated < pixels){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * Sends the message to the chat such that it is in the middle of the chat box.
     * @param player The player sending the message
     * @param message The message to send
     * @param centrePixel The pixel to treat as the centre of the message
     */
    private static void sendMessageInCentre(Player player, String message, int centrePixel) {
        int messagePxSize = minecraftMessageLengthInPixels(message);
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = centrePixel - halvedMessageSize;
        player.sendMessage(getPixelPadding(toCompensate) + message);
    }

    public void showLeaderBoardResponse(Player player, List<HeadQuery.HeadHunter> bestHunters) {
        int centrePixel = minecraftMessageLengthInPixels(
                plugin.config().getLangLeaderboardHeader()) / 2;

        // Show the header first
        sendMessageInCentre(player, plugin.config().getLangLeaderboardHeader(), centrePixel);
        player.sendMessage("");

        if (bestHunters.size() == 0) {
            // If there are no hunters we should probably tell the player
            sendMessageInCentre(player, plugin.config().getLangLeaderboardNoHeads(), centrePixel);
        } else {
            for (int i = 0; i < bestHunters.size(); i++) {
                HeadQuery.HeadHunter hunter = bestHunters.get(i);

                // We probably shouldn't list players who have no heads.
                // Once we find a player with 0 heads then the rest will
                // also have 0 as it is sorted.
                if (hunter.headsCollected() == 0)
                    return;

                int rank = i + 1;
                String rankingColour = switch (rank) {
                    case 1 -> plugin.config().getLangLeaderboardFirstColour();
                    case 2 -> plugin.config().getLangLeaderboardSecondColour();
                    case 3 -> plugin.config().getLangLeaderboardThirdColour();
                    default -> "";
                };

                String rankingMessage = plugin.config().getLangLeaderboardFormat()
                        .replace("%COLOUR%", rankingColour)
                        .replace("%RANKING%", rankToOrdinal(rank))
                        .replace("%PLAYER%", hunter.name())
                        .replace("%NUMBEROFHEADS%", "" + hunter.headsCollected());
                sendMessageInCentre(player, rankingMessage, centrePixel);
            }
        }

        player.sendMessage("");
        sendMessageInCentre(player, plugin.config().getLangLeaderboardHeader(), centrePixel);
    }
}
