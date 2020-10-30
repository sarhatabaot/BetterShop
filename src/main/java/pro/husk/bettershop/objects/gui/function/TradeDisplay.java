package pro.husk.bettershop.objects.gui.function;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import pro.husk.bettershop.util.TransactionUtil;

import java.util.HashMap;
import java.util.List;

public class TradeDisplay implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final StaticPane pane;
    private final CommonGUI backGui;

    public TradeDisplay(ShopItem shopItem, CommonGUI backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(3, ChatColor.GOLD + "Trade item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);

        forceRefreshGUI();

        gui.setOnGlobalClick(click -> click.setCancelled(true));

        gui.addPane(pane);
    }

    private void renderMenu(ShopItem shopItem, StaticPane pane, CommonGUI backGui) {
        ItemStack displayItem = shopItem.getItemStack().clone();
        ItemStack confirmItem = ItemBuilder.builder(Material.GREEN_STAINED_GLASS_PANE).name(ChatColor.GREEN + "Confirm")
                .addLore(ChatColor.GOLD + "Click me to confirm the trade").build();

        ItemStack filler = ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name("").build();
        GuiItem fillerGuiItem = new GuiItem(filler);

        // Load the contents of the shop
        List<ItemStack> shopItemContents = shopItem.getContents();
        for (int i = 0; i < shopItemContents.size(); i++) {
            ItemStack itemStack = shopItemContents.get(i);

            GuiItem guiItem = new GuiItem(itemStack);
            SlotLocation slotLocation = SlotLocation.fromSlotNumber(i + 10, pane.getLength());

            pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
        }

        // Fill top
        for (int i = 0; i < 9; i++) {
            SlotLocation slotLocation = SlotLocation.fromSlotNumber(i, pane.getLength());
            pane.addItem(fillerGuiItem, slotLocation.getX(), slotLocation.getY());
        }

        // Fill bottom
        for (int i = 18; i < 27; i++) {
            SlotLocation slotLocation = SlotLocation.fromSlotNumber(i, pane.getLength());
            pane.addItem(fillerGuiItem, slotLocation.getX(), slotLocation.getY());
        }

        GuiItem backButton = MenuHelper.getBackButton(backGui);
        GuiItem displayGuiItem = new GuiItem(displayItem);
        GuiItem confirmGuiItem = new GuiItem(confirmItem, event -> handleTrade(event, shopItem));

        // Get the respective locations
        SlotLocation displayLocation = SlotLocation.fromSlotNumber(22, pane.getLength());
        SlotLocation backLocation = SlotLocation.fromSlotNumber(21, pane.getLength());
        SlotLocation confirmLocation = SlotLocation.fromSlotNumber(23, pane.getLength());

        // Set the items in their locations
        pane.addItem(displayGuiItem, displayLocation.getX(), displayLocation.getY());
        pane.addItem(backButton, backLocation.getX(), backLocation.getY());
        pane.addItem(confirmGuiItem, confirmLocation.getX(), confirmLocation.getY());

    }

    private void handleTrade(InventoryClickEvent event, ShopItem shopItem) {
        Player player = (Player) event.getWhoClicked();

        boolean hasAll = true;
        for (ItemStack itemStack : shopItem.getContents()) {
            if (TransactionUtil.getContainsAmount(player, itemStack) <= 0) {
                hasAll = false;
            }
        }

        if (hasAll) {
            shopItem.getContents().forEach(itemStack -> TransactionUtil.removeCustomItem(player, itemStack));

            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(shopItem.getItemStack());
            if (!failedItems.isEmpty()) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), shopItem.getItemStack());
            }

            List<String> messages = shopItem.getMessages();
            if (messages != null && messages.size() != 0) {
                messages.forEach(player::sendMessage);
            } else {
                player.sendMessage(ChatColor.GREEN + "You have traded for " + ChatColor.AQUA
                        + shopItem.getItemStack().getAmount() + shopItem.getItemStackName());
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void forceRefreshGUI() {
        renderMenu(shopItem, pane, backGui);
    }
}
