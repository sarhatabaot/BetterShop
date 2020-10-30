package pro.husk.bettershop.objects.gui.function;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;
import pro.husk.bettershop.util.TransactionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyDisplay implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final StaticPane pane;
    private final CommonGUI backGui;
    private int amount;

    public BuyDisplay(ShopItem shopItem, CommonGUI backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(6, ChatColor.GOLD + "Buy item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 6);
        this.amount = 0;

        forceRefreshGUI();

        gui.setOnGlobalClick(click -> click.setCancelled(true));

        gui.addPane(pane);
    }

    private void renderMenu(ShopItem shopItem, StaticPane pane, CommonGUI backGui) {

        double cost = amount * shopItem.getBuyCost();

        ItemBuilder.Builder plusItem = ItemBuilder.builder(Material.GREEN_STAINED_GLASS_PANE);

        ItemStack plus1 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+1").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-1")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack plus8 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+8").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-8")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack plus16 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+16").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-16")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack plus32 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+32").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-32")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack plus64 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+64").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-64")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack plus128 = plusItem.name(ChatColor.YELLOW + "Left click: " + ChatColor.GREEN + "+128").clearLore()
                .addLore(ChatColor.YELLOW + "Right click: " + ChatColor.RED + "-128")
                .addLore("", ChatColor.BLUE + "Amount: " + amount, ChatColor.GREEN + "Price: " + cost).build();

        ItemStack displayItem = shopItem.getItemStack().clone();
        ItemMeta itemMeta = displayItem.getItemMeta();
        List<String> lore = itemMeta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add("");
        lore.add(ChatColor.BLUE + "Amount: " + amount);
        lore.add(ChatColor.GREEN + "Price: " + cost);

        itemMeta.setLore(lore);
        displayItem.setItemMeta(itemMeta);

        GuiItem backButton = MenuHelper.getBackButton(backGui);

        GuiItem displayGuiItem = new GuiItem(displayItem, event -> handlePurchase(event, shopItem, amount, cost));

        GuiItem plus1GuiItem = new GuiItem(plus1, event -> {
            handleClick(event, 1);
            show(event.getWhoClicked());
        });

        GuiItem plus8GuiItem = new GuiItem(plus8, event -> {
            handleClick(event, 8);
            show(event.getWhoClicked());
        });

        GuiItem plus16GuiItem = new GuiItem(plus16, event -> {
            handleClick(event, 16);
            show(event.getWhoClicked());
        });

        GuiItem plus32GuiItem = new GuiItem(plus32, event -> {
            handleClick(event, 32);
            show(event.getWhoClicked());
        });

        GuiItem plus64GuiItem = new GuiItem(plus64, event -> {
            handleClick(event, 64);
            show(event.getWhoClicked());
        });

        GuiItem plus128GuiItem = new GuiItem(plus128, event -> {
            handleClick(event, 128);
            show(event.getWhoClicked());
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
        pane.fillWith(ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name("").build());
    }

    private void handleClick(InventoryClickEvent event, int adjustment) {
        ClickType clickType = event.getClick();
        if (clickType == ClickType.LEFT) {
            amount += adjustment;
        } else if (clickType == ClickType.RIGHT) {
            if (amount - adjustment > 0) {
                amount -= adjustment;
            } else {
                event.setCancelled(true);
            }
        }
    }

    private void handlePurchase(InventoryClickEvent event, ShopItem shopItem, int amount, double cost) {
        ItemStack itemStack = shopItem.getItemStack().clone();
        itemStack.setAmount(amount);

        Player player = (Player) event.getWhoClicked();

        double balance = TransactionUtil.getBalance(player);

        if (balance >= cost) {
            TransactionUtil.deduct(player, amount);

            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
            if (!failedItems.isEmpty()) {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }

            List<String> messages = shopItem.getMessagesOptional().get();
            if (messages.size() != 0) {
                messages.forEach(player::sendMessage);
            } else {
                player.sendMessage(ChatColor.GREEN + "You have purchased " + ChatColor.AQUA + amount + ChatColor.WHITE
                        + " " + shopItem.getItemStackName() + ChatColor.GREEN + " for " + ChatColor.DARK_GREEN + "$"
                        + cost);

                player.sendMessage(ChatColor.GREEN + "Your new balance is " + ChatColor.DARK_GREEN + "$" + balance);
            }

            player.closeInventory();
        }
    }

    @Override
    public void forceRefreshGUI() {
        renderMenu(shopItem, pane, backGui);
    }
}
