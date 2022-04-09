package com.modularenigma.EasterEggHunt;

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

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * This class controls any functionality that requires access to the world.
 */
public class EggWorldController {
    private final EasterEggHuntMain plugin;

    public EggWorldController(EasterEggHuntMain plugin) {
        this.plugin = plugin;
    }

    /**
     * Using the lower region and upper region areas in the config file, count the
     * number of eggs in the region (technically it counts the number of player heads)
     * and update the "totalEggs" field in the config to reflect the answer.
     *
     * Note: Eggs that have disappeared temporarily will not show up in this count.
     */
    public void countEggsInRegion() {
        String eggBlock = plugin.config().getEggBlock().toLowerCase();
        BlockVector3 upperRegion = plugin.config().getUpperRegion();
        BlockVector3 lowerRegion = plugin.config().getLowerRegion();

        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getServer().getWorld("world")));
        Region selection = new CuboidRegion(world, upperRegion, lowerRegion);
        Mask mask = new BlockTypeMask(world, BlockTypes.get(eggBlock));

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            int countedblocks = editSession.countBlocks(selection, mask);
            plugin.getServer().getConsoleSender().sendMessage("There are " + countedblocks + " total eggs in the region");

            // Put total amount into config file.
            plugin.config().setTotalEggs(countedblocks);
            plugin.config().save();
        }
    }

    public void playerCollectedEgg(Player player, Block block, int x, int y, int z) {
        EggQuery.insertCollectedEgg(plugin, player, x, y, z);
        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();

        // Break and set the block to be replaced later
        int eggRespawnTimer = plugin.config().getEggRespawnTimer();

        breakBlock(x, y, z);
        new BukkitRunnable() {
            @Override
            public void run() {
                replaceEggBlock(blockType, blockData, x, y, z);
            }
        }.runTaskLater(plugin, eggRespawnTimer);

    }

    private void breakBlock(int x, int y, int z) {
        Location eggBlock = new Location(Bukkit.getWorld("world"), x, y, z);
        eggBlock.getBlock().setType(Material.AIR);
    }

    private void replaceEggBlock(Material eggMaterialBlock, BlockData blockData, int x, int y, int z) {
        Location eggBlockLocation = new Location(Bukkit.getWorld("world"), x, y, z);
        eggBlockLocation.getBlock().setType(eggMaterialBlock);
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
        int skins = plugin.config().getEggSkins().size();
        return plugin.config().getEggSkins().get(random.nextInt(0, skins));
    }
}
