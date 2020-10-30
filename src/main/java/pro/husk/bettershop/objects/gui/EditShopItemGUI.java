package pro.husk.bettershop.objects.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.SlotLocation;

public class EditShopItemGUI {

    @Getter
    private ShopItem shopItem;

    @Getter
    private Gui gui;

    public EditShopItemGUI(ShopItem shopItem, Gui backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(3, ChatColor.GOLD + "Editing item");

        StaticPane pane = new StaticPane(0, 0, 9, 3);
        renderItems(pane, backGui);

        // Disable outside clicks
        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        // Disable bottom clicks
        gui.setOnBottomClick(onBottomClick -> {
            onBottomClick.setCancelled(true);
        });

        gui.addPane(pane);
    }

    private void renderItems(StaticPane pane, Gui backGui) {
        ItemStack displayItem = shopItem.getItemBuilder().getItemStack();

        // Build all ItemStacks of our items
        ItemStack editDisplayItem = new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + "Item display")
                .addLore(ChatColor.YELLOW + "Modify your item's display").getItemStack();

        ItemStack editFunctionItem = new ItemBuilder(Material.EMERALD).setName(ChatColor.GREEN + "Function")
                .addLore(ChatColor.GOLD + "Change the function of your item")
                .addLore(ChatColor.BLUE + "Current function: " + ChatColor.AQUA + shopItem.getShopFunction().name())
                .getItemStack();

        ItemStack editBuyCostItem = new ItemBuilder(Material.GOLD_INGOT).setName(ChatColor.GOLD + "Change cost")
                .addLore(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + shopItem.getBuyCost()).getItemStack();

        ItemStack editSellCostItem = new ItemBuilder(Material.DIAMOND).setName(ChatColor.GOLD + "Change sell cost")
                .addLore(ChatColor.WHITE + "Sell: " + ChatColor.GREEN + shopItem.getSellCost()).getItemStack();

        ItemStack editContentsItem = new ItemBuilder(Material.CHEST).setName(ChatColor.GREEN + "Item inventory")
                .addLore(ChatColor.GOLD + "Edit item's inventory").getItemStack();

        ItemStack editMessagesItem = new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Messages")
                .addLore(ChatColor.YELLOW + "Manage the messages your item sends").getItemStack();

        ItemStack editVisibilityItem = new ItemBuilder(Material.GLASS).setName(ChatColor.GREEN + "Change visibility")
                .addLore(ChatColor.WHITE + "Visibility: " + ChatColor.YELLOW + shopItem.getVisibility()).getItemStack();

        ItemStack backButtonItem = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED + "Back")
                .addLore(ChatColor.GOLD + "Go back a page").getItemStack();

        // Build GuiItem of each of the ItemStacks
        GuiItem displayGuiItem = new GuiItem(displayItem, event -> event.setCancelled(true));

        GuiItem editDisplayGuiItem = new GuiItem(editDisplayItem, event -> {
            Player player = (Player) event.getWhoClicked();
            Gui editDisplay = new EditDisplayItemGUI(shopItem, gui).getGui();
            editDisplay.show(player);
        });

        GuiItem editFunctionGuiItem = new GuiItem(editFunctionItem, event -> {

        });

        GuiItem editBuyCostGuiItem = new GuiItem(editBuyCostItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setBuyCost(Integer.parseInt(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                renderItems(pane, backGui);
                gui.update();
            }, "Please input the cost to buy this item");
        });

        GuiItem editSellCostGuiItem = new GuiItem(editSellCostItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setSellCost(Integer.parseInt(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                renderItems(pane, backGui);
                gui.update();
            }, "Please input the price rewarded on sale of this item");
        });

        GuiItem editContentsGuiItem = new GuiItem(editContentsItem, event -> {

        });

        GuiItem editMessagesGuiItem = new GuiItem(editMessagesItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.addMessage(callback);
                PlayerChatInput.removeWaitingOnInput(player);
                renderItems(pane, backGui);
                gui.update();
            }, "Please input the new message");
        });

        GuiItem editVisibilityGuiItem = new GuiItem(editVisibilityItem, event -> {

        });

        GuiItem backButtonGuiItem = new GuiItem(backButtonItem, click -> {
            click.setCancelled(true);
            backGui.show(click.getWhoClicked());
        });

        // Build the SlotLocation for each of these
        SlotLocation displayItemSlot = SlotLocation.fromSlotNumber(4, pane.getLength());
        SlotLocation editDisplaySlot = SlotLocation.fromSlotNumber(13, pane.getLength());
        SlotLocation editFunctionSlot = SlotLocation.fromSlotNumber(1, pane.getLength());
        SlotLocation editBuyCostSlot = SlotLocation.fromSlotNumber(19, pane.getLength());
        SlotLocation editSellCostSlot = SlotLocation.fromSlotNumber(18, pane.getLength());
        SlotLocation editContentsSlot = SlotLocation.fromSlotNumber(19, pane.getLength());
        SlotLocation editMessagesSlot = SlotLocation.fromSlotNumber(8, pane.getLength());
        SlotLocation editVisiblitySlot = SlotLocation.fromSlotNumber(10, pane.getLength());
        SlotLocation backButtonSlot = SlotLocation.fromSlotNumber(22, pane.getLength());

        // Insert them all onto the pane
        pane.addItem(displayGuiItem, displayItemSlot.getX(), displayItemSlot.getY());
        pane.addItem(editDisplayGuiItem, editDisplaySlot.getX(), editDisplaySlot.getY());
        pane.addItem(editFunctionGuiItem, editFunctionSlot.getX(), editFunctionSlot.getY());
        pane.addItem(editMessagesGuiItem, editMessagesSlot.getX(), editMessagesSlot.getY());
        pane.addItem(editVisibilityGuiItem, editVisiblitySlot.getX(), editVisiblitySlot.getY());
        pane.addItem(backButtonGuiItem, backButtonSlot.getX(), backButtonSlot.getY());

        // Build the gui relevant to the function
        ShopFunction function = shopItem.getShopFunction();
        if (function == ShopFunction.BUY) {
            pane.addItem(editBuyCostGuiItem, editBuyCostSlot.getX(), editBuyCostSlot.getY());
            pane.addItem(editSellCostGuiItem, editSellCostSlot.getX(), editSellCostSlot.getY());
        } else if (function == ShopFunction.SELL) {
            pane.addItem(editSellCostGuiItem, editSellCostSlot.getX(), editSellCostSlot.getY());
        } else if (function == ShopFunction.TRADE) {
            pane.addItem(editContentsGuiItem, editContentsSlot.getX(), editContentsSlot.getY());
        } else if (function == ShopFunction.COMMAND) {
            pane.addItem(editBuyCostGuiItem, editBuyCostSlot.getX(), editBuyCostSlot.getY());
        }

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("").getItemStack();
        pane.fillWith(filler);
    }
}
