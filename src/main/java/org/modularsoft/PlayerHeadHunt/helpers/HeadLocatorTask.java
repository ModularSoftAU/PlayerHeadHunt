package org.modularsoft.PlayerHeadHunt.helpers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.modularsoft.PlayerHeadHunt.HeadWorldController;
import org.modularsoft.PlayerHeadHunt.items.HeadLocator;

public class HeadLocatorTask {
    private final JavaPlugin plugin;
    private final HeadLocator headLocator;
    private final HeadWorldController headWorldController;

    public HeadLocatorTask(JavaPlugin plugin, HeadLocator headLocator, HeadWorldController headWorldController) {
        this.plugin = plugin;
        this.headLocator = headLocator;
        this.headWorldController = headWorldController;
    }

    public void startTask(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Check if the player is online
            if (!player.isOnline()) {
                return; // Stop if the player is offline
            }

            // Check if the player is holding the Head Locator item
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() &&
                    itemInHand.getItemMeta().getDisplayName().equals("§6Head Locator")) {

                Location nearestHead = headWorldController.findNearestUnclaimedHead(player);
                if (nearestHead != null) {
                    double distance = player.getLocation().distance(nearestHead);
                    if (distance > 10) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aNearest unclaimed head is " + (int) distance + " meters away."));
                        player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1.0f, 1.0f); // Play a sound for far distance
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aYou are very close to an unclaimed head!"));
                        player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1.0f, 1.5f); // Play a sound for close distance
                    }
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cNo unclaimed heads found."));
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cHold the Head Locator to track heads."));
            }
        }, 0L, 1200L); // Runs every 60 seconds (1200 ticks)
    }
}