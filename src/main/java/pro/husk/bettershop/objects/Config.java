package pro.husk.bettershop.objects;

import org.bukkit.configuration.file.FileConfiguration;

public final class Config {

    private static int DEFAULT_SHOP_ROWS;

    public static void loadConfig(FileConfiguration config) {
        DEFAULT_SHOP_ROWS = config.getInt("default-shop-rows");
    }

    public static int getDefaultShopRows() {
        return DEFAULT_SHOP_ROWS;
    }
}
