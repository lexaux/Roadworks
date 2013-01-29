package com.augmentari.roadworks.sensorlogger.util;

import com.augmentari.roadworks.sensorlogger.dao.RecordingSessionDAO;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;

/**
 * Safe close utils.
 */
public class CloseUtils {

    public static void closeStream(InputStream s) {
        if (s == null) return;
        try {
            s.close();
        } catch (Exception e) {
            Log.e("Error closing stream", e);
        }
    }

    public static void closeStream(Writer s) {
        if (s == null) return;
        try {
            s.close();
        } catch (Exception e) {
            Log.e("Error closing stream", e);
        }
    }


    public static void closeStream(OutputStream s) {
        if (s == null) return;
        try {
            s.close();
        } catch (Exception e) {
            Log.e("Error closing stream", e);
        }
    }

    public static void closeDao(RecordingSessionDAO dao) {
        if (dao == null) return;
        try {
            dao.close();
        } catch (Exception e) {
            Log.e("Error closing DAO", e);
        }
    }

    public static void closeConnection(HttpURLConnection conn) {
        if (conn == null) return;
        try {
            conn.disconnect();
        } catch (Exception e) {
            Log.e("Error closing DAO", e);
        }
    }

}
