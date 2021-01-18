package pro.husk.bettershop.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.BetterShop;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseInterface implements Listener {

    @Getter
    protected Inventory inventory;

    @Getter
    private final Map<ItemStack, Runnable> clickActions;

    public BaseInterface(Player player, Inventory inventory) {
        this.inventory = inventory;
        clickActions = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, BetterShop.getInstance());
        renderShopItems();

        // Mark them as viewing this inv
        BetterShop.getInstance().getInterfaceManager().setViewing(player, this);
    }

    protected abstract void renderShopItems();

    protected abstract void handleClickEvent(InventoryClickEvent event);

    public void showTo(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == inventory) {
            handleClickEvent(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent event) {
        if (event.getInventory() == inventory) {
            event.setCancelled(true);
        }
    }
}
