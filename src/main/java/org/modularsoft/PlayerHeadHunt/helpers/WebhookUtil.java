package org.modularsoft.PlayerHeadHunt.helpers;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.modularsoft.PlayerHeadHunt.HeadQuery;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class WebhookUtil {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public WebhookUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void scheduleDailyWebhook(HeadQuery headQuery) {
        if (!config.getBoolean("FEATURE.LEADERBOARDDAILYWEBHOOK")) {
            plugin.getLogger().info("Daily leaderboard webhook is disabled in the config.");
            return;
        }

        String webhookUrl = config.getString("DISCORD.WEBHOOKURL");
        int hour = config.getInt("DISCORD.HOUR");
        int minute = config.getInt("DISCORD.MINUTE");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LocalTime targetTime = LocalTime.of(hour, minute);
        long initialDelay = calculateInitialDelay(targetTime);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendLeaderboardWebhook(webhookUrl, headQuery);
            } catch (Exception e) {
                plugin.getLogger().severe("Exception in scheduled webhook task: " + e.getMessage());
                e.printStackTrace();
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    private long calculateInitialDelay(LocalTime targetTime) {
        LocalTime now = LocalTime.now();
        long delay = now.until(targetTime, TimeUnit.SECONDS.toChronoUnit());
        return delay > 0 ? delay : TimeUnit.DAYS.toSeconds(1) + delay;
    }

    public void sendLeaderboardWebhook(String webhookUrl, HeadQuery headQuery) {
        headQuery.getBestHunters(5).thenAccept((List<HeadQuery.HeadHunter> topHunters) -> {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (topHunters == null || topHunters.isEmpty()) {
                        plugin.getLogger().warning("No top hunters found. Skipping webhook.");
                        return;
                    }

                    List<String> fieldsList = new ArrayList<>();
                    for (HeadQuery.HeadHunter hunter : topHunters) {
                        if (hunter == null) continue;

                        String name = hunter.name();
                        int headsCollected = hunter.headsCollected();

                        if (name != null && !name.isEmpty()) {
                            fieldsList.add(String.format("""
                                {
                                    "name": "%s",
                                    "value": "%d heads collected",
                                    "inline": false
                                }""", name, headsCollected));
                        }
                    }

                    if (fieldsList.isEmpty()) {
                        plugin.getLogger().warning("Top hunters list was populated, but all entries were invalid or excluded.");
                        return;
                    }

                    String fieldsJson = String.join(",", fieldsList);

                    String embedJson = String.format("""
                        {
                            "embeds": [
                                {
                                    "title": "Leaderboard Standings",
                                    "description": "Here are the current leaderboard standings:",
                                    "color": 3447003,
                                    "fields": [
                                        %s
                                    ],
                                    "thumbnail": {
                                        "url": "https://github.com/ModularSoftAU/assets/blob/master/playerheadhunt/playerheadhunt-icon-text-256.png?raw=true"
                                    },
                                    "footer": {
                                        "text": "Developed by Modular Software"
                                    }
                                }
                            ]
                        }
                    """, fieldsJson);

                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpPost post = new HttpPost(webhookUrl);
                        post.setEntity(new StringEntity(embedJson, ContentType.APPLICATION_JSON));
                        try (CloseableHttpResponse response = httpClient.execute(post)) {
                            plugin.getLogger().info("Webhook response: " + response.getCode());
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to send webhook: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
    }
}
