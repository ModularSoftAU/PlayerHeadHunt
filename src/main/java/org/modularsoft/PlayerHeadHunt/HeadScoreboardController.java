package org.modularsoft.PlayerHeadHunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class HeadScoreboardController {
    private final PlayerHeadHuntMain plugin;

    public HeadScoreboardController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
    }

    public void reloadScoreboard(Player player, int headsFound) {
        int totalHeads = plugin.config().getTotalHeads();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("HeadScoreboard", "dummy", Component.text("Player Head Hunt"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score toplinebreak = objective.getScore(" ");
        Score advertisementline = objective.getScore(ChatColor.YELLOW + "craftingforchrist.net");
        Score headtotalline = objective.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Heads: " + ChatColor.WHITE + headsFound + "/" + totalHeads);
        Score bottomlinebreak = objective.getScore("  ");

        toplinebreak.setScore(1);
        advertisementline.setScore(2);
        headtotalline.setScore(3);
        bottomlinebreak.setScore(4);

        player.setScoreboard(board);
    }
}
