package pro.husk.bettershop.objects;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopItem {

    @Getter
    @Setter
    private ItemBuilder itemBuilder;

    @Getter
    @Setter
    private ShopFunction shopFunction;

    @Getter
    @Setter
    private int buyCost;

    @Getter
    @Setter
    private int sellCost;

    @Getter
    @Setter
    private int cooldownSeconds;

    @Getter
    @Setter
    private Visibility visibility;

    @Getter
    @Setter
    private Optional<String> permissionOptional;

    @Getter
    private Optional<List<String>> messagesOptional;

    /**
     * Constructor for a ShopItem
     * 
     * @param itemStack          itemstack of the item
     * @param shopFunction       function of the item
     * @param buyCost            how much it cost to buy this item
     * @param sellCost           how much a player is rewarded on selling this item
     *                           (per 1)
     * @param cooldownSeconds    how long a player is affected by a cooldown on this
     *                           item's shop function
     * @param visibility         who can see the item
     * @param permissionOptional what permission the user should have to see this
     *                           item
     * @param messagesOptional   what messages to send the user upon sale
     */
    public ShopItem(ItemStack itemStack, ShopFunction shopFunction, int buyCost, int sellCost, int cooldownSeconds,
            Visibility visibility, Optional<String> permissionOptional, Optional<List<String>> messagesOptional) {
        this.itemBuilder = new ItemBuilder(itemStack);
        this.shopFunction = shopFunction;
        this.buyCost = buyCost;
        this.sellCost = sellCost;
        this.cooldownSeconds = cooldownSeconds;
        this.visibility = visibility;
        this.permissionOptional = permissionOptional;
        this.messagesOptional = messagesOptional;
    }

    /**
     * Adds a message to the optional list
     * @param message to add
     */
    public void addMessage(String message) {
        if (messagesOptional.isPresent()) {
            messagesOptional.get().add(message);
        } else {
            List<String> messagesList = new ArrayList<>();
            messagesList.add(message);
            this.messagesOptional = Optional.of(messagesList);
        }
    }

    /**
     * Builds the display ItemStack ready for display on the Shop browse page
     * @return ItemStack of this ShopItem instance
     */
    public ItemStack getDisplayItem() {
        itemBuilder.addLore("");
        itemBuilder.addLore(ChatColor.BLUE + "Function: " + shopFunction);
        itemBuilder.addLore(ChatColor.YELLOW + "Visibility: " + visibility);

        if (shopFunction == ShopFunction.BUY) {
            itemBuilder.addLore(ChatColor.DARK_GREEN + "Cost: " + buyCost);
            itemBuilder.addLore(ChatColor.GREEN + "Sell: " + sellCost);
        } else if (shopFunction == ShopFunction.SELL) {
            itemBuilder.addLore(ChatColor.GREEN + "Sell: " + sellCost);
        } else if (shopFunction == ShopFunction.COMMAND) {
            itemBuilder.addLore(ChatColor.DARK_GREEN + "Cost: " + buyCost);
        }

        return itemBuilder.getItemStack();
    }
}