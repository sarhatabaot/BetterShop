package pro.husk.bettershop.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

    @Getter
    @Setter
    private ItemStack itemStack;

    @Getter
    @Setter
    private ShopFunction shopFunction;

    public ShopItem(ItemStack itemStack, ShopFunction shopFunction) {
        this.itemStack = itemStack;
        this.shopFunction = shopFunction;
    }
}
