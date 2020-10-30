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
     * @param first  itemstack
     * @param second itemstack
     * @return whether they are the same
     */
    public static boolean itemEquals(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return false;
        }

        boolean sameType = (first.getType() == second.getType());
        boolean sameData = (first.getData() == second.getData());
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
            }

            if (sameHasLore && firstItemMeta.hasLore()) {
                sameLore = firstItemMeta.getLore().equals(secondItemMeta.getLore());
            }

            sameItemMeta = sameLore && sameDisplayName;
        } else if (!first.hasItemMeta() && !second.hasItemMeta())
            sameItemMeta = true;

        return sameType && sameData && sameHasItemMeta && sameEnchantments && sameItemMeta;
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
