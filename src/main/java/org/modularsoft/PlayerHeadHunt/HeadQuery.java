package org.modularsoft.PlayerHeadHunt;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.modularsoft.PlayerHeadHunt.helpers.YamlFileManager;

import java.util.ArrayList;
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
        return playerData != null ? (int) playerData.getOrDefault("headsCollected", 0) : 0;
    }

    public int foundHeadsAlreadyCount(int xCord, int yCord, int zCord) {
        Map<String, Object> data = yamlFileManager.getData();
        List<Map<String, Object>> heads = (List<Map<String, Object>>) data.get("heads");
        return (int) heads.stream()
                .filter(head -> head.get("x").equals(xCord) && head.get("y").equals(yCord) && head.get("z").equals(zCord))
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

        // Safely retrieve the "heads" list
        List<Map<String, Object>> heads = (List<Map<String, Object>>) data.get("heads");
        if (heads == null) {
            // Initialize the "heads" list if it is null
            heads = new ArrayList<>();
            data.put("heads", heads);
            yamlFileManager.save();
        }

        // Check if the player has already collected the head
        return heads.stream()
                .anyMatch(head -> head.get("playerUUID").equals(playerUUID) &&
                        head.get("x").equals(x) &&
                        head.get("y").equals(y) &&
                        head.get("z").equals(z));
    }

    public void insertCollectedHead(Player player, int x, int y, int z) {
        String playerUUID = player.getUniqueId().toString();
        Map<String, Object> data = yamlFileManager.getData();
        List<Map<String, Object>> heads = (List<Map<String, Object>>) data.get("heads");
        heads.add(Map.of("playerUUID", playerUUID, "x", x, "y", y, "z", z));
        Map<String, Object> playerData = (Map<String, Object>) data.get(playerUUID);
        if (playerData == null) {
            playerData = Map.of("headsCollected", 1);
            data.put(playerUUID, playerData);
        } else {
            playerData.put("headsCollected", (int) playerData.get("headsCollected") + 1);
        }
        yamlFileManager.save();
    }

    public boolean addNewHunter(Player player) {
        String playerUUID = player.getUniqueId().toString();
        String username = player.getName();
        Map<String, Object> data = yamlFileManager.getData();
        if (data.containsKey(playerUUID)) {
            return false;
        }
        data.put(playerUUID, Map.of("username", username, "headsCollected", 0));
        yamlFileManager.save();
        return true;
    }

    public List<HeadHunter> getBestHunters(int topHunters) {
        Map<String, Object> data = yamlFileManager.getData();
        List<HeadHunter> bestHunters = new ArrayList<>();
        data.forEach((key, value) -> {
            if (value instanceof Map) {
                Map<String, Object> playerData = (Map<String, Object>) value;
                bestHunters.add(new HeadHunter((String) playerData.get("username"), (int) playerData.get("headsCollected")));
            }
        });
        bestHunters.sort((a, b) -> Integer.compare(b.headsCollected(), a.headsCollected()));
        return bestHunters.subList(0, Math.min(topHunters, bestHunters.size()));
    }
}