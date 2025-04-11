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
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.io.File;
import java.util.*;

public class HeadWorldController {
    private final PlayerHeadHuntMain plugin;
    private final YamlFileManager yamlFileManager;

    public HeadWorldController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
        this.yamlFileManager = new YamlFileManager(new File(plugin.getDataFolder(), "player-data.yml"));
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
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);

        if (playerData == null) {
            playerData = new HashMap<>();
            playerData.put("headsCollected", new ArrayList<Map<String, Integer>>());
            data.put(playerUUID, playerData);
        }

        List<Map<String, Integer>> collectedHeads = (List<Map<String, Integer>>) playerData.get("headsCollected");
        if (collectedHeads == null) {
            collectedHeads = new ArrayList<>();
            playerData.put("headsCollected", collectedHeads);
        }

        boolean alreadyCollected = collectedHeads.stream().anyMatch(head ->
                head.get("x") == x && head.get("y") == y && head.get("z") == z);

        if (alreadyCollected) {
            player.sendMessage(plugin.config().getLangHeadAlreadyFound());
            return;
        }

        collectedHeads.add(Map.of("x", x, "y", y, "z", z));
        yamlFileManager.save();

        // Increment the player's head count
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