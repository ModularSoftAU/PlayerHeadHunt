package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadQuery {
    private final YamlFileManager yamlFileManager;

    public HeadQuery(YamlFileManager yamlFileManager) {
        this.yamlFileManager = yamlFileManager;
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
        data.remove(playerUUID);
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

    public List<HeadHunter> getBestHunters(int topHunters) {
        Map<String, Object> data = yamlFileManager.getData();
        List<HeadHunter> bestHunters = new ArrayList<>();

        data.forEach((key, value) -> {
            if (value instanceof Map) {
                Map<String, Object> playerData = (Map<String, Object>) value;

                String username = (String) playerData.get("username");
                Object headsCollectedObj = playerData.get("headsCollected");

                // Validate that headsCollected is a list
                if (username != null && headsCollectedObj instanceof List<?>) {
                    int headsCollectedCount = ((List<?>) headsCollectedObj).size();
                    bestHunters.add(new HeadHunter(username, headsCollectedCount));
                }
            }
        });

        // Sort hunters by the number of heads collected in descending order
        bestHunters.sort((a, b) -> Integer.compare(b.headsCollected(), a.headsCollected()));

        // Return the top hunters
        return bestHunters.subList(0, Math.min(topHunters, bestHunters.size()));
    }
}