package pro.husk.bettershop.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {

    public static Builder builder(Material type, int amount) {
        return new Builder(type, amount);
    }

    public static Builder builder(Material type) {
        return new Builder(type, 1);
    }

    public static Builder builder(ItemStack itemStack) {
        return new Builder(itemStack);
    }

    public static class Builder {

        private Material type;
        private int amount;
        private String displayName;
        private List<String> lore;

        private Builder(Material type, int amount) {
            this.type = type;
            this.amount = amount;
        }

        private Builder(ItemStack itemStack) {
            this.type = itemStack.getType();
            this.amount = itemStack.getAmount();
            this.lore = itemStack.getItemMeta().getLore();
            this.displayName = itemStack.getItemMeta().getDisplayName();
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder type(Material type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.displayName = name;
            return this;
        }

        public void lore(List<String> lore) {
            this.lore = lore;
        }

        public Builder clearLore() {
            this.lore = new ArrayList<>();
            return this;
        }

        public Builder addLore(String... lore) {
            if (this.lore == null)
                this.lore = new ArrayList<>();
            Collections.addAll(this.lore, lore);
            return this;
        }

        public Builder removeLore() {
            int lastIndex = this.lore.size() - 1;
            lore.remove(lastIndex);
            return this;
        }

        public ItemStack build() {
            ItemStack itemStack = new ItemStack(type, amount);

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!(displayName == null || displayName.equals(""))) {
                itemMeta.setDisplayName(displayName);
            }

            if (lore != null && !lore.isEmpty()) {
                itemMeta.setLore(lore);
            }

            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        public void setLoreIfSetOnArgument(ItemStack argument) {
            ItemMeta itemMeta = argument.getItemMeta();

            if (itemMeta != null) {
                if (itemMeta.hasLore()) {
                    lore(itemMeta.getLore());
                }
            }
        }

        public void addNameToLoreIfSetOnArgument(ItemStack argument) {
            ItemMeta itemMeta = argument.getItemMeta();

            if (itemMeta != null) {
                if (itemMeta.hasDisplayName()) {
                    addLore(itemMeta.getDisplayName());
                }
            }
        }
    }
}
