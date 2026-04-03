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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.modularsoft.PlayerHeadHunt.compass.HeadCompassController;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.io.File;
import java.util.*;

public class HeadWorldController {
    private final PlayerHeadHuntMain plugin;
    private final YamlFileManager yamlFileManager;
    private HeadCompassController compassController;

    public HeadWorldController(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
        this.yamlFileManager = new YamlFileManager(new File(plugin.getDataFolder(), "player-data.yml"));
    }

    public void setCompassController(HeadCompassController compassController) {
        this.compassController = compassController;
    }

    // Scan all loaded chunks for player heads within the configured region.
    // Uses tile-entity iteration so it only touches actual skull blocks, not every block.
    public Set<Location> scanForHeadLocations() {
        Set<Location> locations = new HashSet<>();
        org.bukkit.World bukkitWorld = Bukkit.getWorld("world");
        if (bukkitWorld == null) return locations;

        String headBlock = plugin.config().getHeadBlock().toUpperCase();
        Material headMaterial;
        try {
            headMaterial = Material.valueOf(headBlock);
        } catch (IllegalArgumentException e) {
            return locations;
        }

        BlockVector3 lower = plugin.config().getLowerRegion();
        BlockVector3 upper = plugin.config().getUpperRegion();
        int minX = Math.min(lower.getX(), upper.getX());
        int maxX = Math.max(lower.getX(), upper.getX());
        int minY = Math.min(lower.getY(), upper.getY());
        int maxY = Math.max(lower.getY(), upper.getY());
        int minZ = Math.min(lower.getZ(), upper.getZ());
        int maxZ = Math.max(lower.getZ(), upper.getZ());

        for (Chunk chunk : bukkitWorld.getLoadedChunks()) {
            int chunkMinX = chunk.getX() * 16;
            int chunkMaxX = chunkMinX + 15;
            int chunkMinZ = chunk.getZ() * 16;
            int chunkMaxZ = chunkMinZ + 15;
            if (chunkMaxX < minX || chunkMinX > maxX || chunkMaxZ < minZ || chunkMinZ > maxZ) continue;

            for (BlockState tileEntity : chunk.getTileEntities()) {
                if (tileEntity.getType() != headMaterial) continue;
                Location loc = tileEntity.getLocation();
                if (loc.getBlockX() >= minX && loc.getBlockX() <= maxX
                        && loc.getBlockY() >= minY && loc.getBlockY() <= maxY
                        && loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ) {
                    locations.add(loc);
                }
            }
        }
        return locations;
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
                Integer.valueOf(x).equals(head.get("x"))
                && Integer.valueOf(y).equals(head.get("y"))
                && Integer.valueOf(z).equals(head.get("z")));

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
        if (compassController != null) {
            compassController.onHeadRemoved(headBlock);
        }
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
        if (compassController != null) {
            compassController.onHeadRespawned(headBlockLocation);
        }
    }

    private String getRandomHead() {
        Random random = new Random();
        int skins = plugin.config().getHeadSkins().size();
        return plugin.config().getHeadSkins().get(random.nextInt(0, skins));
    }
}