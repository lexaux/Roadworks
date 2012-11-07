package com.augmentari.roadworks.sensorlogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.text.format.DateFormat;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.io.*;
import java.util.Calendar;

/**
 * Sample service -- will be doing 2 things later:
 * 1) collecting data from various sources (accelerometer, GPS, maybe microphone)
 * 2) packing it, preparing and sending to the rest (server)
 */
public class SensorLoggerService extends Service implements SensorEventListener {

    // size of the buffer for the file stream; .5M for a start
    public static final int BUFFER_SIZE = 512 * 1024;
    private SensorManager sensorManager;

    private Sensor accelerometer;
    private FileOutputStream outputStream;
    private PrintWriter fileResultsWriter;

    public static final String TIME_FORMAT = "hh:mm:ss";

    @Override
    public IBinder onBind(Intent intent) {
        Log.w("onBind called - but we don't allow binding so skipping.");
        return null;
    }

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            outputStream = openFileOutput(MainActivity.OUTPUT_FILE_NAME, Context.MODE_WORLD_READABLE);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER_SIZE);
            fileResultsWriter = new PrintWriter(new OutputStreamWriter(bufferedOutputStream));
            writeHeading(outputStream);
        } catch (FileNotFoundException e) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        }

        super.onCreate();
    }

    private void writeHeading(FileOutputStream outputStream) {
        fileResultsWriter.println("Time, Sensor 1, Sensor 2, Sensor 3");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (accelerometer == null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);

        // seem like don't need to close 'lower' streams, as this delegates close command down the
        // chain till the fileOutputStream
        fileResultsWriter.close();

        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        StringBuilder sb = new StringBuilder();
        String time = DateFormat.format(TIME_FORMAT, Calendar.getInstance()).toString();
        sb.append(time)
                .append(",").append(sensorEvent.values[0])
                .append(",").append(sensorEvent.values[1])
                .append(",").append(sensorEvent.values[2]);
        fileResultsWriter.println(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
