package pro.husk.bettershop.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.gui.CommonGUI;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.Visibility;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

import java.util.List;

public class EditShopItemGUI implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final CommonGUI backGui;
    private final StaticPane pane;

    public EditShopItemGUI(ShopItem shopItem, CommonGUI backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(3, ChatColor.GOLD + "Editing item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 3);

        forceRefreshGUI();

        gui.setOnGlobalClick(click -> {
            click.setCancelled(true);
        });

        gui.addPane(pane);
    }

    private void renderItems(StaticPane pane, CommonGUI backGui) {
        ItemStack displayItem = shopItem.getItemStack().clone();

        // Build all ItemStacks of our items
        ItemStack editDisplayItem = ItemBuilder.builder(Material.BOOK).name(ChatColor.GOLD + "Item display")
                .addLore(ChatColor.YELLOW + "Modify your item's display").build();

        ItemStack editFunctionItem = ItemBuilder.builder(Material.EMERALD).name(ChatColor.GREEN + "Function")
                .addLore(ChatColor.GOLD + "Change the function of your item")
                .addLore(ChatColor.BLUE + "Current function: " + ChatColor.AQUA + shopItem.getShopFunction().name())
                .build();

        ItemStack editBuyCostItem = ItemBuilder.builder(Material.GOLD_INGOT).name(ChatColor.GOLD + "Change buy price")
                .addLore(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + shopItem.getBuyCost()).build();

        ItemStack editSellCostItem = ItemBuilder.builder(Material.DIAMOND).name(ChatColor.GOLD + "Change sell price")
                .addLore(ChatColor.WHITE + "Sell: " + ChatColor.GREEN + shopItem.getSellCost()).build();

        ItemStack editContentsItem = ItemBuilder.builder(Material.CHEST).name(ChatColor.GREEN + "Item inventory")
                .addLore(ChatColor.GOLD + "Edit item's inventory").build();

        ItemBuilder.Builder editMessagesItemBuilder = ItemBuilder.builder(Material.PAPER)
                .name(ChatColor.GOLD + "Messages").addLore(ChatColor.YELLOW + "Manage the messages your item sends");

        ItemBuilder.Builder editCommandItemBuilder = ItemBuilder.builder(Material.WRITTEN_BOOK)
                .name(ChatColor.GOLD + "Commands")
                .addLore(ChatColor.YELLOW + "Add commands to run when users buy this item");

        ItemStack editCloseOnTransactionItem = ItemBuilder.builder(Material.BARRIER)
                .name(ChatColor.GOLD + "Close on transaction")
                .addLore(ChatColor.YELLOW + "Should this item close the menu on finalised transaction?")
                .addLore(ChatColor.RED + "Currently: " + shopItem.isCloseOnTransaction()).build();

        // Add messages to the lore if present
        List<String> messages = shopItem.getMessages();
        if (messages != null) {
            editMessagesItemBuilder.addLore("");
            messages.forEach(editMessagesItemBuilder::addLore);
        }

        // Add commands to the lore if present
        List<String> commands = shopItem.getCommands();
        if (commands != null) {
            editCommandItemBuilder.addLore("");
            commands.forEach(editCommandItemBuilder::addLore);
        }

        ItemStack editMessagesItem = editMessagesItemBuilder.build();
        ItemBuilder.Builder editPermissionsItemBuilder = ItemBuilder.builder(Material.BARRIER)
                .name(ChatColor.GOLD + "Permissions")
                .addLore(ChatColor.YELLOW + "Users with this permission will see the item in the shop");

        // Add the permission in the item's lore if present
        String permission = shopItem.getPermission();
        if (permission != null) {
            editMessagesItemBuilder.addLore("");
            editMessagesItemBuilder.addLore(ChatColor.GREEN + permission);
        }

        ItemStack editPermissionsItem = editPermissionsItemBuilder.build();
        ItemStack editVisibilityItem = ItemBuilder.builder(Material.GLASS).name(ChatColor.GREEN + "Change visibility")
                .addLore(ChatColor.WHITE + "Visibility: " + ChatColor.YELLOW + shopItem.getVisibility()).build();

        // Build GuiItem of each of the ItemStacks
        GuiItem displayGuiItem = new GuiItem(displayItem, event -> event.setCancelled(true));
        GuiItem editDisplayGuiItem = new GuiItem(editDisplayItem, event -> {
            Gui editDisplay = new EditDisplayItemGUI(shopItem, this).getGui();
            editDisplay.show(event.getWhoClicked());
        });

        GuiItem editFunctionGuiItem = new GuiItem(editFunctionItem, event -> {
            Gui editFunction = new EditShopItemFunction(shopItem, this).getGui();
            editFunction.show(event.getWhoClicked());
        });

        GuiItem editBuyCostGuiItem = new GuiItem(editBuyCostItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setBuyCost(Integer.parseInt(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the cost to buy this item");
        });

        GuiItem editSellCostGuiItem = new GuiItem(editSellCostItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setSellCost(Integer.parseInt(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the price rewarded on sale of this item");
        });

        GuiItem editContentsGuiItem = new GuiItem(editContentsItem, event -> {
            Gui editContents = new EditShopItemContents(shopItem, this).getGui();
            editContents.show(event.getWhoClicked());
        });

        GuiItem editMessagesGuiItem = new GuiItem(editMessagesItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.addMessage(callback);
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the new message");
        });

        GuiItem editPermissionsGuiItem = new GuiItem(editPermissionsItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setPermission(callback);
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the permission node you want users to be able to see this item in the shop");
        });

        GuiItem editCommandsGuiItem = new GuiItem(editCommandItemBuilder.build(), event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.addCommand(callback);
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the command you wish to run");
        });

        GuiItem editVisibilityGuiItem = new GuiItem(editVisibilityItem, event -> {
            Gui editVisibility = new EditShopItemVisibility(shopItem, this).getGui();
            editVisibility.show(event.getWhoClicked());
        });

        GuiItem editCloseOnTransactionGuiItem = new GuiItem(editCloseOnTransactionItem, event -> {
            shopItem.setCloseOnTransaction(!shopItem.isCloseOnTransaction());
            renderItems(pane, backGui);
            gui.update();
        });

        // Build the SlotLocation for each of these
        SlotLocation displayItemSlot = SlotLocation.fromSlotNumber(4, pane.getLength());
        SlotLocation editDisplaySlot = SlotLocation.fromSlotNumber(13, pane.getLength());
        SlotLocation editFunctionSlot = SlotLocation.fromSlotNumber(1, pane.getLength());
        SlotLocation editBuyCostSlot = SlotLocation.fromSlotNumber(19, pane.getLength());
        SlotLocation editSellCostSlot = SlotLocation.fromSlotNumber(18, pane.getLength());
        SlotLocation editContentsSlot = SlotLocation.fromSlotNumber(19, pane.getLength());
        SlotLocation editMessagesSlot = SlotLocation.fromSlotNumber(8, pane.getLength());
        SlotLocation editPermissionsSlot = SlotLocation.fromSlotNumber(15, pane.getLength());
        SlotLocation editCommandsSlot = SlotLocation.fromSlotNumber(16, pane.getLength());
        SlotLocation editVisiblitySlot = SlotLocation.fromSlotNumber(10, pane.getLength());
        SlotLocation backButtonSlot = SlotLocation.fromSlotNumber(22, pane.getLength());
        SlotLocation editCloseOnTransactionSlot = SlotLocation.fromSlotNumber(17, pane.getLength());

        // Insert them all onto the pane
        pane.addItem(displayGuiItem, displayItemSlot.getX(), displayItemSlot.getY());
        pane.addItem(editDisplayGuiItem, editDisplaySlot.getX(), editDisplaySlot.getY());
        pane.addItem(editFunctionGuiItem, editFunctionSlot.getX(), editFunctionSlot.getY());
        pane.addItem(editMessagesGuiItem, editMessagesSlot.getX(), editMessagesSlot.getY());
        pane.addItem(editVisibilityGuiItem, editVisiblitySlot.getX(), editVisiblitySlot.getY());
        pane.addItem(MenuHelper.getBackButton(backGui), backButtonSlot.getX(), backButtonSlot.getY());
        pane.addItem(editCloseOnTransactionGuiItem, editCloseOnTransactionSlot.getX(), editCloseOnTransactionSlot.getY());

        // Build the gui relevant to the function
        ShopFunction function = shopItem.getShopFunction();
        if (function == ShopFunction.BUY) {
            pane.addItem(editBuyCostGuiItem, editBuyCostSlot.getX(), editBuyCostSlot.getY());
        } else if (function == ShopFunction.SELL) {
            pane.addItem(editSellCostGuiItem, editSellCostSlot.getX(), editSellCostSlot.getY());
        } else if (function == ShopFunction.BUY_AND_SELL) {
            pane.addItem(editBuyCostGuiItem, editBuyCostSlot.getX(), editBuyCostSlot.getY());
            pane.addItem(editSellCostGuiItem, editSellCostSlot.getX(), editSellCostSlot.getY());
        } else if (function == ShopFunction.TRADE) {
            pane.addItem(editContentsGuiItem, editContentsSlot.getX(), editContentsSlot.getY());
        } else if (function == ShopFunction.COMMAND) {
            pane.addItem(editBuyCostGuiItem, editBuyCostSlot.getX(), editBuyCostSlot.getY());
            pane.addItem(editCommandsGuiItem, editCommandsSlot.getX(), editCommandsSlot.getY());
        }

        if (shopItem.getVisibility() == Visibility.PERMISSION) {
            pane.addItem(editPermissionsGuiItem, editPermissionsSlot.getX(), editPermissionsSlot.getY());
        }

        ItemStack filler = ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name("").build();
        pane.fillWith(filler);
    }

    @Override
    public void forceRefreshGUI() {
        pane.clear();
        renderItems(pane, backGui);
    }
}
