package pro.husk.bettershop.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;
import pro.husk.bettershop.BetterShop;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.ShopManager;

public class MenuHelper {

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
        slot.setItemTemplate(p -> {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("Change display name");
            item.setItemMeta(itemMeta);
            return item;
        });

        slot.setClickHandler((player, clickInformation) -> {
            if (clickInformation.getClickType() == ClickType.LEFT) {

                menu.close(player);

                // Wait for input from async player chat
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    // Adjust the name to the callback given
                    ItemMeta itemMeta = shopItem.getItemStack().getItemMeta();
                    itemMeta.setDisplayName(colourise(callback));
                    shopItem.getItemStack().setItemMeta(itemMeta);

                    // Reopen the menu
                    menu.open(player);
                }, "Please enter the new name (including colours / hex): ");
            }
        });

        slot = menu.getSlot(1);
        slot.setItemTemplate(p -> {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("Change function");
            item.setItemMeta(itemMeta);
            return item;
        });

        slot = menu.getSlot(2);

        slot.setItem(shopItem.getItemStack());

        return menu;
    }

    private static Menu getFunctionMenu(ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu
                .builder(1)
                .title("Editing function for item in shop: " + shopName)
                .build();


        return menu;
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
