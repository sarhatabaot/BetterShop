package pro.husk.bettershop.objects.gui.edit;

import java.util.Optional;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

public class EditShopItemGUI implements CommonGUI {

    @Getter
    private Gui gui;
    private ShopItem shopItem;
    private CommonGUI backGui;
    private StaticPane pane;

    public EditShopItemGUI(ShopItem shopItem, CommonGUI backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(3, ChatColor.GOLD + "Editing item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 3);

        forceRefreshGUI();

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

    private void renderItems(StaticPane pane, CommonGUI backGui) {
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

        ItemBuilder editMessagesItemBuilder = new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Messages")
                .addLore(ChatColor.YELLOW + "Manage the messages your item sends");

        // Add the messages in the item's lore if present
        if (shopItem.getMessagesOptional().isPresent()) {
            editMessagesItemBuilder.addLore("");

            shopItem.getMessagesOptional().get().forEach(message -> {
                editMessagesItemBuilder.addLore(message);
            });
        }

        ItemStack editMessagesItem = editMessagesItemBuilder.getItemStack();

        ItemBuilder editPermissionsItemBuilder = new ItemBuilder(Material.BARRIER).setName(ChatColor.GOLD + "Permissions")
                .addLore(ChatColor.YELLOW + "Users with this permission will see the item in the shop");

        // Add the messages in the item's lore if present
        if (shopItem.getPermissionOptional().isPresent()) {
            editMessagesItemBuilder.addLore("");
            editMessagesItemBuilder.addLore(ChatColor.GREEN + shopItem.getPermissionOptional().get());
        }

        ItemStack editPermissionsItem = editPermissionsItemBuilder.getItemStack();

        ItemStack editVisibilityItem = new ItemBuilder(Material.GLASS).setName(ChatColor.GREEN + "Change visibility")
                .addLore(ChatColor.WHITE + "Visibility: " + ChatColor.YELLOW + shopItem.getVisibility()).getItemStack();

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
                shopItem.setBuyCost(Double.parseDouble(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the cost to buy this item");
        });

        GuiItem editSellCostGuiItem = new GuiItem(editSellCostItem, event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();

            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setSellCost(Double.parseDouble(callback));
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
                shopItem.setPermissionOptional(Optional.of(callback));
                PlayerChatInput.removeWaitingOnInput(player);
                this.show(player);
            }, "Please input the permission node you want users to be able to see this item in the shop");
        });

        GuiItem editVisibilityGuiItem = new GuiItem(editVisibilityItem, event -> {
            Gui editVisibility = new EditShopItemVisibility(shopItem, this).getGui();
            editVisibility.show(event.getWhoClicked());
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
        SlotLocation editVisiblitySlot = SlotLocation.fromSlotNumber(10, pane.getLength());
        SlotLocation backButtonSlot = SlotLocation.fromSlotNumber(22, pane.getLength());

        // Insert them all onto the pane
        pane.addItem(displayGuiItem, displayItemSlot.getX(), displayItemSlot.getY());
        pane.addItem(editDisplayGuiItem, editDisplaySlot.getX(), editDisplaySlot.getY());
        pane.addItem(editFunctionGuiItem, editFunctionSlot.getX(), editFunctionSlot.getY());
        pane.addItem(editMessagesGuiItem, editMessagesSlot.getX(), editMessagesSlot.getY());
        pane.addItem(editPermissionsGuiItem, editPermissionsSlot.getX(), editPermissionsSlot.getY());
        pane.addItem(editVisibilityGuiItem, editVisiblitySlot.getX(), editVisiblitySlot.getY());
        pane.addItem(MenuHelper.getBackButton(backGui), backButtonSlot.getX(), backButtonSlot.getY());

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

    @Override
    public void forceRefreshGUI() {
        renderItems(pane, backGui);
    }
}
