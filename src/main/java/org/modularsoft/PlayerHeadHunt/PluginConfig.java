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
    @Getter private final boolean flightDisabledFeatureEnabled;

    @Getter private final String headBlock;
    @Getter private final int headRespawnTimer;
    @Getter private final List<String> headSkins;

    @Getter private final Sound headFoundSound;
    @Getter private final Sound headAlreadyFoundSound;
    @Getter private final Sound minorCollectionSound;
    @Getter private final Sound majorCollectionSound;

    @Getter private final Map<Integer, HeadMileStone> headMilestones;

    // Raw percentage templates parsed once from config; counts derived at runtime
    private record MilestoneTemplate(double percentage, boolean isMajor, Material helmet) {}
    private final List<MilestoneTemplate> milestoneTemplates;

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
    @Getter private final String langAllHeadsCollected;
    @Getter private final String langFlightDisabled;

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
        flightDisabledFeatureEnabled = config.getBoolean("FEATURE.DISABLEFLIGHT");

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

        milestoneTemplates = new ArrayList<>();
        for (Object entry : config.getList("MILESTONES.MINOR", Collections.emptyList()))
            parseMilestoneTemplate(entry, false);
        for (Object entry : config.getList("MILESTONES.MAJOR", Collections.emptyList()))
            parseMilestoneTemplate(entry, true);

        headMilestones = new HashMap<>();
        recomputeMilestones(getTotalHeads());

        langDatabaseConnectionError =        lang("LANG.DATABASE.CONNECTIONERROR");
        langDatabaseConnectionSuccess =      lang("LANG.DATABASE.CONNECTIONSUCCESS");
        langNotAPlayer =                     lang("LANG.COMMAND.NOTAPLAYER");
        langInsufficientPermissions =        lang("LANG.COMMAND.INSUFFICENTPERMISSIONS");
        langCommandIncomplete =              lang("LANG.COMMAND.COMMANDINCOMPLETE");
        langHeadFound =                      lang("LANG.HEAD.HEADFOUND");
        langHeadAlreadyFound =               lang("LANG.HEAD.HEADALREADYFOUND");
        langHeadFirstFinder =                lang("LANG.HEAD.FIRSTFINDER");
        langHeadFirstFinderStill =           lang("LANG.HEAD.FIRSTFINDERSTILL");
        langHeadNotFirstFinderSingle =       lang("LANG.HEAD.NOTFIRSTFINDERSINGLE");
        langHeadNotFirstFinderMultiple =     lang("LANG.HEAD.NOTFIRSTFINDERMULTIPLE");
        langFirstHeadFound =                 lang("LANG.HEAD.FIRSTHEADFOUND");
        langLastHeadFound =                  lang("LANG.HEAD.LASTHEADFOUND");
        langHeadCount =                      lang("LANG.HEAD.HEADCOUNT");
        langHeadCollectionMilestoneReached = lang("LANG.HEAD.HEADCOLLECTIONMILESTONEREACHED");
        langAllHeadsCollected =              lang("LANG.HEAD.ALLHEADSCOLLECTED");
        langFlightDisabled =                 lang("LANG.FLIGHT.DISABLED");

        langLeaderboardNoHeads =             lang("LANG.LEADERBOARD.NOHEADS");
        langLeaderboardHeader =              lang("LANG.LEADERBOARD.HEADER");
        langLeaderboardFirstColour =         lang("LANG.LEADERBOARD.FIRSTCOLOUR");
        langLeaderboardSecondColour =        lang("LANG.LEADERBOARD.SECONDCOLOUR");
        langLeaderboardThirdColour =         lang("LANG.LEADERBOARD.THIRDCOLOUR");
        langLeaderboardFormat =              lang("LANG.LEADERBOARD.FORMAT");
    }

    /** Reads a lang string, falling back to the bundled default if the key is absent or the file is corrupt. */
    private String lang(String path) {
        String value = config.getString(path); // falls through to bundled defaults automatically
        if (value == null) {
            plugin.getLogger().warning("Missing config key: " + path + " — using empty string. Delete config.yml to regenerate.");
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    private void parseMilestoneTemplate(Object entry, boolean isMajor) {
        double percentage;
        Material helmet = null;

        if (entry instanceof Number num) {
            percentage = num.doubleValue();
        } else if (entry instanceof Map<?, ?> map) {
            Object pctObj = map.get("percentage");
            if (!(pctObj instanceof Number pctNum)) return;
            percentage = pctNum.doubleValue();
            Object helmetStr = map.get("helmet");
            if (helmetStr instanceof String name) {
                try {
                    helmet = Material.valueOf(name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Unknown helmet material in milestones config: " + name);
                }
            }
        } else {
            return;
        }

        milestoneTemplates.add(new MilestoneTemplate(percentage, isMajor, helmet));
    }

    public void recomputeMilestones(int totalHeads) {
        headMilestones.clear();
        if (totalHeads <= 0) return;

        for (MilestoneTemplate template : milestoneTemplates) {
            int count = Math.max(1, (int) Math.round(template.percentage() / 100.0 * totalHeads));
            HeadMileStone milestone = new HeadMileStone(count, template.isMajor());
            if (template.helmet() != null) milestone.setHelmet(template.helmet());
            headMilestones.put(count, milestone);
        }

        plugin.getLogger().info("Milestones recalculated for " + totalHeads + " total heads: " + headMilestones.keySet().stream().sorted().toList());
    }

    public void save() {
        plugin.saveConfig();
    }

    public void setTotalHeads(int totalHeads) {
        config.set("HEAD.HEADTOTAL", totalHeads);
        recomputeMilestones(totalHeads);
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
