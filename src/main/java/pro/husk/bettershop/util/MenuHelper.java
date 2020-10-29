package pro.husk.bettershop.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.ShopManager;

public class MenuHelper {

    /**
     * Main method of handling edit menu.. todo seperate this into bite sized chunks
     *
     * @param shopItem that is being edited
     * @param shopName that the shopItem belongs to
     */
    public static void openEditMenu(Player playerToOpenFor, ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu.builder(3).title(ChatColor.GOLD + shopName).build();

        menu.setCloseHandler((player, closedMenu) -> {
            Shop editingShop = ShopManager.getEditingMap().get(player.getUniqueId());

            if (editingShop != null) {
                editingShop.saveEdits(shopItem);

                // Remove user from editing map
                ShopManager.getEditingMap().remove(player.getUniqueId());
            }
        });

        // Set the invidiual items
        setEditDisplayItem(menu, shopItem);
        setEditFunctionItem(menu, shopItem, shopName);
        setDisplayItem(menu, shopItem);

        ShopFunction shopFunction = shopItem.getShopFunction();

        if (shopFunction == ShopFunction.BUY || shopFunction == ShopFunction.COMMAND) {
            setEditBuyCostItem(menu, shopItem);
        } else if (shopFunction == ShopFunction.SELL) {
            setEditSellPriceItem(menu, shopItem);
        } else if (shopFunction == ShopFunction.TRADE) {
            setEditContentsItem(menu, shopItem);
        }

        for (int i = 0; i < menu.getDimensions().getArea(); i++) {
            Slot empty = menu.getSlot(i);

            ItemStack item = empty.getItem(playerToOpenFor);

            if (item == null || item.getType() == Material.AIR) {
                empty.setItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("").getItemStack());
            }
        }

        menu.open(playerToOpenFor);
    }

    private static void openFunctionMenu(ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu.builder(1).title("Editing function for item in shop: " + shopName).build();

        // Display an item for each function
        for (int i = 0; i < ShopFunction.values().length; i++) {
            ShopFunction function = ShopFunction.values()[i];
            Slot slot = menu.getSlot(i);

            slot.setItemTemplate(player -> new ItemBuilder(Material.BOOKSHELF).setName(ChatColor.GOLD + function.name())
                    .getItemStack());

            // Click handler for the function change
            slot.setClickHandler((player, clickInformation) -> {
                if (clickInformation.getClickType() == ClickType.LEFT) {
                    int clickedSlotIndex = clickInformation.getClickedSlot().getIndex();
                    shopItem.setShopFunction(ShopFunction.values()[clickedSlotIndex]);

                    // Reopen the shop edit menu
                    openEditMenu(player, shopItem, shopName);
                }
            });
        }
    }

    private static void setEditDisplayItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(13);
        slot.setItem(new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + "Item display")
                .addLore(ChatColor.YELLOW + "Modify your item's display").getItemStack());

        // Click handler for change display name
        slot.setClickHandler((player, clickInformation) -> {
            if (clickInformation.getClickType() == ClickType.LEFT) {

                // Time to close the menu and listen for input
                menu.close(player);

                // Wait for input from async player chat
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    // Adjust the name to the callback given
                    shopItem.getItemBuilder().setName(callback);

                    // Reopen the menu
                    menu.open(player);
                }, "Please enter the new name (including colours / hex)");
            }
        });
    }

    private static void setEditFunctionItem(Menu menu, ShopItem shopItem, String shopName) {
        Slot slot = menu.getSlot(1);
        slot.setItem(new ItemBuilder(Material.EMERALD).setName(ChatColor.GREEN + "Function")
                .addLore(ChatColor.GOLD + "Change the function of your item")
                .addLore(ChatColor.BLUE + "Current function: " + ChatColor.AQUA + shopItem.getShopFunction().name())
                .getItemStack());

        // Click handler for change function type
        slot.setClickHandler((player, clickInformation) -> openFunctionMenu(shopItem, shopName));
    }

    private static void setEditBuyCostItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(19);

        slot.setItem(new ItemBuilder(Material.GOLD_INGOT).setName(ChatColor.GOLD + "Change cost")
                .addLore(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + shopItem.getBuyCost()).getItemStack());

        // Set buy cost click handling
        slot.setClickHandler((player, clickInformation) -> {
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setBuyCost(Integer.parseInt(callback));
            }, "Please input the cost to buy this item:");
        });
    }

    private static void setEditSellPriceItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(19);

        slot.setItem(new ItemBuilder(Material.DIAMOND).setName(ChatColor.GOLD + "Change sell cost")
                .addLore(ChatColor.WHITE + "Sell: " + ChatColor.GREEN + shopItem.getSellCost()).getItemStack());

        // Set sell cost click handling
        slot.setClickHandler((player, clickInformation) -> {
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setSellCost(Integer.parseInt(callback));
            }, "Please input the price to sell this item at:");
            menu.close(player);
        });
    }

    private static void setDisplayItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(4);
        slot.setItem(shopItem.getItemBuilder().getItemStack());
        slot.setClickOptions(ClickOptions.DENY_ALL);
    }

    private static void setEditContentsItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(19);
        slot.setItem(new ItemBuilder(Material.CHEST).setName(ChatColor.GREEN + "Item Inventory")
                .addLore(ChatColor.GOLD + "Edit item's inventory").getItemStack());
        slot.setClickOptions(ClickOptions.DENY_ALL);
    }
}
