package org.modularsoft.PlayerHeadHunt.compass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.modularsoft.PlayerHeadHunt.HeadQuery;
import org.modularsoft.PlayerHeadHunt.PlayerHeadHuntMain;

import java.util.*;

public class HeadCompassController {
    private final PlayerHeadHuntMain plugin;
    private final HeadQuery headQuery;
    private final NamespacedKey compassKey;

    // All currently valid head locations in the world
    private final Set<Location> knownHeadLocations = Collections.synchronizedSet(new HashSet<>());

    // Per-player in-memory compass states
    private final Map<UUID, CompassState> playerStates = new HashMap<>();

    public static class CompassState {
        public CompassMode mode = CompassMode.DEATH_COMPASS;
        public Location trackedHead = null;
        public long cooldownUntil = 0L;
    }

    public HeadCompassController(PlayerHeadHuntMain plugin, HeadQuery headQuery) {
        this.plugin = plugin;
        this.headQuery = headQuery;
        this.compassKey = new NamespacedKey(plugin, "head_compass");
    }

    // Called by HeadWorldController after world scan to populate the known head set
    public void setKnownHeadLocations(Set<Location> locations) {
        knownHeadLocations.clear();
        knownHeadLocations.addAll(locations);
        plugin.getServer().getConsoleSender().sendMessage(
            "[PlayerHeadHunt] Head Compass: indexed " + locations.size() + " head locations."
        );
    }

    // Called by HeadWorldController when a head block is broken
    public void onHeadRemoved(Location loc) {
        knownHeadLocations.remove(normalizeLocation(loc));

        // Any player tracking this head loses their target
        for (Map.Entry<UUID, CompassState> entry : playerStates.entrySet()) {
            CompassState state = entry.getValue();
            if (state.mode == CompassMode.HEAD_TRACKING
                    && state.trackedHead != null
                    && isSameLocation(state.trackedHead, loc)) {
                state.trackedHead = null;
                state.mode = CompassMode.DEATH_COMPASS;
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    setDeathCompassTarget(player);
                    updateCompassItemMeta(player);
                }
            }
        }
    }

    // Called by HeadWorldController when a head block respawns
    public void onHeadRespawned(Location loc) {
        knownHeadLocations.add(normalizeLocation(loc));
    }

    // Start the repeating 5-minute scan task
    public void startCompassTask() {
        int interval = plugin.config().getCompassScanIntervalTicks();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tickCompassForPlayer(player);
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    // Called on player join: restore persisted state and give the compass
    public void onPlayerJoin(Player player) {
        CompassState state = buildStateFromPersistence(player);

        // If cooldown elapsed while offline, clear it
        if (state.mode == CompassMode.RECHARGING
                && System.currentTimeMillis() >= state.cooldownUntil) {
            state.mode = CompassMode.DEATH_COMPASS;
            state.cooldownUntil = 0L;
        }

        playerStates.put(player.getUniqueId(), state);

        // Delay 1 tick so the player's inventory is fully initialised
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            giveCompassToPlayer(player);
            if (state.mode == CompassMode.HEAD_TRACKING && state.trackedHead != null) {
                player.setCompassTarget(state.trackedHead);
            }
        }, 1L);
    }

    // Called on player quit: persist state
    public void onPlayerQuit(Player player) {
        CompassState state = playerStates.get(player.getUniqueId());
        if (state != null) {
            persistState(player, state);
        }
        playerStates.remove(player.getUniqueId());
    }

    // Called from HeadFindEvent BEFORE the block is broken
    public void onHeadCollected(Player player, Location headLoc) {
        CompassState state = getState(player.getUniqueId());
        if (state.mode != CompassMode.HEAD_TRACKING || state.trackedHead == null) {
            return;
        }
        if (!isSameLocation(state.trackedHead, headLoc)) {
            return;
        }

        long cooldownMs = plugin.config().getCompassCooldownTicks() * 50L; // ticks → ms
        state.trackedHead = null;
        state.mode = CompassMode.RECHARGING;
        state.cooldownUntil = System.currentTimeMillis() + cooldownMs;
        setDeathCompassTarget(player);
        updateCompassItemMeta(player);
        persistState(player, state);
        player.sendMessage(plugin.config().getLangCompassTargetCollected());
    }

    // Give a compass to the player in the fixed slot if not already present
    public void giveCompassToPlayer(Player player) {
        int slot = plugin.config().getCompassSlot();
        ItemStack existing = player.getInventory().getItem(slot);
        if (existing == null || !isOurCompass(existing)) {
            player.getInventory().setItem(slot, buildCompassItem(getState(player.getUniqueId())));
        }
    }

    public boolean isOurCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(compassKey, PersistentDataType.BYTE);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void tickCompassForPlayer(Player player) {
        CompassState state = getState(player.getUniqueId());

        if (state.mode == CompassMode.RECHARGING) {
            if (System.currentTimeMillis() >= state.cooldownUntil) {
                state.mode = CompassMode.DEATH_COMPASS;
                state.cooldownUntil = 0L;
                player.sendMessage(plugin.config().getLangCompassCooldownEnded());
                persistState(player, state);
                scanForNewHead(player, state);
            }
            return;
        }

        if (state.mode == CompassMode.HEAD_TRACKING) {
            // If the target is no longer in the known-locations set, clear it
            if (state.trackedHead == null
                    || !knownHeadLocations.contains(normalizeLocation(state.trackedHead))) {
                state.trackedHead = null;
                state.mode = CompassMode.DEATH_COMPASS;
                setDeathCompassTarget(player);
                updateCompassItemMeta(player);
                persistState(player, state);
            }
            return;
        }

        // DEATH_COMPASS – attempt a new scan
        scanForNewHead(player, state);
    }

    private void scanForNewHead(Player player, CompassState state) {
        Location target = findNearestUncollectedHead(player);
        if (target == null) {
            player.sendMessage(plugin.config().getLangCompassNoHeads());
            state.mode = CompassMode.DEATH_COMPASS;
            setDeathCompassTarget(player);
            updateCompassItemMeta(player);
            persistState(player, state);
            return;
        }

        state.trackedHead = target;
        state.mode = CompassMode.HEAD_TRACKING;
        player.setCompassTarget(target);
        updateCompassItemMeta(player);
        persistState(player, state);
        player.sendMessage(plugin.config().getLangCompassTargetFound());
    }

    private Location findNearestUncollectedHead(Player player) {
        Location playerLoc = player.getLocation();
        Location nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (Location loc : new HashSet<>(knownHeadLocations)) {
            if (loc.getWorld() == null || !loc.getWorld().equals(playerLoc.getWorld())) continue;
            if (headQuery.hasAlreadyCollectedHead(player, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) continue;

            double distSq = playerLoc.distanceSquared(loc);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = loc;
            }
        }
        return nearest;
    }

    private void setDeathCompassTarget(Player player) {
        Location bed = player.getBedSpawnLocation();
        player.setCompassTarget(bed != null ? bed : player.getWorld().getSpawnLocation());
    }

    private void updateCompassItemMeta(Player player) {
        int slot = plugin.config().getCompassSlot();
        ItemStack item = player.getInventory().getItem(slot);
        if (item != null && isOurCompass(item)) {
            player.getInventory().setItem(slot, buildCompassItem(getState(player.getUniqueId())));
        }
    }

    private ItemStack buildCompassItem(CompassState state) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta == null) return compass;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                plugin.config().getCompassItemName()));

        List<String> lore = new ArrayList<>();
        switch (state.mode) {
            case HEAD_TRACKING:
                if (state.trackedHead != null) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',
                        "&7Tracking: &e" + state.trackedHead.getBlockX()
                        + ", " + state.trackedHead.getBlockY()
                        + ", " + state.trackedHead.getBlockZ()));
                }
                break;
            case RECHARGING:
                long remainingMs = Math.max(0L, state.cooldownUntil - System.currentTimeMillis());
                long totalSecs = remainingMs / 1000;
                long mins = totalSecs / 60;
                long secs = totalSecs % 60;
                lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&7Recharging: &e" + mins + "m " + secs + "s remaining"));
                break;
            default:
                lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&7Points to your spawn"));
                break;
        }
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(compassKey, PersistentDataType.BYTE, (byte) 1);
        compass.setItemMeta(meta);
        return compass;
    }

    private CompassState getState(UUID uuid) {
        return playerStates.computeIfAbsent(uuid, k -> new CompassState());
    }

    private void persistState(Player player, CompassState state) {
        headQuery.saveCompassState(
            player.getUniqueId(),
            state.mode.name(),
            state.trackedHead != null ? state.trackedHead.getBlockX() : null,
            state.trackedHead != null ? state.trackedHead.getBlockY() : null,
            state.trackedHead != null ? state.trackedHead.getBlockZ() : null,
            state.cooldownUntil
        );
    }

    private CompassState buildStateFromPersistence(Player player) {
        CompassState state = new CompassState();
        Map<String, Object> raw = headQuery.getRawCompassData(player.getUniqueId());
        if (raw.isEmpty()) return state;

        String modeStr = (String) raw.get("compassMode");
        if (modeStr != null) {
            try { state.mode = CompassMode.valueOf(modeStr); }
            catch (IllegalArgumentException ignored) {}
        }

        Object tx = raw.get("compassTrackedX");
        Object ty = raw.get("compassTrackedY");
        Object tz = raw.get("compassTrackedZ");
        if (tx instanceof Integer && ty instanceof Integer && tz instanceof Integer) {
            state.trackedHead = new Location(
                Bukkit.getWorld("world"),
                (Integer) tx, (Integer) ty, (Integer) tz
            );
        }

        Object cooldown = raw.get("compassCooldownUntil");
        if (cooldown instanceof Long l) state.cooldownUntil = l;
        else if (cooldown instanceof Integer i) state.cooldownUntil = i.longValue();

        return state;
    }

    private Location normalizeLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private boolean isSameLocation(Location a, Location b) {
        return a.getBlockX() == b.getBlockX()
            && a.getBlockY() == b.getBlockY()
            && a.getBlockZ() == b.getBlockZ()
            && Objects.equals(a.getWorld(), b.getWorld());
    }
}
