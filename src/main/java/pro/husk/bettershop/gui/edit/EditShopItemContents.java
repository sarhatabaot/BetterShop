package pro.husk.bettershop.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.gui.CommonGUI;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import pro.husk.bettershop.util.TransactionUtil;

public class EditShopItemContents implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final CommonGUI backGui;
    private final StaticPane pane;

    public EditShopItemContents(ShopItem shopItem, CommonGUI backGui) {
        this.gui = new Gui(1, ChatColor.GOLD + "Editing contents:");
        this.shopItem = shopItem;
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);

        forceRefreshGUI();

        // Disable outside clicks
        gui.setOnOutsideClick(onOutsideClick -> onOutsideClick.setCancelled(true));

        gui.setOnBottomClick(click -> {
            ItemStack clickedItem = click.getCurrentItem();

            if (!MenuHelper.isItemStackEmpty(clickedItem)) {
                shopItem.getContents().add(clickedItem);
                TransactionUtil.removeCustomItem((Player) click.getWhoClicked(), clickedItem);
                renderItems(pane, backGui, shopItem);
                gui.update();
                click.setCancelled(true);
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
            if (MenuHelper.isItemStackEmpty(itemStack)) continue;
            GuiItem guiItem = new GuiItem(itemStack, event -> {
                ItemStack clickedItem = event.getCurrentItem();
                shopItem.getContents().remove(clickedItem);
                event.getWhoClicked().getInventory().addItem(clickedItem);
                forceRefreshGUI();
                gui.update();
                event.setCancelled(true);
            });

            SlotLocation slotLoc = SlotLocation.fromSlotNumber(i, pane.getLength());
            pane.addItem(guiItem, slotLoc.getX(), slotLoc.getY());
            i++;
        }

        GuiItem backButton = MenuHelper.getBackButton(backGui);
        SlotLocation backButtonLocation = SlotLocation.fromSlotNumber(8, pane.getLength());
        pane.addItem(backButton, backButtonLocation.getX(), backButtonLocation.getY());
    }

    @Override
    public void forceRefreshGUI() {
        pane.clear();
        renderItems(pane, backGui, shopItem);
    }
}
