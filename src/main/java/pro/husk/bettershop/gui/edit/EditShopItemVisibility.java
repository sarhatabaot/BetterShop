package pro.husk.bettershop.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.Visibility;
import pro.husk.bettershop.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

public class EditShopItemVisibility implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final CommonGUI backGui;
    private final StaticPane pane;

    public EditShopItemVisibility(ShopItem shopItem, CommonGUI backGui) {
        this.gui = new Gui(1, ChatColor.GOLD + "Editing visibility:");
        this.shopItem = shopItem;
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);

        forceRefreshGUI();

        // Disable outside clicks
        gui.setOnOutsideClick(onOutsideClick -> onOutsideClick.setCancelled(true));

        // Disable bottom clicks
        gui.setOnBottomClick(onBottomClick -> onBottomClick.setCancelled(true));

        gui.addPane(pane);
    }

    private void renderItems(StaticPane pane, CommonGUI backGui, ShopItem shopItem) {
        int visibilityAmount = Visibility.values().length;

        // Display an item for each function
        for (int i = 0; i < visibilityAmount; i++) {
            Visibility visibility = Visibility.values()[i];

            ItemStack itemStack = ItemBuilder.builder(Material.GLASS).name(ChatColor.GOLD + visibility.name())
                    .build();

            GuiItem guiItem = new GuiItem(itemStack, click -> {
                int clickedSlotIndex = click.getSlot();
                shopItem.setVisibility(Visibility.values()[clickedSlotIndex]);
                backGui.show(click.getWhoClicked());
            });

            SlotLocation slot = SlotLocation.fromSlotNumber(i, pane.getLength());

            pane.addItem(guiItem, slot.getX(), slot.getY());
        }

        // Add back button
        SlotLocation backSlot = SlotLocation.fromSlotNumber(8, pane.getLength());
        pane.addItem(MenuHelper.getBackButton(backGui), backSlot.getX(), backSlot.getY());
    }

    @Override
    public void forceRefreshGUI() {
        renderItems(pane, backGui, shopItem);
    }
}
