package pro.husk.bettershop.objects;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.ipvp.canvas.ClickInformation;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop {

    @Getter
    private static final HashMap<String, Shop> shopHashMap = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final int rows;

    @Getter
    private Menu menu;

    private final List<ShopItem> shopItems;

    public boolean isBeingEdited() {
        return ShopManager.getEditingMap().containsValue(this);
    }

    public void removeEditor(Player player) {
        ShopManager.getEditingMap().remove(player.getUniqueId());
    }

    public Shop(String name) {
        this(name, Config.getDefaultShopRows());
    }

    public Shop(String name, int rows) {
        this.name = name;
        this.rows = rows;
        this.shopItems = new ArrayList<>();

        shopHashMap.put(name, this);
    }

    public void editShop(Player player) {
        ShopManager.getEditingMap().put(player.getUniqueId(), this);

        Menu menu = ChestMenu.builder(rows)
                .title("Editing: " + name)
                .build();

        menu.open(player);

        int size = menu.getDimensions().getArea();

        for (int i = 0; i < size; i++) {
            menu.getSlot(i).setClickHandler(this::emptySlotClickHandler);
        }

        // Save the data once the window has closed!
        menu.setCloseHandler((closingPlayer, closedMenu) -> {
            removeEditor(closingPlayer);
            this.menu = closedMenu;
            closingPlayer.sendMessage(ChatColor.GREEN + "Successfully finished editing: " + name);
        });
    }

    public void open(Player player) {
        if (!isBeingEdited() && menu != null) {
            menu.open(player);
        } else {
            player.sendMessage(ChatColor.RED + "This shop is still being edited!");
        }
    }

    public void emptySlotClickHandler(Player player, ClickInformation click) {
        if (click.getClickType() == ClickType.LEFT) {
            click.setResult(Event.Result.ALLOW);

            click.getClickedSlot().setClickHandler(this::filledSlotClickHandler);
        }
    }

    public void filledSlotClickHandler(Player player, ClickInformation click) {
        if (isBeingEdited() && ShopManager.getEditingMap().get(player.getUniqueId()) == this) {

        } else {
            // handle shop
        }
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

    public void saveEdits(ShopItem shopItem) {

    }
}
