package com.modularenigma.EasterEggHunt;

import com.modularenigma.EasterEggHunt.helpers.EggMileStone;
import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PluginConfig {
    private final EasterEggHuntMain plugin;
    private final FileConfiguration config;

    @Getter private final String databaseHost;
    @Getter private final int databasePort;
    @Getter private final String databaseName;
    @Getter private final String databaseUsername;
    @Getter private final String databasePassword;

    @Getter private final boolean milestoneHatFeatureEnabled;
    @Getter private final boolean milestoneMessageFeatureEnabled;

    @Getter private final String eggBlock;
    @Getter private final int eggRespawnTimer;
    @Getter private final List<String> eggSkins;

    @Getter private final Sound eggFoundSound;
    @Getter private final Sound eggAlreadyFoundSound;
    @Getter private final Sound minorCollectionSound;
    @Getter private final Sound majorCollectionSound;

    @Getter private final Map<Integer, EggMileStone> eggMilestones;

    @Getter private final String langDatabaseConnectionError;
    @Getter private final String langDatabaseConnectionSuccess;
    @Getter private final String langNotAPlayer;
    @Getter private final String langInsufficientPermissions;
    @Getter private final String langCommandIncomplete;
    @Getter private final String langEggFound;
    @Getter private final String langFirstEggFound;
    @Getter private final String langLastEggFound;
    @Getter private final String langEggAlreadyFound;
    @Getter private final String langEggCount;
    @Getter private final String langEggCollectionMilestoneReached;

    @Getter private final String langLeaderboardNoEggs;
    @Getter private final String langLeaderboardHeader;
    @Getter private final String langLeaderboardFirstColour;
    @Getter private final String langLeaderboardSecondColour;
    @Getter private final String langLeaderboardThirdColour;
    @Getter private final String langLeaderboardFormat;

    public PluginConfig(EasterEggHuntMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        databaseHost = config.getString("DATABASE.HOST");
        databasePort = config.getInt("DATABASE.PORT");
        databaseName = config.getString("DATABASE.DATABASE");
        databaseUsername = config.getString("DATABASE.USERNAME");
        databasePassword = config.getString("DATABASE.PASSWORD");

        milestoneHatFeatureEnabled = config.getBoolean("FEATURE.MILESTONEHAT");
        milestoneMessageFeatureEnabled = config.getBoolean("FEATURE.MILESTONEMESSAGE");

        eggBlock = config.getString("EGG.EGGBLOCK");
        eggRespawnTimer = config.getInt("EGG.RESPAWNTIMER");
        eggSkins = new ArrayList<>();
        int maxSkins = config.getInt("EGG.SKINSMAX");
        for (int i = 0; i < maxSkins; i++) {
            eggSkins.add(config.getString("EGG.SKINS." + i));
        }

        eggFoundSound = Sound.valueOf(config.getString("SOUND.EGGFOUND"));
        eggAlreadyFoundSound = Sound.valueOf(config.getString("SOUND.EGGALREADYFOUND"));
        minorCollectionSound = Sound.valueOf(config.getString("SOUND.MINORCOLLECTIONMILESTONE"));
        majorCollectionSound = Sound.valueOf(config.getString("SOUND.MAJORCOLLECTIONMILESTONE"));

        eggMilestones = new HashMap<>();
        for (Integer minor : config.getIntegerList("MILESTONES.MINOR"))
            eggMilestones.put(minor, new EggMileStone(minor, false));
        for (Integer minor : config.getIntegerList("MILESTONES.MAJOR"))
            eggMilestones.put(minor, new EggMileStone(minor, true));
        eggMilestones.get(config.getInt("MILESTONES.LEATHERHELMET")).setHelmet(Material.LEATHER_HELMET);
        eggMilestones.get(config.getInt("MILESTONES.CHAINMAILHELMET")).setHelmet(Material.CHAINMAIL_HELMET);
        eggMilestones.get(config.getInt("MILESTONES.IRONHELMET")).setHelmet(Material.IRON_HELMET);
        eggMilestones.get(config.getInt("MILESTONES.GOLDENHELMET")).setHelmet(Material.GOLDEN_HELMET);
        eggMilestones.get(config.getInt("MILESTONES.DIAMONDHELMET")).setHelmet(Material.DIAMOND_HELMET);
        eggMilestones.get(config.getInt("MILESTONES.NETHERITEHELMET")).setHelmet(Material.NETHERITE_HELMET);

        langDatabaseConnectionError =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONERROR")));
        langDatabaseConnectionSuccess =     ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONSUCCESS")));
        langNotAPlayer =                    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.NOTAPLAYER")));
        langInsufficientPermissions =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.INSUFFICENTPERMISSIONS")));
        langCommandIncomplete =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.COMMANDINCOMPLETE")));
        langEggFound =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGFOUND")));
        langFirstEggFound =                 ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.FIRSTEGGFOUND")));
        langLastEggFound =                  ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.LASTEGGFOUND")));
        langEggAlreadyFound =               ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGALREADYFOUND")));
        langEggCount =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGCOUNT")));
        langEggCollectionMilestoneReached = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.EGG.EGGCOLLECTIONMILESTONEREACHED")));

        langLeaderboardNoEggs =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.NOEGGS")));
        langLeaderboardHeader =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.HEADER")));
        langLeaderboardFirstColour =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.FIRSTCOLOUR")));
        langLeaderboardSecondColour =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.SECONDCOLOUR")));
        langLeaderboardThirdColour =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.THIRDCOLOUR")));
        langLeaderboardFormat =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.FORMAT")));
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

    public BlockVector3 getLowerRegion() {
        return BlockVector3.at(
                config.getInt("REGION.LOWERREGION.X"),
                config.getInt("REGION.LOWERREGION.Y"),
                config.getInt("REGION.LOWERREGION.Z")
        );
    }

    public BlockVector3 getUpperRegion() {
        return BlockVector3.at(
                config.getInt("REGION.UPPERREGION.X"),
                config.getInt("REGION.UPPERREGION.Y"),
                config.getInt("REGION.UPPERREGION.Z")
        );
    }
}
