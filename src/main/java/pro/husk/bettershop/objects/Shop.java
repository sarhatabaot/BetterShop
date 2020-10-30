package pro.husk.bettershop.objects;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import pro.husk.bettershop.objects.gui.EditShopDisplay;
import pro.husk.bettershop.util.SlotLocation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Shop {

    @Getter
    private static final HashMap<String, Shop> shopHashMap = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final int rows;

    @Getter
    private final HashMap<SlotLocation, ShopItem> contentsMap;

    public Shop(String name) {
        this(name, Config.getDefaultShopRows());
    }

    public Shop(String name, int rows) {
        this.name = name;
        this.rows = rows;
        this.contentsMap = new HashMap<>();

        shopHashMap.put(name, this);
    }

    public void editShop(Player player) {
        ShopManager.getEditingMap().put(player.getUniqueId(), this);
        new EditShopDisplay(this).getGui().show(player);
    }

    public void open(Player player) {

    }

    public static Shop loadShop(YamlConfiguration configuration) {
        return null;
    }

    public static ContextResolver<Shop, BukkitCommandExecutionContext> getContextResolver() {
        return (c) -> {
            Shop shop = shopHashMap.get(c.popFirstArg());

            if (shop == null) {
                throw new InvalidCommandArgument("No shop with that name exists!");
            }

            return shop;
        };
    }

    public void addItem(ShopItem shopItem, SlotLocation slotLocation) {
        contentsMap.put(slotLocation, shopItem);
    }

    public boolean isBeingEdited() {
        return ShopManager.getEditingMap().containsValue(this);
    }

    public void removeEditor(Player player) {
        ShopManager.getEditingMap().remove(player.getUniqueId());
    }
}