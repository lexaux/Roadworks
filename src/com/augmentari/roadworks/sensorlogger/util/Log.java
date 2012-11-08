package com.augmentari.roadworks.sensorlogger.util;

/**
 * Logger which already does supply correct tag to filter later on.
 */
public class Log {
    public static String TAG = "Roadworks.SensorLogger";

    public static void e(String message) {
        android.util.Log.e(TAG, message);
    }

    public static void w(String message) {
        android.util.Log.w(TAG, message);
    }

    public static void i(String message) {
        android.util.Log.i(TAG, message);
    }

    public static void d(String message) {
        android.util.Log.d(TAG, message);
    }

    public static void logNotImplemented() {
        e("Not implemented! " + Thread.currentThread().getStackTrace()[0]);
    }
}
