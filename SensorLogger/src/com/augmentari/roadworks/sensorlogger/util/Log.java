package com.augmentari.roadworks.sensorlogger.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Logger which already does supply correct tag to filter later on.
 */
public class Log {
    public static String TAG = "Roadworks.SensorLogger";

    public static void e(String message) {
        android.util.Log.e(TAG, message);
    }

    public static void e(String message, Throwable tr) {
        android.util.Log.e(TAG, message, tr);
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

    public static void logNotImplemented(Context context) {
        try {
            Toast.makeText(context, "Not implemented!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // nothing here.
            // so that we sure don't drop app if something not implemented.
            // one scenario - if invoked from thread w/out looper (non UI)
        }
        e("Not implemented! " + Thread.currentThread().getStackTrace()[0]);
    }
}
