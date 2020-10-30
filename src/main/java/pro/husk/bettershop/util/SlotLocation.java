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
    @Getter
    private final int x;

    @Getter
    private final int y;

    public static SlotLocation fromSlotNumber(int slot, int length) {
        int x = slot % length;
        int y = slot / length;
        return new SlotLocation(x, y);
    }
}
