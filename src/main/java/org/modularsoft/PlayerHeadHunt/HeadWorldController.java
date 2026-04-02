package org.modularsoft.PlayerHeadHunt;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class HeadWorldController {
    private final PlayerHeadHuntMain plugin;

    public HeadWorldController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
    }

    public void countHeadsInRegion() {
        String headBlock = plugin.config().getHeadBlock().toLowerCase();
        BlockVector3 upperRegion = plugin.config().getUpperRegion();
        BlockVector3 lowerRegion = plugin.config().getLowerRegion();

        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getServer().getWorld("world")));
        Region selection = new CuboidRegion(world, upperRegion, lowerRegion);
        Mask mask = new BlockTypeMask(world, BlockTypes.get(headBlock));

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            int countedBlocks = editSession.countBlocks(selection, mask);
            plugin.getServer().getConsoleSender().sendMessage("There are " + countedBlocks + " total heads in the region");

            // Update the HEAD.HEADTOTAL in the plugin config
            plugin.config().setTotalHeads(countedBlocks);
            plugin.config().save();
        }
    }

    public void playerCollectedHead(Player player, Block block, int x, int y, int z) {
        // Record the collection via HeadQuery (single source of truth for player data)
        plugin.getHeadQuery().insertCollectedHead(player, x, y, z);

        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();
        int headRespawnTimer = plugin.config().getHeadRespawnTimer();

        breakBlock(x, y, z);
        new BukkitRunnable() {
            @Override
            public void run() {
                replaceHeadBlock(blockType, blockData, x, y, z);
            }
        }.runTaskLater(plugin, headRespawnTimer);
    }

    private void breakBlock(int x, int y, int z) {
        Location headBlock = new Location(Bukkit.getWorld("world"), x, y, z);
        headBlock.getBlock().setType(Material.AIR);
    }

    private void replaceHeadBlock(Material headMaterialBlock, BlockData blockData, int x, int y, int z) {
        Location headBlockLocation = new Location(Bukkit.getWorld("world"), x, y, z);
        headBlockLocation.getBlock().setType(headMaterialBlock);
        headBlockLocation.getBlock().setBlockData(blockData);

        BlockState headBlockState = headBlockLocation.getBlock().getState();
        if (headBlockState instanceof Skull skull) {
            PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", getRandomHead()));

            skull.setPlayerProfile(profile);
            skull.update(true);
        }
    }

    private String getRandomHead() {
        Random random = new Random();
        int skins = plugin.config().getHeadSkins().size();
        return plugin.config().getHeadSkins().get(random.nextInt(0, skins));
    }
}