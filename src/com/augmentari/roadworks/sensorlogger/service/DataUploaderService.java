package com.augmentari.roadworks.sensorlogger.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.R;
import com.augmentari.roadworks.sensorlogger.activity.PrefActivity;
import com.augmentari.roadworks.sensorlogger.activity.SessionListActivity;
import com.augmentari.roadworks.sensorlogger.net.ssl.NetworkingFactory;
import com.augmentari.roadworks.sensorlogger.util.Log;
import com.augmentari.roadworks.sensorlogger.util.Notifications;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Data uploader service.
 */
public class DataUploaderService extends Service {

    private AtomicBoolean isDataUploadRunning = new AtomicBoolean(false);

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //This service is not intended to be bound to.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isDataUploadRunning.compareAndSet(false, true)) {
            Toast.makeText(this, "Data upload already running! Please wait for notification soon.", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        DataUploadTask task = new DataUploadTask(startId);
        task.execute();

        Log.i("Calling onCreate for DataUploaderService");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Calling onDestroy for DataUploaderService");
    }

    /**
     * Task for data upload.
     */
    private class DataUploadTask extends AsyncTask<Void, Integer, Void> {
        private Exception ex = null;
        private final int serviceStartId;

        public DataUploadTask(int serviceStartId) {
            this.serviceStartId = serviceStartId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: implement actual upload logic.
            // get Logged and processed data ready to be sent
            // get Set of IDs for them
            // marshal all of them to JSON
            // upload data using NetowrkingFactory via ssl, using endpoints.
            // update the SQL with the given IDs (set).
            ex = null;
            InputStream is = null;
            HttpURLConnection connection = null;

            try {
                Context context = DataUploaderService.this;
                String realUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefActivity.KEY_PREF_API_BASE_URL, "") + "api/helloworld/2";
                connection = NetworkingFactory.openConnection(realUrl, context);
                is = connection.getInputStream();
                // enough to check networking this way.
            } catch (Exception e) {
                ex = e;
                e.printStackTrace();
                Log.e("Error in DataUploadTask (async): " + e.getMessage() + " " + e.getClass());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NotificationManager notificationManager = (NotificationManager) DataUploaderService.this.getSystemService(NOTIFICATION_SERVICE);

            int title = R.string.notification_title;
            String text;
            int ticker;
            Class activityClassToOpen;
            if (ex == null) {
                //TODO: do not show notification and do nothing actually if no data was transferred (no new data logged)
                text = getString(R.string.data_upload_service_success_text);
                ticker = R.string.data_upload_service_success_ticker;
                activityClassToOpen = SessionListActivity.class;
            } else {
                text = getString(R.string.data_upload_service_fail_text) + " " + ex.getMessage();
                ticker = R.string.data_upload_service_fail_ticker;
                activityClassToOpen = PrefActivity.class;
            }
            Intent notificationIntent = new Intent(DataUploaderService.this, activityClassToOpen);
            PendingIntent pendingIntent = PendingIntent.getActivity(DataUploaderService.this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(DataUploaderService.this)
                    .setTicker(getString(ticker))
                    .setContentTitle(getString(title))
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_stat_logger_notification)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .getNotification();
            notificationManager.notify(Notifications.DATA_UPLOADER_NOTIFICATION, notification);

            stopSelf(serviceStartId);
            isDataUploadRunning.set(false);
        }
    }
}
