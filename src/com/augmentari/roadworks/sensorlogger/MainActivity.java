package com.augmentari.roadworks.sensorlogger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.util.Formats;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.io.File;


public class MainActivity extends Activity {

    public static final String FILE_SHARE_MIME_TYPE = "text/plain";

    private Button shareResutsButton;
    private Button stopServiceButton;
    private Button startServiceButton;

    private TextView timeLoggedTextView;
    private TextView statementsLoggedTextView;

    private BroadcastReceiver serviceUpdateInfoReceiver;

    private View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.shareResultsButton:
                    Intent intent = new Intent(MainActivity.this, SessionListActivity.class);
                    MainActivity.this.startActivity(intent);
                    break;
                case R.id.startServiceButton:
                    Intent testServiceIntent = new Intent(MainActivity.this, SensorLoggerService.class);
                    startService(testServiceIntent);
                    stopServiceButton.setEnabled(true);
                    startServiceButton.setEnabled(false);
                    Toast.makeText(MainActivity.this, R.string.dataGatheringStarted, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.stopServiceButton:
                    Intent stopServiceIntent = new Intent(MainActivity.this, SensorLoggerService.class);
                    stopService(stopServiceIntent);
                    stopServiceButton.setEnabled(false);
                    startServiceButton.setEnabled(true);
                    shareResutsButton.setEnabled(true);
                    break;
                default:
                    Log.logNotImplemented(MainActivity.this);
                    break;
            }
        }
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        startServiceButton = (Button) findViewById(R.id.startServiceButton);
        startServiceButton.setOnClickListener(buttonOnClickListener);

        stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
        stopServiceButton.setOnClickListener(buttonOnClickListener);

        shareResutsButton = (Button) findViewById(R.id.shareResultsButton);
        shareResutsButton.setOnClickListener(buttonOnClickListener);

        statementsLoggedTextView = (TextView) findViewById(R.id.statementsLoggedTextView);
        timeLoggedTextView = (TextView) findViewById(R.id.timeLoggedTextView);

        stopServiceButton.setEnabled(false);
        shareResutsButton.setEnabled(false);

        serviceUpdateInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SensorLoggerService.INTENT_UPDATE_LOGGER_DATA.equals(intent.getAction())) {
                    timeLoggedTextView.setText(Formats.formatTimeFromSeconds(intent.getLongExtra(SensorLoggerService.INTENT_UPDATE_LOGGER_DATA_SECONDS_LOGGED, 0)));
                    statementsLoggedTextView.setText(Formats.formatWithSuffices(intent.getLongExtra(SensorLoggerService.INTENT_UPDATE_LOGGER_DATA_STATEMENTS_LOGGED, 0)));
                    return;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        registerReceiver(serviceUpdateInfoReceiver, new IntentFilter(SensorLoggerService.INTENT_UPDATE_LOGGER_DATA));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(serviceUpdateInfoReceiver);
        super.onPause();
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
}
