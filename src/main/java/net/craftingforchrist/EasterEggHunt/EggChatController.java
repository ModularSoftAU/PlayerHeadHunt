package net.craftingforchrist.EasterEggHunt;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static net.craftingforchrist.EasterEggHunt.EggController.getEggs;

public class EggChatController {
    private static EasterEggHuntMain plugin;
    public EggChatController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public static void eggAlreadyFoundResponse(Player player) {
        String EGGALREADYFOUNDSOUND = plugin.getConfig().getString("SOUND.EGGALREADYFOUND");

        player.playSound(player.getLocation(), Sound.valueOf(String.valueOf(EGGALREADYFOUNDSOUND)), 1, 1); // Play sound for an Easter Egg that is already found.
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGALREADYFOUND"))));
    }

    public static void eggFoundResponse(Player player) {
        String EGGFOUNDSOUND = plugin.getConfig().getString("SOUND.EGGFOUND");
        String EGGTOTAL = plugin.getConfig().getString("EGG.EGGTOTAL");

        player.playSound(player.getLocation(), Sound.valueOf(String.valueOf(EGGFOUNDSOUND)), 1, 1); // Play sound for an Easter Egg that is found.
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.EGG.EGGFOUND")
                .replace("%FOUNDEGGS%", String.valueOf(getEggs(player)))
                .replace("%NUMBEROFEGGS%", EGGTOTAL))));
    }

    public static void eggMilestoneReachedEvent(Player player, Sound EggSound, int eggs) {
        Boolean MILESTONEMESSAGE = plugin.getConfig().getBoolean("FEATURE.MILESTONEMESSAGE");

        if (MILESTONEMESSAGE) {
            String MILESTONEREACHEDMESSAGE = plugin.getConfig().getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED");
            player.playSound(player.getLocation(), EggSound, 1, 1);
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', MILESTONEREACHEDMESSAGE.replace("%PLAYER%", player.getName()).replace("%NUMBEROFEGGS%", String.valueOf(eggs))));
        }
    }

}
