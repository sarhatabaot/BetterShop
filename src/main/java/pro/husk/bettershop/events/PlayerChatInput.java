package pro.husk.bettershop.events;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pro.husk.bettershop.BetterShop;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This is simply a utility to take player input from chat upon request
 */
public class PlayerChatInput implements Listener {

    private static final HashMap<UUID, Consumer<String>> waitingOnInput = new HashMap<>();

    /**
     * Adds the target player to be monitored for text input
     * 
     * @param player   to monitor
     * @param consumer of where to return the result
     * @param prompt   of what to send them prior to their input
     */
    public static void addWaitingOnInput(Player player, Consumer<String> consumer, String prompt) {
        waitingOnInput.put(player.getUniqueId(), consumer);
        player.sendMessage(ChatColor.GREEN + "[BetterShop] " + prompt);
    }

    /**
     * Removes the target player from being monitored for text input
     * 
     * @param player to unmonitor
     */
    public static void removeWaitingOnInput(Player player) {
        waitingOnInput.remove(player.getUniqueId());
    }

    /**
     * This method returns whether or not a player is being polled for input
     * 
     * @param player in question
     * @return whether or not monitoring the player is occuring
     */
    public static boolean isWaitingOnInput(Player player) {
        return waitingOnInput.containsKey(player.getUniqueId());
    }

    /**
     * Listens for player chat events to check if that player is being monitored for
     * input handling
     * 
     * @param event AsyncPlayerChatEvent
     */
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (isWaitingOnInput(player)) {
            String message = event.getMessage();

            if (!message.equalsIgnoreCase("-cancel")) {
                // Run sync cause InventoryOpen cant be done async
                Bukkit.getScheduler().runTask(BetterShop.getInstance(),
                        () -> waitingOnInput.get(player.getUniqueId()).accept(message));
                player.sendMessage(ChatColor.GREEN + "Thanks for that! You should be able to chat as per normal now!");
            } else {
                waitingOnInput.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Cancelled");
            }
            event.setCancelled(true);
        }
    }
}
