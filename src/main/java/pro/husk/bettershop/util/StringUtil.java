package pro.husk.bettershop.util;

import net.md_5.bungee.api.ChatColor;

public final class StringUtil {

    /**
     * Helper method to colourise a string with a combination of both hex and legacy
     * chat colours
     *
     * @param input string to colourise
     * @return colourised string
     */
    public static String colourise(String input) {
        while (input.contains("#")) {
            int index = input.indexOf("#");
            if (index != 0 && input.charAt(index - 1) == '&') {
                String hexSubstring = input.substring(index - 1, index + 7).replaceAll("&", "");

                try {
                    ChatColor transformed = ChatColor.of(hexSubstring);
                    // Apply transformation to original string
                    input = input.replaceAll("&" + hexSubstring, transformed + "");
                } catch (IllegalArgumentException ignored) {

                }
            } else {
                break;
            }
        }

        // Apply legacy transformations at end
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
