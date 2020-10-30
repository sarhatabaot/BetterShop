package pro.husk.bettershop.objects.gui.function;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.Visibility;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

public class ViewShopDisplay implements CommonGUI {

    @Getter
    private Gui gui;
    private Shop shop;
    private StaticPane pane;
    private ShopItem moveItem;

    public ViewShopDisplay(Shop shop) {
        this.shop = shop;
        this.gui = new Gui(6, ChatColor.GOLD + shop.getName());
        this.pane = new StaticPane(0, 0, 9, 6);

        forceRefreshGUI();

        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        gui.setOnBottomClick(click -> {
            click.setCancelled(true);
        });

        gui.addPane(pane);
    }

    private void renderShopItems(Shop shop, StaticPane pane) {
        shop.getContentsMap().forEach((slotLocation, shopItem) -> {
            Visibility visibility = shopItem.getVisibility();

            if (visibility != Visibility.ALL) {

            }

            ItemStack itemStack = shopItem.getItemBuilder().getItemStack();

            GuiItem guiItem = new GuiItem(itemStack, event -> {
                handleItemClick(event, shopItem);
            });

            pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
        });
    }

    private void handleItemClick(InventoryClickEvent event, ShopItem shopItem) {
        ShopFunction function = shopItem.getShopFunction();

    }

    @Override
    public void forceRefreshGUI() {
        renderShopItems(shop, pane);
    }
}
