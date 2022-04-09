package com.modularenigma.EasterEggHunt;

import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginConfig {
    private final EasterEggHuntMain plugin;
    private final FileConfiguration config;

    @Getter private final String databaseHost;
    @Getter private final String databasePort;
    @Getter private final String databaseName;
    @Getter private final String databaseUsername;
    @Getter private final String databasePassword;

    @Getter private final BlockVector3 upperRegion;
    @Getter private final BlockVector3 lowerRegion;

    @Getter private final boolean milestoneHatFeatureEnabled;
    @Getter private final boolean milestoneMessageFeatureEnabled;

    @Getter private final String eggBlock;
    @Getter private final int eggRespawnTimer;
    @Getter private final List<String> eggSkins;
    private int totalEggs;

    @Getter private final Sound eggFoundSound;
    @Getter private final Sound eggAlreadyFoundSound;
    @Getter private final Sound minorCollectionSound;
    @Getter private final Sound majorCollectionSound;

    @Getter private final String langDatabaseConnectionError;
    @Getter private final String langDatabaseConnectionSuccess;
    @Getter private final String langNotAPlayer;
    @Getter private final String langInsufficientPermissions;
    @Getter private final String langCommandIncomplete;
    @Getter private final String langEggFound;
    @Getter private final String langEggAlreadyFound;
    @Getter private final String langEggCount;
    @Getter private final String langEggCollectionMilestoneReached;

    public PluginConfig(EasterEggHuntMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        databaseHost = config.getString("DATABASE.HOST");
        databasePort = config.getString("DATABASE.PORT");
        databaseName = config.getString("DATABASE.DATABASE");
        databaseUsername = config.getString("DATABASE.USERNAME");
        databasePassword = config.getString("DATABASE.PASSWORD");

        upperRegion = BlockVector3.at(
                config.getInt("REGION.UPPERREGION.X"),
                config.getInt("REGION.UPPERREGION.Y"),
                config.getInt("REGION.UPPERREGION.Z")
        );
        lowerRegion = BlockVector3.at(
                config.getInt("REGION.LOWERREGION.X"),
                config.getInt("REGION.LOWERREGION.Y"),
                config.getInt("REGION.LOWERREGION.Z")
        );

        milestoneHatFeatureEnabled = config.getBoolean("FEATURE.MILESTONEHAT");
        milestoneMessageFeatureEnabled = config.getBoolean("FEATURE.MILESTONEMESSAGE");

        eggBlock = config.getString("EGG.EGGBLOCK");
        eggRespawnTimer = config.getInt("EGG.RESPAWNTIMER");
        eggSkins = new ArrayList<>();
        int maxSkins = config.getInt("EGG.SKINSMAX");
        for (int i = 0; i < maxSkins; i++) {
            eggSkins.add(config.getString("EGG.SKINS." + i));
        }
        totalEggs = config.getInt("EGG.EGGTOTAL");

        eggFoundSound = Sound.valueOf(config.getString("SOUND.EGGFOUND"));
        eggAlreadyFoundSound = Sound.valueOf(config.getString("SOUND.EGGALREADYFOUND"));
        minorCollectionSound = Sound.valueOf(config.getString("SOUND.MINORCOLLECTIONMILESTONE"));
        majorCollectionSound = Sound.valueOf(config.getString("SOUND.MAJORCOLLECTIONMILESTONE"));

        langDatabaseConnectionError =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONERROR")));
        langDatabaseConnectionSuccess =     ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONSUCCESS")));
        langNotAPlayer =                    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.NOTAPLAYER")));
        langInsufficientPermissions =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.INSUFFICENTPERMISSIONS")));
        langCommandIncomplete =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.COMMANDINCOMPLETE")));
        langEggFound =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGFOUND")));
        langEggAlreadyFound =               ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGALREADYFOUND")));
        langEggCount =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGCOUNT")));
        langEggCollectionMilestoneReached = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED")));
    }

    public void save() {
        plugin.saveConfig();
    }

    public void setTotalEggs(int totalEggs) {
        config.set("EGG.EGGTOTAL", totalEggs);
    }

    public int getTotalEggs() {
        return config.getInt("EGG.EGGTOTAL");
    }
}
