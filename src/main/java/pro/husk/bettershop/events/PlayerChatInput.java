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

public class PlayerChatInput implements Listener {

    private static final HashMap<UUID, Consumer<String>> waitingOnInput = new HashMap<>();

    public static void addWaitingOnInput(Player player, Consumer<String> consumer, String prompt) {
        waitingOnInput.put(player.getUniqueId(), consumer);
        player.sendMessage(ChatColor.GREEN + "[BetterShop] " + prompt);
    }

    public static boolean isWaitingOnInput(Player player) {
        return waitingOnInput.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (isWaitingOnInput(player)) {
            // Run sync cause InventoryOpen cant be done async
            Bukkit.getScheduler().runTask(BetterShop.getInstance(), () -> waitingOnInput.get(player.getUniqueId()).accept(event.getMessage()));
            event.setCancelled(true);
        }
    }
}
