package pro.husk.bettershop.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    @Getter
    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(MenuHelper.colourise(name));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addLore(String... lines) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();

        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        }

        for (String line : lines) {
            lore.add(MenuHelper.colourise(line));
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return this;
    }
}
