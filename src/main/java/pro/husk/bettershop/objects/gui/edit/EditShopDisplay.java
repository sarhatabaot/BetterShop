package pro.husk.bettershop.objects.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

public class EditShopDisplay implements CommonGUI {

    @Getter
    private Gui gui;
    private Shop shop;
    private StaticPane pane;
    private ShopItem moveItem;

    public EditShopDisplay(Shop shop) {
        this.shop = shop;
        this.gui = new Gui(6, ChatColor.GOLD + shop.getName());
        this.pane = new StaticPane(0, 0, 9, 6);

        forceRefreshGUI();

        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        gui.setOnTopClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();
            ShopItem clickedShopItem = shop.getContentsMap()
                    .get(SlotLocation.fromSlotNumber(click.getSlot(), pane.getLength()));

            // Handle moving items from bottom inv to upper
            if (moveItem != null) {
                if (MenuHelper.isItemStackEmpty(clickedItem)) {
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
                        EditShopItemGUI editShopItemGUI = new EditShopItemGUI(clickedShopItem, this);
                        editShopItemGUI.getGui().show(click.getWhoClicked());
                    }
                }
            }

            gui.update();
        });

        // Handle moving items from bottom inv to upper
        gui.setOnBottomClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();

            if (!MenuHelper.isItemStackEmpty(clickedItem)) {
                moveItem = new ShopItem(clickedItem);
                gui.setTitle(ChatColor.GREEN + "Now select the new slot");
            }

            gui.update();
        });

        gui.addPane(pane);
    }

    private void renderShopItems(Shop shop, StaticPane pane) {
        shop.getContentsMap().forEach((slotLocation, shopItem) -> {
            ItemStack itemStack = shopItem.getItemBuilder().getItemStack();

            GuiItem guiItem = new GuiItem(itemStack, event -> {
                if (event.getClick() == ClickType.RIGHT) {
                    new EditShopItemGUI(shopItem, this).show(event.getWhoClicked());
                }
            });

            pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
        });
    }

    @Override
    public void forceRefreshGUI() {
        renderShopItems(shop, pane);
    }
}
