package net.craftingforchrist.EasterEggHunt;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.block.Counter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EggController {
    private static EasterEggHuntMain plugin;
    public EggController(EasterEggHuntMain plugin){
        this.plugin = plugin;
    }

    public String setTotalEggBlocks() {
        String eggBlock = plugin.getConfig().getString("EGG.EGGBLOCK").toLowerCase();

        int UPPERREGIONX = plugin.getConfig().getInt("REGION.UPPERREGION.X");
        int UPPERREGIONY = plugin.getConfig().getInt("REGION.UPPERREGION.Y");
        int UPPERREGIONZ = plugin.getConfig().getInt("REGION.UPPERREGION.Z");

        int LOWERREGIONX = plugin.getConfig().getInt("REGION.LOWERREGION.X");
        int LOWERREGIONY = plugin.getConfig().getInt("REGION.LOWERREGION.Y");
        int LOWERREGIONZ = plugin.getConfig().getInt("REGION.LOWERREGION.Z");

        World world = BukkitAdapter.adapt(Bukkit.getServer().getWorld("world"));
        CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(UPPERREGIONX, UPPERREGIONY, UPPERREGIONZ), BlockVector3.at(LOWERREGIONX, LOWERREGIONY, LOWERREGIONZ));
        BlockTypeMask mask = new BlockTypeMask(world, BlockTypes.get(eggBlock));

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            Counter count = new Counter();
            RegionMaskingFilter filter = new RegionMaskingFilter(mask, count);
            RegionVisitor visitor = new RegionVisitor(selection, filter);
            Operations.completeBlindly(visitor);

            int countedblocks = count.getCount();

            // Put total amount into config file.
            plugin.getConfig().set("EGG.EGGTOTAL", countedblocks);
            plugin.saveConfig();

            return String.valueOf(countedblocks);
        }
    }

    public static int getEggs(Player player) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement findstatement = plugin.getConnection().prepareStatement("select count(*) as 'eastereggs' from eastereggs where playerid = (select id from playerdata where uuid=?)");
            findstatement.setString(1, UserUUID);

            ResultSet results = findstatement.executeQuery();
            if (results.next()) return results.getInt("eastereggs");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }
        return 0;
    }

    public static void clearEggs(Player player) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement deletestatement = plugin.getConnection().prepareStatement("DELETE from eastereggs where playerid=(select id from playerdata where uuid=?)");
            deletestatement.setString(1, UserUUID);

            deletestatement.executeUpdate();
            player.sendMessage("All eggs have been cleared.");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }
    }

    public static void breakEggBlock(int x, int y, int z) {
        Location EggBlock = new Location(Bukkit.getWorld("world"), x, y, z);
        EggBlock.getBlock().setType(Material.AIR);
    }

    public static void replaceEggBlock(Material EggBlock, BlockData blockData, int x, int y, int z) {
        Location EggBlockLocation = new Location(Bukkit.getWorld("world"), x, y, z);
        EggBlockLocation.getBlock().setType(EggBlock);
        EggBlockLocation.getBlock().setBlockData(blockData);
    }

    public static boolean alreadyCollectedEgg(Player player, int x, int y, int z) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check if the player has already found that Easter Egg before.
        //
        try {
            PreparedStatement findstatement = plugin.getConnection().prepareStatement("SELECT * FROM eastereggs WHERE playerid=(select id from playerdata where uuid=?) AND eggcordx=? AND eggcordy=? AND eggcordz=?");
            findstatement.setString(1, UserUUID);
            findstatement.setString(2, String.valueOf(x));
            findstatement.setString(3, String.valueOf(y));
            findstatement.setString(4, String.valueOf(z));

            ResultSet results = findstatement.executeQuery();
            if (!results.next()) return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }
        return false;
    }

    public static void insertCollectedEgg(Player player, Block block, int x, int y, int z) {
        int EGGRESPAWNTIMER = plugin.getConfig().getInt("EGG.RESPAWNTIMER");
        String UserUUID = player.getUniqueId().toString();
        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();

        //
        // Database Query
        // Insert Easter Egg
        //
        try {
            PreparedStatement insertstatement = plugin.getConnection().prepareStatement("INSERT INTO eastereggs (playerid, eggcordx, eggcordy, eggcordz) VALUES ((select id from playerdata where uuid=?), ?, ?, ?)");

            insertstatement.setString(1, UserUUID);
            insertstatement.setString(2, String.valueOf(x));
            insertstatement.setString(3, String.valueOf(y));
            insertstatement.setString(4, String.valueOf(z));

            insertstatement.executeUpdate();

            EggChatController.eggFoundResponse(player);
            breakEggBlock(x, y, z);

            new BukkitRunnable() {
                @Override
                public void run() {
                    replaceEggBlock(blockType, blockData, x, y, z);
                }
            }.runTaskLater(plugin, EGGRESPAWNTIMER);

        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
        }
    }
}
