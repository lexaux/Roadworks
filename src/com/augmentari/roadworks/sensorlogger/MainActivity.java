package com.augmentari.roadworks.sensorlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.io.File;


public class MainActivity extends Activity {

    public static final String OUTPUT_FILE_NAME = "data.csv";
    public static final String FILE_SHARE_MIME_TYPE = "text/plain";

    private Button shareResutsButton;
    private Button stopServiceButton;
    private Button startServiceButton;


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
        if (savedInstanceState.containsKey("startServiceButtonEnabled")) {
            startServiceButton.setEnabled(savedInstanceState.getBoolean("startServiceButtonEnabled"));
        }

        if (savedInstanceState.containsKey("stopServiceButtonEnabled")) {
            stopServiceButton.setEnabled(savedInstanceState.getBoolean("stopServiceButtonEnabled"));
        }


        if (savedInstanceState.containsKey("shareResultsButtonEnabled")) {
            shareResutsButton.setEnabled(savedInstanceState.getBoolean("shareResultsButtonEnabled"));
        }
    }

    public void onStartServiceClick(View sender) {
        if (!SensorLoggerService.getResultsFile(getFilesDir()).exists()) {
            //don't bother, we don't have anything to erase.
            actualStartSensorService();
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            actualStartSensorService();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.questionSureToOverwriteDataFile))
                    .setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener)
                    .show();
        }
    }


    private void actualStartSensorService() {
        Intent testServiceIntent = new Intent(this, SensorLoggerService.class);
        startService(testServiceIntent);
        stopServiceButton.setEnabled(true);
        startServiceButton.setEnabled(false);
        Toast.makeText(this, "Started recording location and accelerometer readings.", Toast.LENGTH_SHORT).show();
    }

    public void onStopServiceClick(View sender) {
        Intent stopServiceIntent = new Intent(this, SensorLoggerService.class);
        stopService(stopServiceIntent);
        stopServiceButton.setEnabled(false);
        startServiceButton.setEnabled(true);
        shareResutsButton.setEnabled(true);
    }

    public void onShareResultsClick(View sender) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        File resultFile = SensorLoggerService.getResultsFile(getFilesDir());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(resultFile));
        shareIntent.setType(FILE_SHARE_MIME_TYPE);
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.sendToInvitation)));
    }

    public void onClearClick(View sender) {
        Log.logNotImplemented();
    }

}
