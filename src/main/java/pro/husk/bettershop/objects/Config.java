package pro.husk.bettershop.objects;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public final class Config {

    @Getter
    private static String defaultShop;

    public static void loadConfig(FileConfiguration config) {
        defaultShop = config.getString("default-shop");
    }
}
