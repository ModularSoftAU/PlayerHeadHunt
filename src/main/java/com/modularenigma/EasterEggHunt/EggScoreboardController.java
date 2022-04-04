package com.modularenigma.EasterEggHunt;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static com.modularenigma.EasterEggHunt.EggController.getEggs;

public class EggScoreboardController {
    private static EasterEggHuntMain plugin;
    public EggScoreboardController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public static void loadSidebarScoreboard(Player player) {
        String EGGTOTAL = plugin.getConfig().getString("EGG.EGGTOTAL");

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("EggScoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Easter Egg Hunt");

        Score toplinebreak = objective.getScore(" ");
        toplinebreak.setScore(1);

        Score advertisementline = objective.getScore(org.bukkit.ChatColor.YELLOW + "craftingforchrist.net");
        advertisementline.setScore(2);

        Score eggtotalline = objective.getScore(org.bukkit.ChatColor.YELLOW.toString() + org.bukkit.ChatColor.BOLD + "Eggs: " + org.bukkit.ChatColor.WHITE + getEggs(player) + "/" + EGGTOTAL);
        eggtotalline.setScore(3);

        Score bottomlinebreak = objective.getScore("  ");
        bottomlinebreak.setScore(4);

        player.setScoreboard(board);
    }

}
