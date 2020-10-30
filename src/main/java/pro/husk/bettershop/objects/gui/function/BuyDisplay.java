package pro.husk.bettershop.objects.gui.function;

import java.util.HashMap;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import pro.husk.bettershop.util.TransactionUtil;

public class BuyDisplay implements CommonGUI {

    @Getter
    private Gui gui;
    private ShopItem shopItem;
    private StaticPane pane;
    private Player viewer;
    private int amount;

    public BuyDisplay(ShopItem shopItem, Player viewer) {
        this.shopItem = shopItem;
        this.gui = new Gui(6, ChatColor.GOLD + "Buy item:");
        this.pane = new StaticPane(0, 0, 9, 6);
        this.viewer = viewer;
        this.amount = 0;

        forceRefreshGUI();

        gui.setOnOutsideClick(onOutsideClick -> {
            onOutsideClick.setCancelled(true);
        });

        gui.setOnBottomClick(click -> {
            click.setCancelled(true);
        });

        gui.addPane(pane);
    }

    private void renderMenu(ShopItem shopItem, StaticPane pane, Player viewer) {
        double cost = amount * shopItem.getBuyCost();

        ItemBuilder plusItem = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);

        ItemStack plus1 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+1").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-1")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack plus8 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+8").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-8")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack plus16 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+16").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-16")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack plus32 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+32").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-32")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack plus64 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+64").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-64")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack plus128 = plusItem.setName(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+128").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-128")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).getItemStack().clone();

        ItemStack displayItem = shopItem.getItemBuilder()
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost)
                .addLore("", ChatColor.DARK_GREEN + "Click to buy").getItemStack().clone();

        GuiItem backButton = MenuHelper.getBackButton(this);

        GuiItem displayGuiItem = new GuiItem(displayItem, event -> {
            handlePurchase(event, shopItem, amount, cost);
            event.setCancelled(true);
        });

        GuiItem plus1GuiItem = new GuiItem(plus1, event -> {
            amount += 1;
            forceRefreshGUI();
            event.setCancelled(true);
        });

        GuiItem plus8GuiItem = new GuiItem(plus8, event -> {
            amount += 8;
            forceRefreshGUI();
            event.setCancelled(true);
        });

        GuiItem plus16GuiItem = new GuiItem(plus16, event -> {
            amount += 16;
            forceRefreshGUI();
            event.setCancelled(true);
        });

        GuiItem plus32GuiItem = new GuiItem(plus32, event -> {
            amount += 32;
            forceRefreshGUI();
        });

        GuiItem plus64GuiItem = new GuiItem(plus64, event -> {
            amount += 64;
            forceRefreshGUI();
            event.setCancelled(true);
        });

        GuiItem plus128GuiItem = new GuiItem(plus128, event -> {
            amount += 128;
            forceRefreshGUI();
            event.setCancelled(true);
        });

        // Get the respective locations
        SlotLocation plus1Location = SlotLocation.fromSlotNumber(20, pane.getLength());
        SlotLocation plus8Location = SlotLocation.fromSlotNumber(21, pane.getLength());
        SlotLocation plus16Location = SlotLocation.fromSlotNumber(22, pane.getLength());
        SlotLocation plus32Location = SlotLocation.fromSlotNumber(23, pane.getLength());
        SlotLocation plus64Location = SlotLocation.fromSlotNumber(24, pane.getLength());
        SlotLocation plus128Location = SlotLocation.fromSlotNumber(31, pane.getLength());
        SlotLocation displayLocation = SlotLocation.fromSlotNumber(49, pane.getLength());
        SlotLocation backLocation = SlotLocation.fromSlotNumber(4, pane.getLength());

        // Set the items in their locations
        pane.addItem(plus1GuiItem, plus1Location.getX(), plus1Location.getY());
        pane.addItem(plus8GuiItem, plus8Location.getX(), plus8Location.getY());
        pane.addItem(plus16GuiItem, plus16Location.getX(), plus16Location.getY());
        pane.addItem(plus32GuiItem, plus32Location.getX(), plus32Location.getY());
        pane.addItem(plus64GuiItem, plus64Location.getX(), plus64Location.getY());
        pane.addItem(plus128GuiItem, plus128Location.getX(), plus128Location.getY());
        pane.addItem(displayGuiItem, displayLocation.getX(), displayLocation.getY());
        pane.addItem(backButton, backLocation.getX(), backLocation.getY());

        // Fill remainder
        pane.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("").getItemStack());
    }

    private void handlePurchase(InventoryClickEvent event, ShopItem shopItem, int amount, double cost) {
        ItemStack itemStack = shopItem.getItemBuilder().setAmount(amount).getItemStack();
        Player player = (Player) event.getWhoClicked();

        double balance = TransactionUtil.getBalance(player);

        if (balance >= cost) {
            TransactionUtil.deduct(player, amount);

            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
            if (!failedItems.isEmpty()) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
            } else {
                if (shopItem.getMessagesOptional().isPresent()) {
                    shopItem.getMessagesOptional().get().forEach(message -> {
                        player.sendMessage(message);
                    });
                } else {
                    player.sendMessage(ChatColor.GREEN + "You have purchased " + ChatColor.AQUA + amount
                            + ChatColor.WHITE + shopItem.getItemBuilder().getName() + ChatColor.GREEN + " for "
                            + ChatColor.DARK_GREEN + "$" + cost);

                    player.sendMessage(ChatColor.GREEN + "Your new balance is " + ChatColor.DARK_GREEN + "$" + balance);
                }
            }
        }
    }

    @Override
    public void forceRefreshGUI() {
        renderMenu(shopItem, pane, viewer);
    }
}
