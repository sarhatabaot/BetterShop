package pro.husk.bettershop.objects.gui;

import java.util.Optional;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.Visibility;
import pro.husk.bettershop.util.SlotLocation;

public class EditShopDisplay {

    @Getter
    private Gui gui;

    private ShopItem moveItem;

    public EditShopDisplay(Shop shop) {
        this.gui = new Gui(6, ChatColor.GOLD + shop.getName());

        StaticPane pane = new StaticPane(0, 0, 9, 6);
        renderShopItems(shop, pane);

        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        gui.setOnTopClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();
            ShopItem clickedShopItem = shop.getContentsMap()
                    .get(SlotLocation.fromSlotNumber(click.getSlot(), pane.getLength()));

            // Handle moving items from bottom inv to upper
            if (moveItem != null) {
                if (isItemStackEmpty(clickedItem)) {
                    shop.addItem(moveItem, SlotLocation.fromSlotNumber(click.getSlot(), pane.getLength()));
                    click.getWhoClicked().getInventory().remove(moveItem.getItemBuilder().getItemStack());
                    moveItem = null;
                    gui.setTitle(ChatColor.GOLD + shop.getName());
                    renderShopItems(shop, pane);
                }
            } else {
                if (click.isRightClick()) {
                    moveItem = clickedShopItem;
                    gui.setTitle(ChatColor.GREEN + "Now select the new slot");
                } else if (click.isLeftClick()) {
                    if (clickedShopItem != null) {
                        EditShopItemGUI editShopItemGUI = new EditShopItemGUI(clickedShopItem, gui);
                        editShopItemGUI.getGui().show(click.getWhoClicked());
                    }
                }
            }

            gui.update();
        });

        // Handle moving items from bottom inv to upper
        gui.setOnBottomClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();

            if (!isItemStackEmpty(clickedItem)) {
                moveItem = new ShopItem(clickedItem, ShopFunction.NONE, 0, 0, 0, Visibility.ALL, Optional.empty(),
                        Optional.empty());
                gui.setTitle(ChatColor.GREEN + "Now select the new slot");
            }

            gui.update();
        });

        gui.addPane(pane);
    }

    private boolean isItemStackEmpty(ItemStack check) {
        if (check == null)
            return true;
        if (check.getType() == Material.AIR)
            return true;

        return false;
    }

    private void renderShopItems(Shop shop, StaticPane pane) {
        shop.getContentsMap().forEach((slotLocation, shopItem) -> {
            ItemStack itemStack = shopItem.getItemBuilder().getItemStack();

            GuiItem guiItem = new GuiItem(itemStack, event -> {
                if (event.getClick() == ClickType.RIGHT) {
                    new EditShopItemGUI(shopItem, gui).getGui().show(event.getWhoClicked());
                }
            });

            pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
        });
    }
}
