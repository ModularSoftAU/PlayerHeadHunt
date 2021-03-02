package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.craftingforchrist.EasterEggHunt.EggChatController;
import net.craftingforchrist.EasterEggHunt.EggController;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EggFindEvent implements Listener {
    public static EasterEggHuntMain plugin;
    public EggFindEvent(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String UserUUID = player.getUniqueId().toString();
        EquipmentSlot EquipSlot = event.getHand();

        Block block = event.getClickedBlock();
        String blockType = String.valueOf(block.getType());
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String EGGBLOCK = plugin.getConfig().getString("EGG.EGGBLOCK");

        // This stops the event from firing twice, since the event fires for each hand.
        if (EquipSlot.equals(EquipmentSlot.OFF_HAND) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        if (blockType.equals(EGGBLOCK)) {
            //
            // Database Query
            // Check if the player has already found that Easter Egg before.
            //
            try {
                PreparedStatement findstatement = plugin.getConnection().prepareStatement("SELECT * FROM eastereggs WHERE playerid=(select id from playerdata where uuid=?) AND eggcordx=? AND eggcordy=? AND eggcordz=?");
                findstatement.setString(1, UserUUID);
                findstatement.setString(2, String.valueOf(x));
                findstatement.setString(3, String.valueOf(y));
                findstatement.setString(4, String.valueOf(z));

                ResultSet results = findstatement.executeQuery();
                if (!results.next()) {
                    EggController.insertCollectedEgg(player, block, x, y, z);
                } else {
                    EggChatController.eggAlreadyFoundResponse(player);
                    event.setCancelled(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("LANG.DATABASE.CONNECTIONERROR")));
                event.setCancelled(true);
            }
        }
    }
}