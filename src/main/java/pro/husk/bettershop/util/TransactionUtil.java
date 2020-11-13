package pro.husk.bettershop.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pro.husk.bettershop.BetterShop;

public final class TransactionUtil {

    /**
     * Returns how many of itemStack the player has
     *
     * @param player    to check
     * @param itemStack to check for
     * @return number of itemStack the player has
     */
    public static int getContainsAmount(Player player, ItemStack itemStack) {
        int count = 0;

        for (ItemStack playerItem : player.getInventory().getStorageContents()) {
            if (itemEquals(playerItem, itemStack))
                count += playerItem.getAmount();
        }

        return count;
    }

    /**
     * Utility method to check if two ItemStacks are equal
     *
     * @param first  itemStack to compare
     * @param second itemStack to compare
     * @return whether they are the same
     */
    public static boolean itemEquals(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return false;
        }

        boolean sameType = (first.getType() == second.getType());
        boolean sameData = (first.getData().equals(second.getData()));
        boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
        boolean sameEnchantments = (first.getEnchantments().equals(second.getEnchantments()));
        boolean sameItemMeta = false;

        if (sameHasItemMeta && first.hasItemMeta()) {
            ItemMeta firstItemMeta = first.getItemMeta();
            ItemMeta secondItemMeta = second.getItemMeta();

            boolean sameHasDisplayName = firstItemMeta.hasDisplayName() == secondItemMeta.hasDisplayName();
            boolean sameHasLore = firstItemMeta.hasLore() == secondItemMeta.hasLore();

            boolean sameDisplayName = false;
            boolean sameLore = false;

            if (sameHasDisplayName && firstItemMeta.hasDisplayName()) {
                sameDisplayName = firstItemMeta.getDisplayName().equals(secondItemMeta.getDisplayName());
            } else if (!firstItemMeta.hasDisplayName() && !secondItemMeta.hasDisplayName()) sameDisplayName = true;

            if (sameHasLore && firstItemMeta.hasLore()) {
                sameLore = firstItemMeta.getLore().equals(secondItemMeta.getLore());
            } else if (!firstItemMeta.hasLore() && !secondItemMeta.hasLore()) sameLore = true;

            sameItemMeta = sameLore && sameDisplayName;
        } else if (!first.hasItemMeta() && !second.hasItemMeta())
            sameItemMeta = true;

        return sameType && sameData && sameHasItemMeta && sameEnchantments && sameItemMeta;
    }

    public static void removeCustomItem(Player player, ItemStack item) {
        int amountToRemove = item.getAmount();

        for (ItemStack search : player.getInventory()) {
            if (amountToRemove > 0) {
                if (search != null) {
                    if (itemEquals(search, item)) {
                        int searchAmount = search.getAmount();

                        if (amountToRemove - searchAmount == 0) {
                            player.getInventory().removeItem(search);
                            amountToRemove = 0;
                        } else if (amountToRemove - searchAmount > 0) {
                            amountToRemove = amountToRemove - searchAmount;
                            player.getInventory().removeItem(search);
                        } else if (amountToRemove - searchAmount < 0) {
                            ItemStack readd = search.clone();
                            player.getInventory().removeItem(search);

                            readd.setAmount(searchAmount - amountToRemove);
                            player.getInventory().addItem(readd);

                            amountToRemove = 0;
                        }
                    }
                }
            }
        }
    }

    public static void deduct(Player player, int amount) {
        BetterShop.getEconomy().withdrawPlayer(player, amount);
    }

    public static void add(Player player, int amount) {
        BetterShop.getEconomy().depositPlayer(player, amount);
    }

    public static double getBalance(Player player) {
        return BetterShop.getEconomy().getBalance(player);
    }
}
