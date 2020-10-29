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
import pro.husk.bettershop.objects.Visibility;

public class MenuHelper {

    /**
     * Main method of handling edit menu.. todo seperate this into bite sized chunks
     *
     * @param playerToOpenFor player who we are opening this edit menu for
     * @param shopItem        that is being edited
     * @param shopName        that the shopItem belongs to
     */
    public static void openEditMenu(Player playerToOpenFor, ShopItem shopItem, String shopName) {
        Menu menu = ChestMenu.builder(3).title(ChatColor.GOLD + shopName).build();

        ItemStack backButton = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED + "Back")
                .addLore(ChatColor.GOLD + "Go back a page").getItemStack();

        menu.setCloseHandler((player, closedMenu) -> {
            Shop editingShop = ShopManager.getEditingMap().get(player.getUniqueId());

            if (editingShop != null) {
                editingShop.saveEdits(shopItem);

                // Remove user from editing map
                ShopManager.getEditingMap().remove(player.getUniqueId());
            }
        });

        // Set the invidiual items
        setEditDisplayItem(menu, shopItem, shopName, backButton);
        setEditFunctionItem(menu, shopItem, shopName, backButton);
        setDisplayItem(menu, shopItem);
        setEditMessagesItem(menu, shopItem, shopName);
        setBackItem(menu, backButton);
        setVisibilityItem(menu, shopItem, shopName, backButton);

        ShopFunction shopFunction = shopItem.getShopFunction();

        if (shopFunction == ShopFunction.BUY || shopFunction == ShopFunction.COMMAND) {
            setEditBuyCostItem(menu, shopItem, shopName);
        } else if (shopFunction == ShopFunction.SELL) {
            setEditSellPriceItem(menu, shopItem, shopName);
        } else if (shopFunction == ShopFunction.TRADE) {
            setEditContentsItem(menu);
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

    private static void openFunctionMenu(Player playerToOpenFor, ShopItem shopItem, String shopName,
            ItemStack backButton) {
        Menu menu = ChestMenu.builder(1).title(ChatColor.GOLD + "Editing function").build();

        int shopFunctionAmount = ShopFunction.values().length;

        // Display an item for each function
        for (int i = 0; i < shopFunctionAmount; i++) {
            ShopFunction function = ShopFunction.values()[i];
            Slot slot = menu.getSlot(i);

            slot.setItemTemplate(player -> new ItemBuilder(Material.BOOKSHELF).setName(ChatColor.GOLD + function.name())
                    .getItemStack());

            // Click handler for the function change
            slot.setClickHandler((player, clickInformation) -> {
                int clickedSlotIndex = clickInformation.getClickedSlot().getIndex();
                shopItem.setShopFunction(ShopFunction.values()[clickedSlotIndex]);

                // Reopen the shop edit menu
                openEditMenu(player, shopItem, shopName);
            });
        }

        subPageBackButtonAndCloseHandler(playerToOpenFor, shopItem, shopName, menu,
                menu.getDimensions().getColumns() - 1, backButton);
    }

    private static void openVisibilityMenu(Player playerToOpenFor, ShopItem shopItem, String shopName,
            ItemStack backButton) {
        Menu menu = ChestMenu.builder(1).title(ChatColor.GOLD + "Editing Visiblity").build();

        int visibilityAmount = Visibility.values().length;

        // Display an item for each function
        for (int i = 0; i < visibilityAmount; i++) {
            Visibility visibility = Visibility.values()[i];
            Slot slot = menu.getSlot(i);

            slot.setItemTemplate(player -> new ItemBuilder(Material.RED_STAINED_GLASS)
                    .setName(ChatColor.GOLD + visibility.name()).getItemStack());

            // Click handler for the function change
            slot.setClickHandler((player, clickInformation) -> {
                int clickedSlotIndex = clickInformation.getClickedSlot().getIndex();
                shopItem.setVisibility(Visibility.values()[clickedSlotIndex]);
                menu.close(player);
                openEditMenu(player, shopItem, shopName);
            });
        }

        subPageBackButtonAndCloseHandler(playerToOpenFor, shopItem, shopName, menu,
                menu.getDimensions().getColumns() - 1, backButton);
    }

    private static void openEditItemDisplayMenu(Player playerToOpenFor, ShopItem shopItem, String shopName,
            ItemStack backButton) {
        Menu menu = ChestMenu.builder(1).title(ChatColor.GOLD + "Editing Display").build();

        Slot name = menu.getSlot(0);
        Slot lore = menu.getSlot(1);
        Slot amount = menu.getSlot(2);

        ItemBuilder nameItemBuilder = new ItemBuilder(Material.NAME_TAG)
                .setName(ChatColor.GREEN + "Change display name");

        ItemBuilder loreItemBuilder = new ItemBuilder(Material.BOOK).setName(ChatColor.GREEN + "Add lore");

        ItemBuilder amountItemBuilder = new ItemBuilder(Material.ANVIL).setName(ChatColor.GREEN + "Change amount")
                .addLore("" + ChatColor.WHITE + shopItem.getItemBuilder().getItemStack().getAmount());

        ItemStack shopItemStack = shopItem.getItemBuilder().getItemStack();

        nameItemBuilder.addNameToLoreIfSetOnArgument(shopItemStack);
        loreItemBuilder.setLoreIfSetOnArgument(shopItemStack);

        name.setItem(nameItemBuilder.getItemStack());
        name.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.getItemBuilder().setName(callback);
                openEditItemDisplayMenu(playerToOpenFor, shopItem, shopName, backButton);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please enter the new name");
        });
        name.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());

        lore.setItem(loreItemBuilder.getItemStack());
        lore.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.getItemBuilder().addLore(callback);
                openEditItemDisplayMenu(playerToOpenFor, shopItem, shopName, backButton);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please enter the line of lore you want added");
        });
        lore.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());

        amount.setItem(amountItemBuilder.getItemStack());
        amount.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.getItemBuilder().setAmount(Integer.parseInt(callback));
                openEditItemDisplayMenu(playerToOpenFor, shopItem, shopName, backButton);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please enter the amount you want to be displayed!");
        });
        amount.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());

        subPageBackButtonAndCloseHandler(playerToOpenFor, shopItem, shopName, menu,
                menu.getDimensions().getColumns() - 1, backButton);
    }

    private static void subPageBackButtonAndCloseHandler(Player playerToOpenFor, ShopItem shopItem, String shopName,
            Menu menu, int backButtonLocation, ItemStack backButton) {
        Slot slot = menu.getSlot(backButtonLocation);
        slot.setItem(backButton);
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
        slot.setClickHandler((player, clickInformation) -> handleBackButton(player, shopItem, shopName));

        menu.open(playerToOpenFor);
    }

    private static void handleBackButton(Player player, ShopItem shopItem, String shopName) {
        openEditMenu(player, shopItem, shopName);
    }

    private static void setEditDisplayItem(Menu menu, ShopItem shopItem, String shopName, ItemStack backButton) {
        Slot slot = menu.getSlot(13);
        slot.setItem(new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + "Item display")
                .addLore(ChatColor.YELLOW + "Modify your item's display").getItemStack());

        slot.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            openEditItemDisplayMenu(player, shopItem, shopName, backButton);
        });
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setEditFunctionItem(Menu menu, ShopItem shopItem, String shopName, ItemStack backButton) {
        Slot slot = menu.getSlot(1);
        slot.setItem(new ItemBuilder(Material.EMERALD).setName(ChatColor.GREEN + "Function")
                .addLore(ChatColor.GOLD + "Change the function of your item")
                .addLore(ChatColor.BLUE + "Current function: " + ChatColor.AQUA + shopItem.getShopFunction().name())
                .getItemStack());

        // Click handler for change function type
        slot.setClickHandler((player, clickInformation) -> openFunctionMenu(player, shopItem, shopName, backButton));
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setEditBuyCostItem(Menu menu, ShopItem shopItem, String shopName) {
        Slot slot = menu.getSlot(19);

        slot.setItem(new ItemBuilder(Material.GOLD_INGOT).setName(ChatColor.GOLD + "Change cost")
                .addLore(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + shopItem.getBuyCost()).getItemStack());

        // Set buy cost click handling
        slot.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setBuyCost(Integer.parseInt(callback));
                openEditMenu(player, shopItem, shopName);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please input the cost to buy this item:");
        });

        // Only allow left clicks
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setEditSellPriceItem(Menu menu, ShopItem shopItem, String shopName) {
        Slot slot = menu.getSlot(18);

        slot.setItem(new ItemBuilder(Material.DIAMOND).setName(ChatColor.GOLD + "Change sell cost")
                .addLore(ChatColor.WHITE + "Sell: " + ChatColor.GREEN + shopItem.getSellCost()).getItemStack());

        // Set sell cost click handling
        slot.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.setSellCost(Integer.parseInt(callback));
                openEditMenu(player, shopItem, shopName);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please input the price to sell this item at:");
        });

        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setDisplayItem(Menu menu, ShopItem shopItem) {
        Slot slot = menu.getSlot(4);
        slot.setItem(shopItem.getItemBuilder().getItemStack());
        slot.setClickOptions(ClickOptions.DENY_ALL);
    }

    private static void setEditContentsItem(Menu menu) {
        Slot slot = menu.getSlot(19);
        slot.setItem(new ItemBuilder(Material.CHEST).setName(ChatColor.GREEN + "Item inventory")
                .addLore(ChatColor.GOLD + "Edit item's inventory").getItemStack());
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setEditMessagesItem(Menu menu, ShopItem shopItem, String shopName) {
        Slot slot = menu.getSlot(8);
        slot.setItem(new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Messages")
                .addLore(ChatColor.YELLOW + "Manage the messages your item sends").getItemStack());
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());

        slot.setClickHandler((player, clickInformation) -> {
            menu.close(player);
            PlayerChatInput.addWaitingOnInput(player, callback -> {
                shopItem.addMessage(callback);
                openEditMenu(player, shopItem, shopName);
                PlayerChatInput.removeWaitingOnInput(player);
            }, "Please input the new message");
        });
    }

    private static void setBackItem(Menu menu, ItemStack itemStack) {
        Slot slot = menu.getSlot(22);
        slot.setItem(itemStack);
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
    }

    private static void setVisibilityItem(Menu menu, ShopItem shopItem, String shopName, ItemStack backButton) {
        Slot slot = menu.getSlot(10);
        String visibleString = shopItem.getVisibility().name();
        slot.setItem(new ItemBuilder(Material.GLASS).setName(ChatColor.GREEN + "Change visibility")
                .addLore(ChatColor.WHITE + "Visibility: " + ChatColor.YELLOW + visibleString).getItemStack());
        slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());
        slot.setClickHandler((player, clickInformation) -> {
            openVisibilityMenu(player, shopItem, shopName, backButton);
        });
    }
}
