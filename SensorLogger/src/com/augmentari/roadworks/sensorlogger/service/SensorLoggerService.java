package com.augmentari.roadworks.sensorlogger.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;
import com.augmentari.roadworks.model.RecordingSession;
import com.augmentari.roadworks.sensorlogger.activity.MainActivity;
import com.augmentari.roadworks.sensorlogger.R;
import com.augmentari.roadworks.sensorlogger.dao.RecordingSessionDAO;
import com.augmentari.roadworks.sensorlogger.util.CloseUtils;
import com.augmentari.roadworks.sensorlogger.util.Formats;
import com.augmentari.roadworks.sensorlogger.util.Log;
import com.augmentari.roadworks.sensorlogger.util.Notifications;

import java.io.*;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Sample service -- will be doing 2 things later:
 * 1) collecting data from various sources (accelerometer, GPS, maybe microphone)
 * 2) packing it, preparing and sending to the rest (server)
 */
public class SensorLoggerService extends Service implements SensorEventListener, LocationListener {

    private boolean isStarted = false;
    // size of the buffer for the file stream; .5M for a start
    public static final int BUFFER_SIZE = 128 * 1024;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocationManager locationManager;

    private FileOutputStream outputStream;
    private PrintWriter fileResultsWriter;

    private long startTimeMillis;
    private long statementsLogged;

    private double latitude = 0;
    private double longitude = 0;
    private float speed = 0;

    private RecordingSessionDAO recordingSessionDAO;

    // A Wake Lock object. Lock is acquired when the application asks the service to start listening to events, and
    // is releaserd when the service is actually stopped. As this wake lock is a PARTIAL one, screen may go off but the
    // processor should remain running in the background
    private PowerManager.WakeLock wakeLock = null;

    private RecordingSession currentSession;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("OnBind");
        return new SessionLoggerServiceBinder();
    }

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        recordingSessionDAO = new RecordingSessionDAO(this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("On start command");
        isStarted = true;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setTicker(getString(R.string.sensor_logger_service_notification_ticker))
                .setContentTitle(getString(R.string.notification_ongoing_title))
                .setContentText(getString(R.string.sensor_logger_service_notification_text))
                .setSmallIcon(R.drawable.ic_stat_logger_notification)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .getNotification();

        startForeground(Notifications.ONGOING_NOTIFICATION, notification);

        if (accelerometer == null) {
            try {
                currentSession = new RecordingSession();
                currentSession.setStartTime(new Date());

                String shortFileName = "data" + currentSession.getStartTime().getTime() + ".log";
                currentSession.setDataFileFullPath(new File(getFilesDir(),
                        shortFileName).getAbsolutePath());
                recordingSessionDAO.open();
                currentSession = recordingSessionDAO.startNewRecordingSession(currentSession);

                outputStream = openFileOutput(shortFileName, Context.MODE_WORLD_READABLE);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER_SIZE);
                fileResultsWriter = new PrintWriter(new OutputStreamWriter(bufferedOutputStream));
                writeHeading();
            } catch (FileNotFoundException e) {
                CloseUtils.closeStream(outputStream);
                throw new RuntimeException(e);
            }

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
            wakeLock.acquire();

            startTimeMillis = System.currentTimeMillis();
            statementsLogged = 0;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("On service destroy");
        if (!isStarted) {
            // we have only been bound, and no real action to take.
            return;
        }
        isStarted = false;
        stopForeground(true);
        sensorManager.unregisterListener(this);

        // seem like don't need to close 'lower' streams, as this delegates close command down the
        // chain till the fileOutputStream
        if (wakeLock != null) {
            wakeLock.release();
        }
        CloseUtils.closeStream(fileResultsWriter);

        locationManager.removeUpdates(this);

        recordingSessionDAO.open();
        recordingSessionDAO.finishSession(currentSession.getId(), statementsLogged, new Date());
        CloseUtils.closeDao(recordingSessionDAO);

        Toast.makeText(
                this,
                MessageFormat.format(
                        getString(R.string.fileCollectedSizeMessage),
                        Formats.formatReadableBytesSize(new File(currentSession.getDataFileFullPath()).length())),
                Toast.LENGTH_LONG).show();

        super.onDestroy();
    }

    private void writeHeading() {
        fileResultsWriter.println("Time, Accelerometer Sensor 1, Sensor 2, Sensor 3, Gps Speed, Latitude, Longitude");
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        statementsLogged++;
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis()) //as we need to re-sample the actual sequence to a constant sample rate
                .append(",").append(sensorEvent.values[0])
                .append(",").append(sensorEvent.values[1])
                .append(",").append(sensorEvent.values[2])
                .append(",").append(speed)
                .append(",").append(latitude)
                .append(",").append(longitude);
        fileResultsWriter.println(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("onAccuracyChanged");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("onProviderDisabled");
    }

    public class SessionLoggerServiceBinder extends Binder {
        public boolean isStarted() {
            return isStarted;
        }

        public long getStartTimeMillis() {
            return startTimeMillis;
        }

        public long getStatementsLogged() {
            return statementsLogged;
        }
    }
}
