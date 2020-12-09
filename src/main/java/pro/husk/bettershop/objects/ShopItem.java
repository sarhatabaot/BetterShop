package pro.husk.bettershop.objects;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pro.husk.bettershop.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {

    @Getter
    @Setter
    private ItemStack itemStack;

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
    private String permission;

    @Getter
    private List<String> messages;

    @Getter
    private List<String> commands;

    @Getter
    private final List<ItemStack> contents;

    @Getter
    @Setter
    private boolean closeOnTransaction;

    /**
     * Constructor for a ShopItem
     *
     * @param itemStack       itemstack of the item
     * @param shopFunction    function of the item
     * @param buyCost         how much it cost to buy this item
     * @param sellCost        how much a player is rewarded on selling this item
     *                        (per 1)
     * @param cooldownSeconds how long a player is affected by a cooldown on this
     *                        item's shop function
     * @param visibility      who can see the item
     * @param permission      what permission the user should have to see this item
     * @param messages        what messages to send the user upon sale
     * @param commands        what commands are run after an item sale
     */
    public ShopItem(ItemStack itemStack, ShopFunction shopFunction, int buyCost, int sellCost, int cooldownSeconds,
            Visibility visibility, String permission, List<String> messages, List<ItemStack> contents,
            List<String> commands, boolean closeOnTransaction) {
        this.itemStack = itemStack;
        this.shopFunction = shopFunction;
        this.buyCost = buyCost;
        this.sellCost = sellCost;
        this.cooldownSeconds = cooldownSeconds;
        this.visibility = visibility;
        this.messages = messages;
        this.permission = permission;
        this.contents = contents;
        this.commands = commands;
        this.closeOnTransaction = closeOnTransaction;
    }

    /**
     * Used when creating a new ShopItem
     *
     * @param itemStack of the ShopItem
     */
    public ShopItem(ItemStack itemStack) {
        this(itemStack, ShopFunction.NONE, 0, 0, 0, Visibility.ALL, null, null, new ArrayList<>(), null, true);
    }

    /**
     * Adds a message to the optional list
     *
     * @param message to add
     */
    public void addMessage(String message) {
        if (messages != null) {
            messages.add(message);
        } else {
            List<String> messagesList = new ArrayList<>();
            messagesList.add(message);
            this.messages = messagesList;
        }
    }

    /**
     * Builds the display ItemStack ready for display on the Shop browse page
     *
     * @return ItemStack of this ShopItem instance
     */
    public ItemStack getDisplayItem() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemBuilder.Builder itemBuilder = ItemBuilder.builder(itemStack.getType()).name(getItemStackName())
                .lore(itemMeta.getLore()).setEnchantments(itemStack.getEnchantments());

        if (shopFunction == ShopFunction.BUY) {
            itemBuilder.addLore(ChatColor.DARK_GREEN + "Cost: " + buyCost);
        } else if (shopFunction == ShopFunction.SELL) {
            itemBuilder.addLore(ChatColor.GREEN + "Sell: " + sellCost);
        } else if (shopFunction == ShopFunction.BUY_AND_SELL) {
            itemBuilder.addLore(ChatColor.DARK_GREEN + "Cost: " + buyCost);
            itemBuilder.addLore(ChatColor.GREEN + "Sell: " + sellCost);
            itemBuilder.addLore("", ChatColor.GRAY + "Left click to buy, Right click to sell");
        } else if (shopFunction == ShopFunction.COMMAND) {
            itemBuilder.addLore(ChatColor.DARK_GREEN + "Cost: " + buyCost);
        }

        return itemBuilder.build();
    }

    public void setItemStackName(String newName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(newName);
        itemStack.setItemMeta(itemMeta);
    }

    public String getItemStackName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null ? itemMeta.getDisplayName() : null;
    }

    public void addCommand(String command) {
        if (this.commands == null) {
            this.commands = new ArrayList<>();
        }
        this.commands.add(command);
    }
}