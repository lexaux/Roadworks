package com.augmentari.roadworks.sensorlogger;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.util.Formats;
import com.augmentari.roadworks.sensorlogger.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenu:
                Intent settingsActivityIntent = new Intent(this, PrefActivity.class);
                startActivity(settingsActivityIntent);
                break;
            case R.id.testNetworking:
                new TestNetworkingTask().execute();
                break;
        }
        return true;
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

    /**
     * Tests networking.
     */
    class TestNetworkingTask extends AsyncTask<String, Void, String> {

        public String readItSIC(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            HttpURLConnection connection = null;
            try {
                String realUrl = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(PrefActivity.KEY_PREF_API_BASE_URL, "") + "api/helloworld/2";
                URL url = new URL(realUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                is = connection.getInputStream();
                String s = readItSIC(is, 4000);
                return s;
            } catch (MalformedURLException e) {
                Log.e("malformed URL");
                //TODO! refactor this out!
                throw new RuntimeException(e);
            } catch (IOException e) {
                Log.e("IO Exception");
                //TODO! refactor this out!
                throw new RuntimeException(e);
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
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Toast.makeText(MainActivity.this, o, Toast.LENGTH_LONG);
        }

    }
}
