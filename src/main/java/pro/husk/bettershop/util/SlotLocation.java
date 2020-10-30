package pro.husk.bettershop.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Utility class to convert slot number into x,y coordinates required for
 */
@AllArgsConstructor
@EqualsAndHashCode
public class SlotLocation {

    /**
     * Returns x value
     */
    @Getter
    private final int x;

    /**
     * Returns y value
     */
    @Getter
    private final int y;

    /**
     * Method to create a SlotLocation from the original slot index
     *
     * @param slot   index
     * @param length of the inventory
     * @return SlotLocation object
     */
    public static SlotLocation fromSlotNumber(int slot, int length) {
        int x = slot % length;
        int y = slot / length;
        return new SlotLocation(x, y);
    }

    /**
     * Converts SlotLocation to a String
     */
    @Override
    public String toString() {
        return x + ", " + y;
    }

    /**
     * Method to create a SlotLocation from a String, used for loading Shop config
     *
     * @param input string to load from
     * @return SlotLocation object
     */
    public static SlotLocation fromString(String input) {
        String[] split = input.split(", ");

        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        return new SlotLocation(x, y);
    }
}
