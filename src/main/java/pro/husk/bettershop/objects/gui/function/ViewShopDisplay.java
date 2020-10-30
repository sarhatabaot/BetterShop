package pro.husk.bettershop.objects.gui.function;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.Visibility;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.TransactionUtil;

public class ViewShopDisplay implements CommonGUI {

    @Getter
    private final Gui gui;
    private final Shop shop;
    private final StaticPane pane;
    private final Player viewer;

    public ViewShopDisplay(Shop shop, Player viewer) {
        this.shop = shop;
        this.gui = new Gui(6, ChatColor.GOLD + shop.getName());
        this.pane = new StaticPane(0, 0, 9, 6);
        this.viewer = viewer;

        forceRefreshGUI();

        gui.setOnOutsideClick(onOutsideClick -> onOutsideClick.setCancelled(true));

        gui.setOnBottomClick(click -> click.setCancelled(true));

        gui.addPane(pane);
    }

    private void renderShopItems(Shop shop, StaticPane pane, Player viewer) {
        shop.getContentsMap().forEach((slotLocation, shopItem) -> {
            Visibility visibility = shopItem.getVisibility();

            String permission = shopItem.getPermission();
            boolean isSome = permission != null;

            boolean hideItem = visibility == Visibility.HIDDEN || (isSome && !viewer.hasPermission(permission));
            if (!hideItem) {
                ItemStack itemStack = shopItem.getDisplayItem();

                GuiItem guiItem = new GuiItem(itemStack, event -> handleItemClick(event, shopItem));

                pane.addItem(guiItem, slotLocation.getX(), slotLocation.getY());
            }
        });
    }

    private void handleItemClick(InventoryClickEvent event, ShopItem shopItem) {
        Player player = (Player) event.getWhoClicked();
        ShopFunction function = shopItem.getShopFunction();

        if (function == ShopFunction.BUY) {
            new BuyDisplay(shopItem, this).show(player);
        } else if (function == ShopFunction.SELL) {
            new SellDisplay(shopItem, this).show(player);
        } else if (function == ShopFunction.TRADE) {
            new TradeDisplay(shopItem, this).show(player);
        } else if (function == ShopFunction.COMMAND) {
            processCommandSale(player, shopItem);
        } else if (function == ShopFunction.NONE) {
            event.setCancelled(true);
        }
    }

    private void processCommandSale(Player player, ShopItem shopItem) {
        double balance = TransactionUtil.getBalance(player);

        if (balance >= shopItem.getBuyCost()) {
            TransactionUtil.deduct(player, shopItem.getBuyCost());
        }
    }

    @Override
    public void forceRefreshGUI() {
        renderShopItems(shop, pane, viewer);
    }
}
