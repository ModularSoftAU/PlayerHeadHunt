package com.modularenigma.EasterEggHunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class EggScoreboardController {
    private final EasterEggHuntMain plugin;

    public EggScoreboardController(EasterEggHuntMain plugin) {
        this.plugin = plugin;
    }

    public void reloadScoreboard(Player player, int eggsFound) {
        int totalEggs = plugin.config().getTotalEggs();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("EggScoreboard", "dummy", Component.text("Easter Egg Hunt"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score toplinebreak = objective.getScore(" ");
        Score advertisementline = objective.getScore(ChatColor.YELLOW + "craftingforchrist.net");
        Score eggtotalline = objective.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Eggs: " + ChatColor.WHITE + eggsFound + "/" + totalEggs);
        Score bottomlinebreak = objective.getScore("  ");

        toplinebreak.setScore(1);
        advertisementline.setScore(2);
        eggtotalline.setScore(3);
        bottomlinebreak.setScore(4);

        player.setScoreboard(board);
    }
}
