/*
 * BLOCKY STUDIOS LLC - Cody Lynn
 * cody@blocky.gg
 *
 * [2019] - [2021] Blocky Studios LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Blocky Studios LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Blocky Studios LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Blocky Studios LLC.
 */

package gg.blocky.melonstackedspawners.util;

import org.bukkit.Bukkit;

public final class NMSUtil {

    private static String cachedVersion = null;
    private static int cachedVersionNumber = -1;

    private NMSUtil() {

    }

    /**
     * @return The server version
     */
    public static String getVersion() {
        if (cachedVersion == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            cachedVersion = name.substring(name.lastIndexOf('.') + 1);
        }
        return cachedVersion;
    }

    /**
     * @return the server version major release number
     */
    public static int getVersionNumber() {
        if (cachedVersionNumber == -1) {
            String name = getVersion().substring(3);
            cachedVersionNumber = Integer.parseInt(name.substring(0, name.length() - 3));
        }
        return cachedVersionNumber;
    }

}