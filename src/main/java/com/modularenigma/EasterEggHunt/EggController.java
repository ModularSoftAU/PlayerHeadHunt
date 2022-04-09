package com.modularenigma.EasterEggHunt;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.block.Counter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class EggController {
    private static EggController instance;

    public static EggController instance() {
        if (instance == null)
            instance = new EggController();
        return instance;
    }

    private EggController() {}

    public void calculateTotalEggs() {
        String eggBlock = Objects.requireNonNull(EasterEggHuntMain.plugin().getConfig().getString("EGG.EGGBLOCK")).toLowerCase();

        BlockVector3 upperRegion = EasterEggHuntMain.plugin().config().getUpperRegion();
        BlockVector3 lowerRegion = EasterEggHuntMain.plugin().config().getLowerRegion();

        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getServer().getWorld("world")));

        Region selection = new CuboidRegion(world, upperRegion, lowerRegion);
        Mask mask = new BlockTypeMask(world, BlockTypes.get(eggBlock));
        Counter count = new Counter();

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            int countedBlocksMine = editSession.countBlocks(selection, mask);

            RegionMaskingFilter filter = new RegionMaskingFilter(mask, count);
            RegionVisitor visitor = new RegionVisitor(selection, filter);
            Operations.completeBlindly(visitor);
            int countedblocks = count.getCount();

            EasterEggHuntMain.plugin().getServer().getConsoleSender().sendMessage("Mine counted: " + countedBlocksMine);
            EasterEggHuntMain.plugin().getServer().getConsoleSender().sendMessage("Default counted: " + countedblocks);

            // Put total amount into config file.
            EasterEggHuntMain.plugin().config().setTotalEggs(countedblocks);
            EasterEggHuntMain.plugin().config().save();
        }
    }

    public int getEggs(Player player) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement findstatement = EasterEggHuntMain.plugin().getConnection().prepareStatement("select eggsCollected as 'eastereggs' from playerdata where uuid=?");
            findstatement.setString(1, UserUUID);

            ResultSet results = findstatement.executeQuery();
            if (results.next()) return results.getInt("eastereggs");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(EasterEggHuntMain.plugin().config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    public void clearEggs(Player player) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check how many eggs the player has collected.
        //
        try {
            PreparedStatement deletestatement = EasterEggHuntMain.plugin().getConnection().prepareStatement("DELETE from eastereggs where playerid=(select id from playerdata where uuid=?)");
			PreparedStatement resetEggCountStatement = EasterEggHuntMain.plugin().getConnection().prepareStatement("UPDATE playerdata SET eggsCollected = 0 WHERE uuid = ?");
            deletestatement.setString(1, UserUUID);
			resetEggCountStatement.setString(1, UserUUID);
            deletestatement.executeUpdate();
			resetEggCountStatement.executeUpdate();
            player.sendMessage("All eggs have been cleared.");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(EasterEggHuntMain.plugin().config().getLangDatabaseConnectionError());
        }
    }

    public boolean hasAlreadyCollectedEgg(Player player, int x, int y, int z) {
        String UserUUID = player.getUniqueId().toString();

        //
        // Database Query
        // Check if the player has already found that Easter Egg before.
        //
        try {
            PreparedStatement findstatement = EasterEggHuntMain.plugin().getConnection().prepareStatement("SELECT e.* FROM eastereggs e JOIN playerdata p ON e.playerid = p.id WHERE p.uuid = ? AND eggcordx=? AND eggcordy=? AND eggcordz=?");
            findstatement.setString(1, UserUUID);
            findstatement.setString(2, "" + x);
            findstatement.setString(3, "" + y);
            findstatement.setString(4, "" + z);

            ResultSet results = findstatement.executeQuery();

            // Return's true if we already found the egg.
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(EasterEggHuntMain.plugin().config().getLangDatabaseConnectionError());
        }
        return false;
    }

    public void breakBlock(int x, int y, int z) {
        Location eggBlock = new Location(Bukkit.getWorld("world"), x, y, z);
        eggBlock.getBlock().setType(Material.AIR);
    }

    public void replaceEggBlock(Material EggMaterialBlock, BlockData blockData, int x, int y, int z) {
        Location eggBlockLocation = new Location(Bukkit.getWorld("world"), x, y, z);
        eggBlockLocation.getBlock().setType(EggMaterialBlock);
        eggBlockLocation.getBlock().setBlockData(blockData);

        BlockState eggBlockState = eggBlockLocation.getBlock().getState();
        if (eggBlockState instanceof Skull skull) {
            PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", getRandomHead()));

            skull.setPlayerProfile(profile);
            skull.update(true);
        }
    }

    private String getRandomHead() {
        Random random = new Random();
        int skins = EasterEggHuntMain.plugin().config().getEggSkins().size();
        return EasterEggHuntMain.plugin().config().getEggSkins().get(random.nextInt(0, skins));
    }

    public void insertCollectedEgg(Player player, Block block, int x, int y, int z) {
        int eggRespawnTimer = EasterEggHuntMain.plugin().config().getEggRespawnTimer();
        String UserUUID = player.getUniqueId().toString();
        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();

        //
        // Database Query
        // Insert Easter Egg
        //
        try {
            PreparedStatement insertstatement = EasterEggHuntMain.plugin().getConnection().prepareStatement(
                "INSERT INTO eastereggs (playerid, eggcordx, eggcordy, eggcordz) " +
                    "VALUES ((select id from playerdata where uuid=?), ?, ?, ?)");

            insertstatement.setString(1, UserUUID);
            insertstatement.setString(2, String.valueOf(x));
            insertstatement.setString(3, String.valueOf(y));
            insertstatement.setString(4, String.valueOf(z));
            insertstatement.executeUpdate();

            EggChatController.instance().eggFoundResponse(player);

            // Break the egg block
            breakBlock(x, y, z);

            new BukkitRunnable() {
                @Override
                public void run() {
                    replaceEggBlock(blockType, blockData, x, y, z);
                }
            }.runTaskLater(EasterEggHuntMain.plugin(), eggRespawnTimer);

        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(EasterEggHuntMain.plugin().config().getLangDatabaseConnectionError());
        }
    }
}
