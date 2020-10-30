package pro.husk.bettershop.objects;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class ShopManager {

    @Getter
    private static final HashMap<UUID, Shop> editingMap = new HashMap<>();

    @Getter
    private static final HashMap<UUID, ShopItem> itemMoveCache = new HashMap<>();
}
