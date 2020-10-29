package pro.husk.bettershop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.husk.bettershop.objects.Config;
import pro.husk.bettershop.objects.Shop;
import pro.husk.bettershop.objects.ShopFunction;
import pro.husk.bettershop.objects.ShopItem;
import pro.husk.bettershop.util.MenuHelper;

@CommandAlias("bshop|bs")
@Description("BetterShop commands")
public class BetterShopCommands extends BaseCommand {

    @Default
    @Description("Shows the default help menu")
    @Subcommand("help")
    public void help(CommandSender sender) {
        sender.sendMessage("");
    }

    @Subcommand("create")
    @CommandPermission("shop.create")
    @Description("Creates a shop")
    public void create(Player player, String shopName, @Optional String rows) {
        if (Shop.getShopHashMap().containsKey(shopName)) {
            player.sendMessage(ChatColor.RED + "A shop with that name already exists!");
            return;
        }

        if (rows == null) {
            new Shop(shopName, Config.getDefaultShopRows());
        } else {
            new Shop(shopName, Integer.parseInt(rows));
        }

        player.sendMessage(ChatColor.GREEN + "A shop with name '" + shopName + "' has been created!");
    }

    @Subcommand("edit")
    @CommandPermission("shop.edit")
    @Description("Edits a shop")
    public void edit(Player player, Shop shop) {
        shop.editShop(player);
    }

    @CommandPermission("shop.edit")
    @Description("Opens a shop")
    public void open(Player player, Shop shop) {
        shop.open(player);
    }

    @Subcommand("list")
    @CommandPermission("shop.list")
    @Description("Lists all shop")
    public void list(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Current shops:");
        for (String shopName : Shop.getShopHashMap().keySet()) {
            sender.sendMessage(ChatColor.GREEN + shopName);
        }
        sender.sendMessage(ChatColor.GOLD + "====================");
    }

    @Subcommand("t")
    @Description("test")
    public void test(Player player) {
        MenuHelper.openEditMenu(player, new ShopItem(new ItemStack(Material.OBSIDIAN), ShopFunction.BUY), "test");
    }
}
