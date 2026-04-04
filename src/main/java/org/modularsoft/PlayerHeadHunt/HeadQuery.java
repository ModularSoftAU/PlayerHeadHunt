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
import java.util.Collections;

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
            if (!(entry.getValue() instanceof Map<?, ?> rawPlayerData)) continue;
            Map<String, Object> playerData = (Map<String, Object>) rawPlayerData;
            Object headsObj = playerData.get("headsCollected");
            if (!(headsObj instanceof List<?> rawList)) continue;
            for (Object rawHead : rawList) {
                if (!(rawHead instanceof Map<?, ?> rawHeadMap)) continue;
                Map<String, Object> head = (Map<String, Object>) rawHeadMap;
                if (Integer.valueOf(xCord).equals(head.get("x"))
                        && Integer.valueOf(yCord).equals(head.get("y"))
                        && Integer.valueOf(zCord).equals(head.get("z"))) {
                    count++;
                    break; // each player counts once per head
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
                Integer.valueOf(x).equals(head.get("x"))
                && Integer.valueOf(y).equals(head.get("y"))
                && Integer.valueOf(z).equals(head.get("z")));
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

    // Compass persistence

    public Map<String, Object> getRawCompassData(UUID uuid) {
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(uuid.toString());
        if (playerData == null) return Collections.emptyMap();

        Map<String, Object> compassData = new HashMap<>();
        compassData.put("compassMode",          playerData.get("compassMode"));
        compassData.put("compassTrackedX",       playerData.get("compassTrackedX"));
        compassData.put("compassTrackedY",       playerData.get("compassTrackedY"));
        compassData.put("compassTrackedZ",       playerData.get("compassTrackedZ"));
        compassData.put("compassCooldownUntil",  playerData.get("compassCooldownUntil"));
        return compassData;
    }

    public void saveCompassState(UUID uuid, String mode,
                                 Integer trackedX, Integer trackedY, Integer trackedZ,
                                 long cooldownUntil) {
        Map<String, Object> data = yamlFileManager.getData();
        Map<String, Object> playerData = (Map<String, Object>) data.get(uuid.toString());
        if (playerData == null) return;

        playerData.put("compassMode",         mode);
        playerData.put("compassTrackedX",     trackedX);
        playerData.put("compassTrackedY",     trackedY);
        playerData.put("compassTrackedZ",     trackedZ);
        playerData.put("compassCooldownUntil", cooldownUntil);
        yamlFileManager.save();
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
                        .sorted((a, b) -> {
                            int cmp = Integer.compare(b.headsCollected(), a.headsCollected());
                            if (cmp != 0) return cmp;
                            return a.name().compareToIgnoreCase(b.name());
                        })
                        .limit(topHunters)
                        .collect(Collectors.toList()));
    }
}