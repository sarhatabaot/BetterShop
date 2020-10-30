package pro.husk.bettershop;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private static Logger logger;

    private PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        saveDefaultConfig();
        Config.loadConfig(getConfig());
        loadShops();

        paperCommandManager = new PaperCommandManager(instance);
        paperCommandManager.getCommandContexts().registerContext(Shop.class, Shop.getContextResolver());
        paperCommandManager.registerCommand(new BetterShopCommands());

        getServer().getPluginManager().registerEvents(new PlayerChatInput(), instance);
    }

    @Override
    public void onDisable() {
        paperCommandManager.unregisterCommands();
    }

    public static void info(String info) {
        logger.info(info);
    }

    private void loadShops() {
        File shopFiles = new File(getDataFolder().getPath() + "/shops/");

        if (!shopFiles.exists()) {
            if (!shopFiles.mkdirs()) {
                info("Shop data folder already exists! Moving on!");
            }
        } else {
            if (shopFiles.isDirectory()) {
                for (File shopFile : shopFiles.listFiles()) {
                    if (Shop.loadShop(YamlConfiguration.loadConfiguration(shopFile)) != null) {
                        info("Loaded shop: " + shopFile.getName());
                    } else {
                        info("Error loading shop: " + shopFile.getName());
                    }
                }
            }
        }
    }
}
