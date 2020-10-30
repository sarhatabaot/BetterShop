package pro.husk.bettershop.util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    @Getter
    @Setter
    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private ItemMeta getItemMetaSafely() {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }

        return itemMeta;
    }

    public ItemBuilder setName(String name) {
        ItemMeta itemMeta = getItemMetaSafely();

        itemMeta.setDisplayName(StringUtil.colourise(name));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public String getName() {
        ItemMeta itemMeta = getItemMetaSafely();
        return itemMeta.getDisplayName();
    }

    public ItemBuilder setAmount(int newAmount) {
        itemStack.setAmount(newAmount);
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        ItemMeta itemMeta = getItemMetaSafely();

        List<String> lore = new ArrayList<>();

        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        }

        for (String line : lines) {
            lore.add(StringUtil.colourise(line));
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder clearLore() {
        return setLore(new ArrayList<>());
    }

    public ItemBuilder removeLore() {
        ItemMeta itemMeta = getItemMetaSafely();

        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            lore.remove(lore.size() - 1);
        }

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta itemMeta = getItemMetaSafely();

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder setLoreIfSetOnArgument(ItemStack argument) {
        ItemMeta itemMeta = argument.getItemMeta();

        if (itemMeta != null) {
            if (itemMeta.hasLore()) {
                this.setLore(itemMeta.getLore());
            }
        }

        return this;
    }

    public ItemBuilder addNameToLoreIfSetOnArgument(ItemStack argument) {
        ItemMeta itemMeta = argument.getItemMeta();

        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                this.addLore(ChatColor.WHITE + itemMeta.getDisplayName());
            }
        }

        return this;
    }

    public boolean hasLore() {
        return getItemMetaSafely().hasLore();
    }
}
