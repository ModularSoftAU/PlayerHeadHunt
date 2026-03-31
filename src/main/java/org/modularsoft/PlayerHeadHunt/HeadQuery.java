package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeadQuery {
    private final YamlFileManager yamlFileManager;
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
        int count = 0;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!(entry.getValue() instanceof Map<?, ?> playerData)) continue;

            Object headsObj = playerData.get("headsCollected");
            if (!(headsObj instanceof List<?> heads)) continue;

            for (Object head : heads) {
                if (!(head instanceof Map<?, ?> headMap)) continue;
                Object hx = headMap.get("x");
                Object hy = headMap.get("y");
                Object hz = headMap.get("z");
                if (hx instanceof Integer && hy instanceof Integer && hz instanceof Integer
                        && (Integer) hx == xCord && (Integer) hy == yCord && (Integer) hz == zCord) {
                    count++;
                    break; // each player counts once per head location
                }
            }
        }

        return count;
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
                Objects.equals(head.get("x"), x) && Objects.equals(head.get("y"), y) && Objects.equals(head.get("z"), z));
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

    public int foundHeadsCountByName(String playerName) {
        Map<String, Object> data = yamlFileManager.getData();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!(entry.getValue() instanceof Map<?, ?> playerData)) continue;
            String username = (String) playerData.get("username");
            if (!playerName.equalsIgnoreCase(username)) continue;
            Object headsObj = playerData.get("headsCollected");
            if (headsObj instanceof List<?> heads) {
                return heads.size();
            }
            return 0;
        }
        return -1; // -1 indicates player not found in data
    }

    public boolean clearHeadsByName(String playerName) {
        Map<String, Object> data = yamlFileManager.getData();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!(entry.getValue() instanceof Map<?, ?> rawPlayerData)) continue;
            String username = (String) rawPlayerData.get("username");
            if (!playerName.equalsIgnoreCase(username)) continue;

            Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
            List<Map<String, Integer>> headsCollected = (List<Map<String, Integer>>) playerData.get("headsCollected");
            if (headsCollected != null) headsCollected.clear();
            playerData.put("headsCollectedCount", 0);
            yamlFileManager.save();
            return true;
        }
        return false;
    }

    public int getLoadedPlayerCount() {
        return yamlFileManager.getData().size();
    }

    public int getTotalHeadsCollectedAcrossAllPlayers() {
        return yamlFileManager.getData().values().stream()
                .filter(v -> v instanceof Map<?, ?>)
                .mapToInt(v -> {
                    Object headsObj = ((Map<?, ?>) v).get("headsCollected");
                    return (headsObj instanceof List<?> list) ? list.size() : 0;
                })
                .sum();
    }

    private boolean isPlayerExcluded(UUID uuid, String username) {
        return luckPerms.getUserManager().loadUser(uuid)
                .thenApply(user -> {
                    if (user == null) {
                        Bukkit.getLogger().warning("LuckPerms failed to load user for UUID: " + uuid);
                        return false; // Include the player if user data cannot be loaded
                    }

                    Tristate permissionResult = user.getCachedData()
                            .getPermissionData()
                            .checkPermission("playerheadhunt.leaderboard.exclude");

                    // Only exclude if the permission is explicitly TRUE
                    boolean isExcluded = permissionResult == Tristate.TRUE;

                    Bukkit.getLogger().info("Player " + username + " exclusion status: " + isExcluded);
                    return isExcluded;
                }).join();
    }

    private Optional<HeadHunter> processPlayerData(String uuidStr, Map<String, Object> playerData) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid UUID string: " + uuidStr);
            return Optional.empty();
        }

        String username = (String) playerData.get("username");
        Object headsCollectedObj = playerData.get("headsCollected");

        if (username == null || !(headsCollectedObj instanceof List<?> headsCollected)) {
            Bukkit.getLogger().warning("Invalid data for user " + uuidStr + ": username=" + username + ", headsCollected=" + headsCollectedObj);
            return Optional.empty();
        }

        Bukkit.getLogger().info("Processing player: " + username + ", Heads Collected: " + headsCollected.size());

        if (isPlayerExcluded(uuid, username)) {
            return Optional.empty();
        }

        return Optional.of(new HeadHunter(username, headsCollected.size()));
    }

    public CompletableFuture<List<HeadHunter>> getBestHunters(int topHunters) {
        Map<String, Object> data = yamlFileManager.getData();
        List<CompletableFuture<Optional<HeadHunter>>> futures = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String uuidStr = entry.getKey();
            Map<String, Object> playerData = (Map<String, Object>) entry.getValue();

            if (playerData == null) {
                Bukkit.getLogger().warning("Missing playerData for UUID: " + uuidStr);
                continue;
            }

            CompletableFuture<Optional<HeadHunter>> future = CompletableFuture.supplyAsync(() -> processPlayerData(uuidStr, playerData));
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .sorted((a, b) -> Integer.compare(b.headsCollected(), a.headsCollected()))
                        .limit(topHunters)
                        .collect(Collectors.toList()));
    }
}