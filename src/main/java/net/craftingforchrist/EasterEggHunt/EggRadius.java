package net.craftingforchrist.EasterEggHunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class EggRadius {
    public static EasterEggHuntMain plugin;
    public void egg(EasterEggHuntMain instance) {
        plugin = instance;
    }

    static ArrayList<Block> blocks = new ArrayList<Block>();

    public static ArrayList<Block> getEggBlocks(Player player) {
        int EggRadius = plugin.getConfig().getInt("EGG.RADIUS");
        Iterator EggBlocks = plugin.getConfig().getStringList("EGG.EGGBLOCK").iterator();

//        String BlockMaterial = block.getType().name();
//        Material BlockMaterial = Material.

        while (EggBlocks.hasNext()) {
            String ConfigEggBlock = (String)EggBlocks.next();

            if (EggBlocks.equals(ConfigEggBlock)) {
                for (double x = EggBlocks.getLocation().getX() - EggRadius; x <= EggBlocks.getLocation().getX() + EggRadius; x++) {
                    for (double y = EggBlocks.getLocation().getY() - EggRadius; y <= EggBlocks.getLocation().getY() + EggRadius; y++) {
                        for (double z = EggBlocks.getLocation().getZ() - EggRadius; z <= EggBlocks.getLocation().getZ() + EggRadius; z++) {


                            player.sendMessage(String.valueOf(blocks));


                        }
                    }
                }
            }
        }

        return blocks;
    }

}
