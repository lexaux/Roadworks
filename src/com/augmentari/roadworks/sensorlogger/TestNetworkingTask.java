package com.augmentari.roadworks.sensorlogger;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.augmentari.roadworks.sensorlogger.net.ssl.NetworkingFactory;
import com.augmentari.roadworks.sensorlogger.net.ssl.NetworkingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

/**
 * Tests networking.
 */
class TestNetworkingTask extends AsyncTask<String, Void, String> {

    private Context context;

    private Exception ex = null;

    public TestNetworkingTask(Context context) {
        this.context = context;
    }

    public String readItSIC(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    protected String doInBackground(String... params) {
        ex = null;

        InputStream is = null;
        HttpURLConnection connection = null;
        try {
            String realUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefActivity.KEY_PREF_API_BASE_URL, "") + "api/helloworld/2";
            is = NetworkingFactory.openConnection(realUrl, context).getInputStream();
            String s = readItSIC(is, 4000);
            return s;
        } catch (Exception e) {
            ex = e;
            e.printStackTrace();
            return null;
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
        if (ex != null) {
            Toast.makeText(context, "Error accessing network!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, o, Toast.LENGTH_LONG).show();
        }
    }

}
