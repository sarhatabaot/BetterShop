package pro.husk.bettershop.objects.gui.edit;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
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
    private Gui gui;
    private ShopItem shopItem;
    private StaticPane pane;
    private Gui backGui;

    public EditDisplayItemGUI(ShopItem shopItem, Gui backGui) {
        this.shopItem = shopItem;
        this.gui = new Gui(1, ChatColor.GOLD + "Edit display item:");
        this.backGui = backGui;
        this.pane = new StaticPane(0, 0, 9, 1);
        renderItems(pane, backGui);

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

    private void renderItems(StaticPane pane, Gui backGui) {
        ItemBuilder nameItemBuilder = new ItemBuilder(Material.NAME_TAG)
                .setName(ChatColor.GREEN + "Change display name");

        ItemBuilder loreItemBuilder = new ItemBuilder(Material.BOOK).setName(ChatColor.GREEN + "Add lore");

        ItemBuilder amountItemBuilder = new ItemBuilder(Material.ANVIL).setName(ChatColor.GREEN + "Change amount")
                .addLore("" + ChatColor.WHITE + shopItem.getItemBuilder().getItemStack().getAmount());

        ItemStack shopItemStack = shopItem.getItemBuilder().getItemStack();

        nameItemBuilder.addNameToLoreIfSetOnArgument(shopItemStack);
        loreItemBuilder.setLoreIfSetOnArgument(shopItemStack);

        // Handle name item
        GuiItem nameItem = new GuiItem(nameItemBuilder.getItemStack(), event -> {
            Player player = (Player) event.getWhoClicked();
            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.getItemBuilder().setName(callback);
                    PlayerChatInput.removeWaitingOnInput(player);
                    renderItems(pane, backGui);
                    gui.show(player);
                }, "Please enter the new name");
            }
        });

        GuiItem loreItem = new GuiItem(loreItemBuilder.getItemStack(), event -> {
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.getItemBuilder().addLore(callback);
                    PlayerChatInput.removeWaitingOnInput(player);
                    renderItems(pane, backGui);
                    gui.show(player);
                }, "Please enter a single line of lore you want");
            } else if (event.getClick() == ClickType.RIGHT) {
                shopItem.getItemBuilder().removeLore();
            }
        });

        GuiItem amountItem = new GuiItem(amountItemBuilder.getItemStack(), event -> {
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                PlayerChatInput.addWaitingOnInput(player, callback -> {
                    shopItem.getItemBuilder().setAmount(Integer.parseInt(callback));
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
        pane.addItem(MenuHelper.getBackButton(this), backSlot.getX(), backSlot.getY());
    }

    @Override
    public void forceRefreshGUI() {
        renderItems(pane, backGui);
    }
}
