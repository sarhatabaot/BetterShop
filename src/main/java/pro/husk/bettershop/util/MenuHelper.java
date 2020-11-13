package pro.husk.bettershop.util;

import com.github.stefvanschie.inventoryframework.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.gui.CommonGUI;

public final class MenuHelper {

    public static GuiItem getBackButton(CommonGUI backGui) {
        ItemStack backButtonItem = ItemBuilder.builder(Material.COMPASS).name(ChatColor.RED + "Back")
                .addLore(ChatColor.GOLD + "Go back a page").build();

        return new GuiItem(backButtonItem, click -> {
            click.setCancelled(true);
            backGui.show(click.getWhoClicked());
        });
    }

    public static boolean isItemStackEmpty(ItemStack check) {
        if (check == null) return true;
        return check.getType() == Material.AIR;
    }
}
