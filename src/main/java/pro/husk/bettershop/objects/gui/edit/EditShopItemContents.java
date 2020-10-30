package pro.husk.bettershop.objects.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.inventory.ItemStack;

import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class EditShopItemContents implements CommonGUI {

    @Getter
    private Gui gui;
    private ShopItem shopItem;
    private CommonGUI backGui;
    private StaticPane pane;

    public EditShopItemContents(ShopItem shopItem, CommonGUI backGui) {
        this.gui = new Gui(1, ChatColor.GOLD + "Editing contents:");
        this.shopItem = shopItem;
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);

        forceRefreshGUI();

        // Disable outside clicks
        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        gui.setOnBottomClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();

            if (!MenuHelper.isItemStackEmpty(clickedItem)) {
                shopItem.getContents().add(clickedItem);
                renderItems(pane, backGui, shopItem);
            }
        });

        // Add back button
        SlotLocation slotLocation = SlotLocation.fromSlotNumber(8, pane.getLength());
        pane.addItem(MenuHelper.getBackButton(backGui), slotLocation.getX(), slotLocation.getY());

        gui.addPane(pane);
    }

    private void renderItems(StaticPane pane, CommonGUI backGui, ShopItem shopItem) {
        int i = 0;
        for (ItemStack itemStack : shopItem.getContents()) {
            GuiItem guiItem = new GuiItem(itemStack, event -> {
                ItemStack clickedItem = event.getCurrentItem();
                shopItem.getContents().remove(clickedItem);
                event.getWhoClicked().getInventory().addItem(clickedItem);
                renderItems(pane, backGui, shopItem);
            });

            SlotLocation slotLoc = SlotLocation.fromSlotNumber(i, pane.getLength());
            pane.addItem(guiItem, slotLoc.getX(), slotLoc.getY());
            i++;
        }
    }

    @Override
    public void forceRefreshGUI() {
        renderItems(pane, backGui, shopItem);
    }
}
