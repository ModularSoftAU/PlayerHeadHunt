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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        // Calculate initial delay
        LocalTime targetTime = LocalTime.of(hour, minute);
        long initialDelay = calculateInitialDelay(targetTime);

        // Schedule the task
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendLeaderboardWebhook(webhookUrl, headQuery);
            } catch (Exception e) {
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
                    // Build the fields dynamically
                    StringBuilder fieldsJson = new StringBuilder();
                    for (HeadQuery.HeadHunter hunter : topHunters) {
                        fieldsJson.append(String.format("""
                        {
                            "name": "%s",
                            "value": "%d heads collected",
                            "inline": false
                        },
                    """, hunter.name(), hunter.headsCollected()));
                    }

                    // Remove trailing comma
                    if (fieldsJson.length() > 0 && fieldsJson.charAt(fieldsJson.length() - 2) == ',') {
                        fieldsJson.setLength(fieldsJson.length() - 2);
                    }

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

                    plugin.getLogger().info("Generated JSON: " + embedJson);

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