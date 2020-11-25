package pro.husk.bettershop.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.gui.CommonGUI;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import pro.husk.bettershop.util.TransactionUtil;

public class EditShopDisplay implements CommonGUI {

    @Getter
    private final Gui gui;
    private final Shop shop;
    private final StaticPane pane;
    private ShopItem moveItem;
    private SlotLocation priorSlotLocation;

    public EditShopDisplay(Shop shop) {
        this.shop = shop;
        this.gui = new Gui(6, ChatColor.GOLD + shop.getName());
        this.pane = new StaticPane(0, 0, 9, 6);

        forceRefreshGUI();

        gui.setOnOutsideClick(onOutsideClick -> onOutsideClick.setCancelled(true));

        gui.setOnTopClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();
            SlotLocation slotLocation = SlotLocation.fromSlotNumber(click.getSlot(), pane.getLength());
            ShopItem clickedShopItem = shop.getContentsMap()
                    .get(slotLocation);

            Player clicker = (Player) click.getWhoClicked();

            // Handle moving items from bottom inv to upper
            if (moveItem != null) {
                if (MenuHelper.isItemStackEmpty(clickedItem)) {

                    // Remove the source of the shop item correctly
                    if (priorSlotLocation != null) {
                        shop.getContentsMap().remove(priorSlotLocation);
                    } else {
                        TransactionUtil.removeCustomItem(clicker, moveItem.getItemStack());
                    }

                    // Add only one of the item (issue #21)
                    moveItem.getItemStack().setAmount(1);
                    shop.addItem(moveItem, slotLocation);

                    moveItem = null;
                    priorSlotLocation = null;
                    gui.setTitle(ChatColor.GOLD + shop.getName());
                    forceRefreshGUI();
                }
            } else {
                if (click.isRightClick()) {
                    moveItem = clickedShopItem;
                    priorSlotLocation = slotLocation;
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
            } else if (moveItem != null && priorSlotLocation != null) {
                shop.getContentsMap().remove(priorSlotLocation, moveItem);
                click.getWhoClicked().getInventory().setItem(click.getSlot(), moveItem.getItemStack());
                moveItem = null;
                priorSlotLocation = null;
                gui.setTitle(ChatColor.GOLD + shop.getName());
                forceRefreshGUI();
            }

            gui.update();
        });

        gui.addPane(pane);
    }

    private void renderShopItems(Shop shop, StaticPane pane) {
        shop.getContentsMap().forEach((slotLocation, shopItem) -> {
            ItemStack itemStack = shopItem.getItemStack();

            GuiItem guiItem = new GuiItem(itemStack, event -> {
                if (event.getClick() == ClickType.LEFT) {
                    if (moveItem == null && priorSlotLocation == null) {
                        new EditShopItemGUI(shopItem, this).show(event.getWhoClicked());
                    } else {
                        moveItem = null;
                        priorSlotLocation = null;
                        gui.setTitle(ChatColor.GOLD + shop.getName());
                        gui.show(event.getWhoClicked());
                    }
                }
            });

            pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
        });
    }

    @Override
    public void forceRefreshGUI() {
        pane.clear();
        renderShopItems(shop, pane);
    }
}
