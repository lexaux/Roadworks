package com.augmentari.roadworks.sensorlogger.util;

import java.text.DecimalFormat;

/**
 * Format short-hand for various used strings.
 */
public class Formats {
    /**
     * Show correct formatting of data size in Kilo/Mega/Giga bytes.
     *
     * @param sizeBytes bytes to format to
     * @return returns a string in user-readable format
     */
    public static String readableDataSize(long sizeBytes) {
        if (sizeBytes <= 0) return "Zero";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"}; //TB OH RLY?
        int digitGroups = (int) (Math.log10(sizeBytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeBytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
