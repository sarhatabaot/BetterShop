package pro.husk.bettershop.objects;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.BetterShop;
import pro.husk.bettershop.gui.CommonGUI;
import pro.husk.bettershop.gui.edit.EditShopDisplay;
import pro.husk.bettershop.gui.function.ViewShopDisplay;
import pro.husk.bettershop.util.SlotLocation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Shop {

    @Getter
    private static final HashMap<String, Shop> shopHashMap = new HashMap<>();

    @Getter
    private static final HashMap<UUID, Shop> editingMap = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final HashMap<SlotLocation, ShopItem> contentsMap;

    private YamlConfiguration configuration;

    public Shop(String name) {
        this(name, null);
    }

    public Shop(String name, YamlConfiguration configuration) {
        this.name = name;
        this.contentsMap = new HashMap<>();
        this.configuration = configuration;

        shopHashMap.put(name, this);
    }

    public void editShop(Player player) {
        editingMap.put(player.getUniqueId(), this);
        CommonGUI gui = new EditShopDisplay(this);

        gui.getGui().setOnClose(close -> {
            saveToConfig();
            removeEditor(player);
        });

        gui.show(player);
    }

    public void open(Player player) {
        if (!isBeingEdited()) {
            new ViewShopDisplay(this, player).getGui().show(player);
        } else {
            player.sendMessage(ChatColor.RED + "This shop is still being edited...");
        }
    }

    public static Shop loadShop(File shopFile) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(shopFile);
        Shop shop = new Shop(shopFile.getName().replaceAll(".yml", ""), configuration);

        ConfigurationSection section = configuration.getConfigurationSection("shop.contents");
        if (section == null) return null;

        // Load each of the items
        section.getKeys(false).forEach(key -> {
            String functionString = section.getString(key + ".function");
            int buyCost = section.getInt(key + ".buy_cost");
            int sellCost = section.getInt(key + ".sell_cost");
            String visibilityString = section.getString(key + ".visibility");
            int cooldown = section.getInt(key + ".cooldown");
            ItemStack itemStack = section.getItemStack(key + ".itemstack.display");
            List<ItemStack> itemStackContents = (List<ItemStack>) section.getList(key + ".itemstack.contents");
            List<String> messages = section.getStringList(key + ".messages");
            String permission = section.getString(key + ".permission");
            List<String> commands = section.getStringList(key + ".commands");
            boolean closeOnTransaction = section.getBoolean(key + ".close_on_transaction");

            ShopFunction function = ShopFunction.valueOf(functionString);
            Visibility visibility = Visibility.valueOf(visibilityString);

            SlotLocation slotLocation = SlotLocation.fromString(key);
            ShopItem shopItem = new ShopItem(itemStack, function, buyCost, sellCost, cooldown, visibility, permission,
                    messages, itemStackContents, commands, closeOnTransaction);

            shop.getContentsMap().put(slotLocation, shopItem);
        });

        return shop;
    }

    public void saveToConfig() {
        File file = new File(BetterShop.getInstance().getDataFolder() + "/Shops/" + name + ".yml");
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(file);
            configuration.options().header("~ BetterShop - Shop Configuration ~");
        }

        contentsMap.forEach((slotLocation, shopItem) -> {
            String slotLocationString = slotLocation.toString();
            configuration.set("shop.contents." + slotLocationString + ".function", shopItem.getShopFunction().name());
            configuration.set("shop.contents." + slotLocationString + ".buy_cost", shopItem.getBuyCost());
            configuration.set("shop.contents." + slotLocationString + ".sell_cost", shopItem.getSellCost());
            configuration.set("shop.contents." + slotLocationString + ".visibility", shopItem.getVisibility().name());
            configuration.set("shop.contents." + slotLocationString + ".cooldown", shopItem.getCooldownSeconds());
            configuration.set("shop.contents." + slotLocationString + ".itemstack.display", shopItem.getItemStack());
            configuration.set("shop.contents." + slotLocationString + ".itemstack.contents", shopItem.getContents());
            configuration.set("shop.contents." + slotLocationString + ".messages", shopItem.getMessages());
            configuration.set("shop.contents." + slotLocationString + ".permission", shopItem.getPermission());
            configuration.set("shop.contents." + slotLocationString + ".commands", shopItem.getCommands());
        });

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete() {
        shopHashMap.remove(name);

        File file = new File(BetterShop.getInstance().getDataFolder() + "/Shops/" + name + ".yml");

        if (file.exists()) {
            return file.delete();
        }

        return false;
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

    public static Shop getFromName(String name) {
        return shopHashMap.get(name);
    }

    public void addItem(ShopItem shopItem, SlotLocation slotLocation) {
        contentsMap.put(slotLocation, shopItem);
    }

    public boolean isBeingEdited() {
        return editingMap.containsValue(this);
    }

    public void removeEditor(Player player) {
        editingMap.remove(player.getUniqueId());
    }
}