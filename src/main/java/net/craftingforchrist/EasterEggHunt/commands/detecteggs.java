package net.craftingforchrist.EasterEggHunt.commands;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class detecteggs implements CommandExecutor {
    public static EasterEggHuntMain plugin;
    public detecteggs(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        String UserUUID = player.getUniqueId().toString();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.COMMAND.NOTAPLAYER")));
            return true;
        }

//        getBlocks(Block.getBlockKey(84,64,-5));

        return true;
    }

    public List<Block> getBlocks(Block middle) {
        List<Block> blocks = new ArrayList<>();
        int EggDetectRadius = plugin.getConfig().getInt("EGG.RADIUS");

        for (int x = -EggDetectRadius; x <= EggDetectRadius; x++) {
            for (int y = -EggDetectRadius; y <= EggDetectRadius; y++) {
                for (int z = -EggDetectRadius; z <= EggDetectRadius; z++) {
                    blocks.add(middle.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

}
