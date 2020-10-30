package pro.husk.bettershop.objects.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.objects.gui.CommonGUI;
import pro.husk.bettershop.util.ItemBuilder;
import pro.husk.bettershop.util.MenuHelper;
import pro.husk.bettershop.util.SlotLocation;

public class EditDisplayItemGUI implements CommonGUI {

    @Getter
    private final Gui gui;
    private final ShopItem shopItem;
    private final StaticPane pane;
    private final CommonGUI backGui;

    public EditDisplayItemGUI(ShopItem shopItem, CommonGUI backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(1, ChatColor.GOLD + "Edit display item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);

        forceRefreshGUI();

        gui.setOnGlobalClick(click -> click.setCancelled(true));

        gui.addPane(pane);
    }

    private void renderItems(StaticPane pane, CommonGUI backGui) {
        ItemBuilder.Builder nameItemBuilder = ItemBuilder.builder(Material.NAME_TAG)
                .name(ChatColor.GREEN + "Change display name");

        ItemBuilder.Builder loreItemBuilder = ItemBuilder.builder(Material.BOOK).name(ChatColor.GREEN + "Add lore");

        ItemBuilder.Builder amountItemBuilder = ItemBuilder.builder(Material.ANVIL)
                .name(ChatColor.GREEN + "Change amount")
                .addLore("" + ChatColor.WHITE + shopItem.getItemStack().getAmount());

        ItemStack shopItemStack = shopItem.getItemStack().clone();

        nameItemBuilder.addNameToLoreIfSetOnArgument(shopItemStack);
        loreItemBuilder.setLoreIfSetOnArgument(shopItemStack);

        // Handle name item
        GuiItem nameItem = new GuiItem(nameItemBuilder.build(), event -> {
            Player player = (Player) event.getWhoClicked();
            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.setItemStackName(callback);
                    PlayerChatInput.removeWaitingOnInput(player);
                    renderItems(pane, backGui);
                    gui.show(player);
                }, "Please enter the new name");
            }
        });

        GuiItem loreItem = new GuiItem(loreItemBuilder.build(), event -> {
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.setItemStack(ItemBuilder.builder(shopItemStack).addLore(callback).build());
                    PlayerChatInput.removeWaitingOnInput(player);
                    renderItems(pane, backGui);
                    gui.show(player);
                }, "Please enter a single line of lore you want");
            } else if (event.getClick() == ClickType.RIGHT) {
                shopItem.setItemStack(ItemBuilder.builder(shopItemStack).removeLore().build());
                forceRefreshGUI();
                gui.show(player);
            }
        });

        GuiItem amountItem = new GuiItem(amountItemBuilder.build(), event -> {
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.setItemStack(
                            ItemBuilder.builder(shopItemStack).amount(Integer.parseInt(callback)).build());
                    PlayerChatInput.removeWaitingOnInput(player);
                    renderItems(pane, backGui);
                    gui.show(player);
                }, "Please enter the new amount");
            }
        });

        // Build SlotLocations of the wanted slots
        SlotLocation nameSlot = SlotLocation.fromSlotNumber(0, pane.getLength());
        SlotLocation loreSlot = SlotLocation.fromSlotNumber(1, pane.getLength());
        SlotLocation amountSlot = SlotLocation.fromSlotNumber(2, pane.getLength());
        SlotLocation backSlot = SlotLocation.fromSlotNumber(8, pane.getLength());

        pane.addItem(nameItem, nameSlot.getX(), nameSlot.getY());
        pane.addItem(loreItem, loreSlot.getX(), loreSlot.getY());
        pane.addItem(amountItem, amountSlot.getX(), amountSlot.getY());
        pane.addItem(MenuHelper.getBackButton(backGui), backSlot.getX(), backSlot.getY());
    }

    @Override
    public void forceRefreshGUI() {
        pane.clear();
        renderItems(pane, backGui);
    }
}
