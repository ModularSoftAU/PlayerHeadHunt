package org.modularsoft.PlayerHeadHunt;

import org.modularsoft.PlayerHeadHunt.helpers.HeadMileStone;
import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PluginConfig {
    private final PlayerHeadHuntMain plugin;
    private final FileConfiguration config;

    @Getter private final boolean milestoneHatFeatureEnabled;
    @Getter private final boolean milestoneMessageFeatureEnabled;

    @Getter private final String headBlock;
    @Getter private final int headRespawnTimer;
    @Getter private final List<String> headSkins;

    @Getter private final Sound headFoundSound;
    @Getter private final Sound headAlreadyFoundSound;
    @Getter private final Sound minorCollectionSound;
    @Getter private final Sound majorCollectionSound;

    @Getter private final Map<Integer, HeadMileStone> headMilestones;

    @Getter private final String langDatabaseConnectionError;
    @Getter private final String langDatabaseConnectionSuccess;
    @Getter private final String langNotAPlayer;
    @Getter private final String langInsufficientPermissions;
    @Getter private final String langCommandIncomplete;
    @Getter private final String langHeadFound;
    @Getter private final String langHeadAlreadyFound;
    @Getter private final String langHeadFirstFinder;
    @Getter private final String langHeadFirstFinderStill;
    @Getter private final String langHeadNotFirstFinderSingle;
    @Getter private final String langHeadNotFirstFinderMultiple;
    @Getter private final String langFirstHeadFound;
    @Getter private final String langLastHeadFound;
    @Getter private final String langHeadCount;
    @Getter private final String langHeadCollectionMilestoneReached;

    @Getter private final String langLeaderboardNoHeads;
    @Getter private final String langLeaderboardHeader;
    @Getter private final String langLeaderboardFirstColour;
    @Getter private final String langLeaderboardSecondColour;
    @Getter private final String langLeaderboardThirdColour;
    @Getter private final String langLeaderboardFormat;

    public PluginConfig(PlayerHeadHuntMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        milestoneHatFeatureEnabled = config.getBoolean("FEATURE.MILESTONEHAT");
        milestoneMessageFeatureEnabled = config.getBoolean("FEATURE.MILESTONEMESSAGE");

        headBlock = config.getString("HEAD.HEADBLOCK");
        headRespawnTimer = config.getInt("HEAD.RESPAWNTIMER");
        headSkins = new ArrayList<>();
        int maxSkins = config.getInt("HEAD.SKINSMAX");
        for (int i = 0; i < maxSkins; i++) {
            headSkins.add(config.getString("HEAD.SKINS." + i));
        }

        headFoundSound = Sound.valueOf(config.getString("SOUND.HEADFOUND"));
        headAlreadyFoundSound = Sound.valueOf(config.getString("SOUND.HEADALREADYFOUND"));
        minorCollectionSound = Sound.valueOf(config.getString("SOUND.MINORCOLLECTIONMILESTONE"));
        majorCollectionSound = Sound.valueOf(config.getString("SOUND.MAJORCOLLECTIONMILESTONE"));

        headMilestones = new HashMap<>();
        for (Integer minor : config.getIntegerList("MILESTONES.MINOR"))
            headMilestones.put(minor, new HeadMileStone(minor, false));
        for (Integer minor : config.getIntegerList("MILESTONES.MAJOR"))
            headMilestones.put(minor, new HeadMileStone(minor, true));
        headMilestones.get(config.getInt("MILESTONES.LEATHERHELMET")).setHelmet(Material.LEATHER_HELMET);
        headMilestones.get(config.getInt("MILESTONES.CHAINMAILHELMET")).setHelmet(Material.CHAINMAIL_HELMET);
        headMilestones.get(config.getInt("MILESTONES.IRONHELMET")).setHelmet(Material.IRON_HELMET);
        headMilestones.get(config.getInt("MILESTONES.GOLDENHELMET")).setHelmet(Material.GOLDEN_HELMET);
        headMilestones.get(config.getInt("MILESTONES.DIAMONDHELMET")).setHelmet(Material.DIAMOND_HELMET);
        headMilestones.get(config.getInt("MILESTONES.NETHERITEHELMET")).setHelmet(Material.NETHERITE_HELMET);

        langDatabaseConnectionError =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONERROR")));
        langDatabaseConnectionSuccess =      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.DATABASE.CONNECTIONSUCCESS")));
        langNotAPlayer =                     ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.NOTAPLAYER")));
        langInsufficientPermissions =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.INSUFFICENTPERMISSIONS")));
        langCommandIncomplete =              ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.COMMAND.COMMANDINCOMPLETE")));
        langHeadFound =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.HEADFOUND")));
        langHeadAlreadyFound =               ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.HEADALREADYFOUND")));
        langHeadFirstFinder =                ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.FIRSTFINDER")));
        langHeadFirstFinderStill =           ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.FIRSTFINDERSTILL")));
        langHeadNotFirstFinderSingle =       ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.NOTFIRSTFINDERSINGLE")));
        langHeadNotFirstFinderMultiple =     ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.NOTFIRSTFINDERMULTIPLE")));
        langFirstHeadFound =                 ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.FIRSTHEADFOUND")));
        langLastHeadFound =                  ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.LASTHEADFOUND")));
        langHeadCount =                      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.HEADCOUNT")));
        langHeadCollectionMilestoneReached = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.HEAD.HEADCOLLECTIONMILESTONEREACHED")));

        langLeaderboardNoHeads =             ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.NOHEADS")));
        langLeaderboardHeader =              ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.HEADER")));
        langLeaderboardFirstColour =         ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.FIRSTCOLOUR")));
        langLeaderboardSecondColour =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.SECONDCOLOUR")));
        langLeaderboardThirdColour =         ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.THIRDCOLOUR")));
        langLeaderboardFormat =              ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("LANG.LEADERBOARD.FORMAT")));
    }

    public void save() {
        plugin.saveConfig();
    }

    public void setTotalHeads(int totalHeads) {
        config.set("HEAD.HEADTOTAL", totalHeads);
    }

    public int getTotalHeads() {
        return config.getInt("HEAD.HEADTOTAL");
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
