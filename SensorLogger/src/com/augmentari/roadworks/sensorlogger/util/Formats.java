package com.augmentari.roadworks.sensorlogger.util;

import android.text.format.DateFormat;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Format short-hand for various used strings.
 */
public class Formats {


    private static String[] bytesUnits = new String[]{"B", "KB", "MB", "GB", "TB"};
    private static String[] units = new String[]{"", "K", "M", "G", "T"};

    /**
     * Show correct formatting of data size in Kilo/Mega/Giga bytes.
     *
     * @param sizeBytes bytes to format to
     * @return returns a string in user-readable format
     */
    public static String formatReadableBytesSize(long sizeBytes) {
        return formatWithSuffices(sizeBytes, bytesUnits);
    }

    public static String formatWithSuffices(long value) {
        return formatWithSuffices(value, units);
    }

    private static String formatWithSuffices(long value, String[] prefixes) {
        if (value <= 0) return "Zero";
        int digitGroups = (int) (Math.log10(value) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1024, digitGroups)) + " " + prefixes[digitGroups];

    }

    /**
     * Date format used in JSON implementation.
     *
     * @param date date to process
     * @return
     */
    public static CharSequence formatJsonDate(Date date) {
        // trick here -- Android is different from 'Big java',hh (android)=HH(big java).hh=kk though.
        return DateFormat.format("EEE, dd MMM yyyy hh:mm:ss zzz", date);
    }

    public static DecimalFormat twoDigitDecimalFormat = new DecimalFormat("00");

    public static CharSequence formatTimeFromSeconds(long value) {
        return twoDigitDecimalFormat.format(value / 60) + ":" + twoDigitDecimalFormat.format(value % 60);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
