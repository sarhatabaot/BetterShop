package pro.husk.bettershop.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.ShopManager;

public class MenuHelper {

    /**
     * Main method of handling edit menu..
     * todo seperate this into bite sized chunks
     *
     * @param shopItem that is being edited
     * @param shopName that the shopItem belongs to
     */
    public static Menu getEditMenu(ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu
                .builder(1)
                .title("Editing item for: " + shopName)
                .build();

        menu.setCloseHandler((player, closedMenu) -> {
            Shop editingShop = ShopManager.getEditingMap().get(player.getUniqueId());

            if (editingShop != null) {
                editingShop.saveEdits(shopItem);
            }
        });

        Slot slot = menu.getSlot(0);
        slot.setItem(new ItemBuilder(Material.BOOK)
                .setName(ChatColor.GOLD + "Item display")
                .addLore(ChatColor.YELLOW + "Modify your item's display")
                .getItemStack());

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

        slot = menu.getSlot(1);
        slot.setItem(new ItemBuilder(Material.INK_SAC)
                .setName(ChatColor.GOLD + "Function")
                .addLore(ChatColor.GREEN + "Change the function of your item")
                .addLore(ChatColor.BLUE + "Current function: " + ChatColor.AQUA + shopItem.getShopFunction().name())
                .getItemStack());

        // Click handler for change function type
        slot.setClickHandler((player, clickInformation) -> openFunctionMenu(shopItem, shopName));

        ShopFunction shopFunction = shopItem.getShopFunction();

        if (shopFunction == ShopFunction.BUY
                || shopFunction == ShopFunction.COMMAND) {
            slot = menu.getSlot(2);

            slot.setItem(new ItemBuilder(Material.GOLD_INGOT)
                    .setName(ChatColor.GOLD + "Set price")
                    .getItemStack());

            // Set buy cost click handling
            slot.setClickHandler((player, clickInformation) -> {
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.setBuyCost(Integer.parseInt(callback));
                }, "Please input the cost to buy this item:");
            });

        } else if (shopFunction == ShopFunction.SELL) {
            // Set sell cost click handling
            slot.setClickHandler((player, clickInformation) -> {
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.setSellCost(Integer.parseInt(callback));
                }, "Please input the price to sell this item at:");
                menu.close(player);
            });
        }

        slot = menu.getSlot(8);

        slot.setItem(shopItem.getItemBuilder().getItemStack());

        return menu;
    }

    private static void openFunctionMenu(ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu
                .builder(1)
                .title("Editing function for item in shop: " + shopName)
                .build();

        // Display an item for each function
        for (int i = 0; i < ShopFunction.values().length; i++) {
            ShopFunction function = ShopFunction.values()[i];
            Slot slot = menu.getSlot(i);

            slot.setItemTemplate(player -> new ItemBuilder(Material.BOOKSHELF)
                    .setName(ChatColor.GOLD + function.name())
                    .getItemStack());

            // Click handler for the function change
            slot.setClickHandler((player, clickInformation) -> {
                if (clickInformation.getClickType() == ClickType.LEFT) {
                    int clickedSlotIndex = clickInformation.getClickedSlot().getIndex();
                    shopItem.setShopFunction(ShopFunction.values()[clickedSlotIndex]);

                    // Reopen the shop edit menu
                    getEditMenu(shopItem, shopName).open(player);
                }
            });
        }
    }

    /**
     * Helper method to colourise a string with a combination of both hex and legacy chat colours
     *
     * @param input string to colourise
     * @return colourised string
     */
    public static String colourise(String input) {
        while (input.contains("#")) {
            int index = input.indexOf("#");
            if (index != 0 && input.charAt(index - 1) == '&') {
                String hexSubstring = input.substring(index - 1, index + 7).replaceAll("&", "");

                try {
                    ChatColor transformed = ChatColor.of(hexSubstring);
                    // Apply transformation to original string
                    input = input.replaceAll("&" + hexSubstring, transformed + "");
                } catch (IllegalArgumentException ignored) {

                }
            } else {
                break;
            }
        }

        // Apply legacy transformations at end
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
