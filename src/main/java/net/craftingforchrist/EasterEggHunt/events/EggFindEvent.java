package net.craftingforchrist.EasterEggHunt.events;

import net.craftingforchrist.EasterEggHunt.EasterEggHuntMain;
import net.craftingforchrist.EasterEggHunt.EggChatController;
import net.craftingforchrist.EasterEggHunt.EggController;
import net.craftingforchrist.EasterEggHunt.EggHatController;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static net.craftingforchrist.EasterEggHunt.EggController.alreadyCollectedEgg;
import static net.craftingforchrist.EasterEggHunt.EggController.getEggs;
import static net.craftingforchrist.EasterEggHunt.EggScoreboardController.loadSidebarScoreboard;

public class EggFindEvent implements Listener {
    public static EasterEggHuntMain plugin;
    public EggFindEvent(EasterEggHuntMain instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEggFind(PlayerInteractEvent event) {

        try {
            Player player = event.getPlayer();
            EquipmentSlot EquipSlot = event.getHand();

            Block block = event.getClickedBlock();
            if (block.getType() == null) return;
            String blockType = String.valueOf(block.getType());

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int eggs = getEggs(player) + 1;

            String EGGBLOCK = plugin.getConfig().getString("EGG.EGGBLOCK");
            String MAJORCOLLECTIONMILESTONESOUND = plugin.getConfig().getString("SOUND.MAJORCOLLECTIONMILESTONE");
            String MINORCOLLECTIONMILESTONESOUND = plugin.getConfig().getString("SOUND.MINORCOLLECTIONMILESTONE");

            // This stops the event from firing twice, since the event fires for each hand.
            if (EquipSlot.equals(EquipmentSlot.OFF_HAND) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
            if (!EGGBLOCK.equals(blockType)) return;

            switch(eggs) {
                case 10:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MINORCOLLECTIONMILESTONESOUND)), eggs);
                    event.setCancelled(true);
                    break;
                case 50:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MINORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.LEATHER_HELMET);
                    event.setCancelled(true);
                    break;
                case 100:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MAJORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.CHAINMAIL_HELMET);
                    event.setCancelled(true);
                    break;
                case 150:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MINORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.IRON_HELMET);
                    event.setCancelled(true);
                    break;
                case 200:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MINORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.GOLDEN_HELMET);
                    event.setCancelled(true);
                    break;
                case 250:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MINORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.DIAMOND_HELMET);
                    event.setCancelled(true);
                    break;
                case 300:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MAJORCOLLECTIONMILESTONESOUND)), eggs);
                    EggHatController.equipHelmet(player, Material.NETHERITE_HELMET);
                    event.setCancelled(true);
                    break;
                case 500:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MAJORCOLLECTIONMILESTONESOUND)), eggs);
                    event.setCancelled(true);
                    break;
                case 1000:
                    EggChatController.eggMilestoneReachedEvent(player, Sound.valueOf(String.valueOf(MAJORCOLLECTIONMILESTONESOUND)), eggs);
                    event.setCancelled(true);
                    break;
                default:
                    // code block
            }

            if (alreadyCollectedEgg(player, x, y, z) == true) {
                EggChatController.eggAlreadyFoundResponse(player);
                event.setCancelled(true);
            } else if (alreadyCollectedEgg(player, x, y, z) == false) {
                EggController.insertCollectedEgg(player, block, x, y, z);
                loadSidebarScoreboard(player);
            }
        } catch (NullPointerException error) {
            return;
        }

    }
}