package pro.husk.bettershop.objects.gui;

import com.github.stefvanschie.inventoryframework.Gui;

import org.bukkit.entity.HumanEntity;

/**
 * Simple interface for all GUIs created for BetterShop
 */
public interface CommonGUI {

    /**
     * Simple method to force a refresh of the GUI, rendering all items again
     */
    public void forceRefreshGUI();

    /**
     * Simple method to get GUI
     * 
     * @return gui
     */
    public Gui getGui();

    /**
     * Shows GUI to player
     * 
     * @param humanEntity player
     */
    default void show(HumanEntity humanEntity) {
        forceRefreshGUI();
        getGui().show(humanEntity);
    }
}
