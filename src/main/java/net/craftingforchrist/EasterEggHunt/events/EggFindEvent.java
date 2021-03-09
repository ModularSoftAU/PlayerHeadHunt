package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.craftingforchrist.EasterEggHunt.EggChatController;
import net.craftingforchrist.EasterEggHunt.EggController;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static net.craftingforchrist.EasterEggHunt.EggController.alreadyCollectedEgg;

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
            if (alreadyCollectedEgg(player, x, y, z) == true) {
                EggChatController.eggAlreadyFoundResponse(player);
                event.setCancelled(true);
            } else if (alreadyCollectedEgg(player, x, y, z) == false) {
                EggController.insertCollectedEgg(player, block, x, y, z);
            }
        }
    }
}