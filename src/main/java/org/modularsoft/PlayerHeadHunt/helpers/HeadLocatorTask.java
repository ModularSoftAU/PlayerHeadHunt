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
            // Check if the player is holding the Head Locator item
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(HeadLocator.createHeadLocator())) {
                Location nearestHead = headWorldController.findNearestUnclaimedHead(player);
                if (nearestHead != null) {
                    double distance = player.getLocation().distance(nearestHead);
                    if (distance > 10) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aNearest unclaimed head is " + (int) distance + " meters away."));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aYou are very close to an unclaimed head!"));
                    }
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cNo unclaimed heads found."));
                }
            }
        }, 0L, 1200L); // Runs every 60 seconds (1200 ticks)
    }
}