package pro.husk.bettershop.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.util.ItemBuilder;

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

    public ShopItem(ItemStack itemStack, ShopFunction shopFunction) {
        this.itemBuilder = new ItemBuilder(itemStack);
        this.shopFunction = shopFunction;
    }
}
