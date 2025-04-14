package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HeadQuery {
    private final YamlFileManager yamlFileManager;
    RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    private final LuckPerms luckPerms;

    public HeadQuery(YamlFileManager yamlFileManager) {
        this.yamlFileManager = yamlFileManager;

        // Get the LuckPerms provider from the Bukkit services manager
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
        } else {
            throw new IllegalStateException("LuckPerms API is not available!");
        }
    }

    public record HeadHunter(@Getter String name, @Getter int headsCollected) { }

    public int foundHeadsCount(Player player) {
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);

        if (playerData == null) {
            return 0; // No data for the player, so no heads collected
        }

        List<Map<String, Integer>> headsCollected = (List<Map<String, Integer>>) playerData.get("headsCollected");
        if (headsCollected == null) {
            return 0; // No heads collected yet
        }

        return headsCollected.size(); // Return the count of collected heads
    }

    public int foundHeadsAlreadyCount(int xCord, int yCord, int zCord) {
        Map<String, Object> data = yamlFileManager.getData();
        Object headsObject = data.get("heads");

        // Ensure the "heads" object is a list
        if (!(headsObject instanceof List<?>)) {
            return 0; // Return 0 if the data is not a list
        }

        List<?> heads = (List<?>) headsObject;

        // Filter and count matching heads
        return (int) heads.stream()
                .filter(head -> head instanceof Map)
                .map(head -> (Map<String, Object>) head)
                .filter(head ->
                        head.get("x") instanceof Integer && head.get("y") instanceof Integer && head.get("z") instanceof Integer &&
                                head.get("x").equals(xCord) && head.get("y").equals(yCord) && head.get("z").equals(zCord)
                )
                .count();
    }

    public boolean clearHeads(Player player) {
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);

        if (playerData == null) {
            return false; // No data for the player
        }

        // Clear the contents of the headsCollected list
        List<Map<String, Integer>> headsCollected = (List<Map<String, Integer>>) playerData.get("headsCollected");
        if (headsCollected != null) {
            headsCollected.clear();
        }

        // Reset the headsCollectedCount to 0
        playerData.put("headsCollectedCount", 0);

        yamlFileManager.save();
        return true;
    }

    public boolean hasAlreadyCollectedHead(Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);

        if (playerData == null) {
            return false; // Player has no data, so they haven't collected any heads
        }

        List<Map<String, Integer>> headsCollected = (List<Map<String, Integer>>) playerData.get("headsCollected");
        if (headsCollected == null) {
            return false; // No heads collected yet
        }

        // Check if the head coordinates already exist in the list
        return headsCollected.stream().anyMatch(head ->
                head.get("x") == x && head.get("y") == y && head.get("z") == z);
    }

    public void insertCollectedHead(Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);

        if (playerData == null) {
            playerData = new HashMap<>();
            playerData.put("headsCollected", new ArrayList<Map<String, Integer>>());
            playerData.put("headsCollectedCount", 0);
            data.put(playerUUID, playerData);
        }

        List<Map<String, Integer>> headsCollected = (List<Map<String, Integer>>) playerData.get("headsCollected");
        if (headsCollected == null) {
            headsCollected = new ArrayList<>();
            playerData.put("headsCollected", headsCollected);
        }

        // Add the new head coordinates to the list
        Map<String, Integer> newHead = new HashMap<>();
        newHead.put("x", x);
        newHead.put("y", y);
        newHead.put("z", z);
        headsCollected.add(newHead);

        // Increment the count of collected heads
        int currentCount = (int) playerData.getOrDefault("headsCollectedCount", 0);
        playerData.put("headsCollectedCount", currentCount + 1);

        yamlFileManager.save();
    }

    public boolean addNewHunter(Player player) {
        String playerUUID = player.getUniqueId().toString();
        String username = player.getName();
        Map<String, Object> data = yamlFileManager.getData();

        if (data.containsKey(playerUUID)) {
            return false;
        }

        // Use a mutable map to store player data
        Map<String, Object> newPlayerData = new HashMap<>();
        newPlayerData.put("username", username);
        newPlayerData.put("headsCollected", new ArrayList<Map<String, Integer>>()); // Initialize an empty list for collected heads
        newPlayerData.put("headsCollectedCount", 0); // Optional: Track the count separately for efficiency
        data.put(playerUUID, newPlayerData);

        yamlFileManager.save();
        return true;
    }

    public CompletableFuture<List<HeadHunter>> getBestHunters(int topHunters) {
        Map<String, Object> data = yamlFileManager.getData();
        List<CompletableFuture<HeadHunter>> futures = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String uuidStr = entry.getKey();
            Map<String, Object> playerData = (Map<String, Object>) entry.getValue();

            if (playerData == null) continue;

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }

            String username = (String) playerData.get("username");
            Object headsCollectedObj = playerData.get("headsCollected");

            if (username == null || !(headsCollectedObj instanceof List<?>)) continue;

            List<?> headsCollected = (List<?>) headsCollectedObj;
            Player onlinePlayer = Bukkit.getPlayer(uuid);

            if (onlinePlayer != null) {
                if (!onlinePlayer.hasPermission("playerheadhunt.leaderboard.exclude")) {
                    futures.add(CompletableFuture.completedFuture(new HeadHunter(username, headsCollected.size())));
                }
            } else {
                CompletableFuture<HeadHunter> future = luckPerms.getUserManager().loadUser(uuid)
                        .thenApply(user -> {
                            boolean isExcluded = user.getCachedData()
                                    .getPermissionData()
                                    .checkPermission("playerheadhunt.leaderboard.exclude")
                                    .asBoolean();

                            return isExcluded ? null : new HeadHunter(username, headsCollected.size());
                        });

                futures.add(future);
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .sorted((a, b) -> Integer.compare(b.headsCollected(), a.headsCollected()))
                        .limit(topHunters)
                        .collect(Collectors.toList()));
    }
}