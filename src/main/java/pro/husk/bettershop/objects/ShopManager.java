package pro.husk.bettershop.objects;

import lombok.Getter;
import org.ipvp.canvas.Menu;

import java.util.HashMap;
import java.util.UUID;

public class ShopManager {

    @Getter
    private static final HashMap<UUID, Shop> editingMap = new HashMap<>();


}
