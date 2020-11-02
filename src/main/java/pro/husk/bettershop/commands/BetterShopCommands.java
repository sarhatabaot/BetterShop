package pro.husk.bettershop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.husk.bettershop.objects.Config;
import pro.husk.bettershop.objects.Shop;

@CommandAlias("shop|bettershop|bs")
@Description("BetterShop commands")
public class BetterShopCommands extends BaseCommand {

    @Description("Shows the default help menu")
    @Subcommand("help")
    public void help(CommandSender sender) {
        sender.sendMessage("");
    }

    @Default
    @Description("Shows the default shop")
    public void shop(Player player, @Optional Shop shop) {
        if (shop == null) {
            shop = Shop.getFromName(Config.getDefaultShop());
        }
        shop.open(player);
    }

    @Subcommand("create")
    @CommandPermission("shop.create")
    @Description("Creates a shop")
    public void create(Player player, String shopName) {
        if (Shop.getShopHashMap().containsKey(shopName)) {
            player.sendMessage(ChatColor.RED + "A shop with that name already exists!");
            return;
        }

        new Shop(shopName);

        player.sendMessage(ChatColor.GREEN + "A shop with name '" + shopName + "' has been created!");
    }

    @Subcommand("edit")
    @CommandPermission("shop.edit")
    @Description("Edits a shop")
    public void edit(Player player, Shop shop) {
        shop.editShop(player);
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
}
