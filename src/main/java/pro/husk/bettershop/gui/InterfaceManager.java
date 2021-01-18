package pro.husk.bettershop.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class InterfaceManager {

    private final HashMap<UUID, BaseInterface> viewingMap;

    public InterfaceManager() {
        viewingMap = new HashMap<>();
    }

    public void setViewing(Player player, BaseInterface baseInterface) {
        viewingMap.put(player.getUniqueId(), baseInterface);
    }

    public void removeViewing(Player player) {
        viewingMap.remove(player.getUniqueId());
    }

    public BaseInterface getInventoryViewed(Player player) {
        return viewingMap.get(player.getUniqueId());
    }
}
