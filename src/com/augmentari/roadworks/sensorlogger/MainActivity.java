package com.augmentari.roadworks.sensorlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.io.File;

public class MainActivity extends Activity {

    public static final String OUTPUT_FILE_NAME = "data.csv";

    private Button shareResutsButton;
    private Button stopServiceButton;
    private Button startServiceButton;
    private PowerManager.WakeLock wl;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("testing logging.");

        setContentView(R.layout.main);
        startServiceButton = (Button) findViewById(R.id.startServiceButton);
        stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
        shareResutsButton = (Button) findViewById(R.id.shareResultsButton);

        stopServiceButton.setEnabled(false);
        shareResutsButton.setEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("startServiceButtonEnabled", startServiceButton.isEnabled());
        outState.putBoolean("stopServiceButtonEnabled", stopServiceButton.isEnabled());
        outState.putBoolean("shareResultsButtonEnabled", shareResutsButton.isEnabled());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("startServiceButtonEnabled")) {
            startServiceButton.setEnabled(savedInstanceState.getBoolean("startServiceButtonEnabled"));
        }

        if(savedInstanceState.containsKey("stopServiceButtonEnabled")) {
            stopServiceButton.setEnabled(savedInstanceState.getBoolean("stopServiceButtonEnabled"));
        }


        if(savedInstanceState.containsKey("shareResultsButtonEnabled")) {
            shareResutsButton.setEnabled(savedInstanceState.getBoolean("shareResultsButtonEnabled"));
        }
    }

    public void startServiceButtonClicked(View sender) {
        Intent testServiceIntent = new Intent(this, SensorLoggerService.class);
        startService(testServiceIntent);
        stopServiceButton.setEnabled(true);
        startServiceButton.setEnabled(false);
        Toast.makeText(this, "Started recording location and accelerometer readings.", Toast.LENGTH_SHORT).show();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    public void stopServiceButtonClicked(View sender) {
        wl.release();

        Intent stopServiceIntent = new Intent(this, SensorLoggerService.class);
        stopService(stopServiceIntent);
        stopServiceButton.setEnabled(false);
        startServiceButton.setEnabled(true);
        shareResutsButton.setEnabled(true);
        Toast.makeText(this, "Stopped sniffing accelerometer readings. Results written to disk.", Toast.LENGTH_SHORT).show();
    }

    public void shareResultsButtonClicked(View sender) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        File resultFile = new File(getFilesDir(), OUTPUT_FILE_NAME);
        Log.i(Uri.fromFile(resultFile).toString() + " --> size is " + resultFile.length());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(resultFile));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.sendToInvitation)));
    }
}
