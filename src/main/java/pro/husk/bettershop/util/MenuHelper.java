package pro.husk.bettershop.util;

import com.github.stefvanschie.inventoryframework.GuiItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import pro.husk.bettershop.objects.gui.CommonGUI;

public final class MenuHelper {

    public static GuiItem getBackButton(CommonGUI backGui) {
        ItemStack backButtonItem = new ItemBuilder(Material.COMPASS).setName(ChatColor.RED + "Back")
                .addLore(ChatColor.GOLD + "Go back a page").getItemStack();

        GuiItem backButtonGuiItem = new GuiItem(backButtonItem, click -> {
            click.setCancelled(true);
            backGui.show(click.getWhoClicked());
        });

        return backButtonGuiItem;
    }

    public static boolean isItemStackEmpty(ItemStack check) {
        if (check == null)
            return true;
        if (check.getType() == Material.AIR)
            return true;

        return false;
    }
}
