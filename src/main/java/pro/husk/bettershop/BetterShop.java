package pro.husk.bettershop;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pro.husk.bettershop.commands.BetterShopCommands;
import pro.husk.bettershop.events.PlayerChatInput;
import pro.husk.bettershop.objects.Config;
import pro.husk.bettershop.objects.Shop;

import java.io.File;
import java.util.logging.Logger;

public final class BetterShop extends JavaPlugin {

    @Getter
    private static BetterShop instance;

    @Getter
    private static Economy economy;

    private static Logger logger;

    private PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        // Load configs
        saveDefaultConfig();
        Config.loadConfig(getConfig());
        loadShops();

        // Setup commands
        paperCommandManager = new PaperCommandManager(instance);
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("shops", c -> Shop.getShopHashMap().keySet());
        paperCommandManager.getCommandContexts().registerContext(Shop.class, Shop.getContextResolver());
        paperCommandManager.registerCommand(new BetterShopCommands());

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerChatInput(), instance);

        // Load vault
        setupEconomy();
    }

    @Override
    public void onDisable() {
        // Save all shops on shutdown
        Shop.getShopHashMap().forEach((name, shop) -> {
            shop.saveToConfig();
        });
        paperCommandManager.unregisterCommands();
    }

    public static void info(String info) {
        logger.info(info);
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null)
                return;
            economy = rsp.getProvider();
        } else {
            info("Vault not found - disabling!");
            setEnabled(false);
        }
    }

    private void loadShops() {
        File shopFiles = new File(getDataFolder().getPath() + "/Shops/");

        if (!shopFiles.exists()) {
            if (!shopFiles.mkdirs()) {
                info("Shop data folder already exists! Moving on!");
            }
        } else {
            if (shopFiles.isDirectory()) {
                for (File shopFile : shopFiles.listFiles()) {
                    if (shopFile.getName().endsWith(".yml")) {
                        if (Shop.loadShop(shopFile) != null) {
                            info("Loaded shop: " + shopFile.getName());
                        } else {
                            info("Error loading shop: " + shopFile.getName());
                        }
                    }
                }
            }
        }
    }
}
