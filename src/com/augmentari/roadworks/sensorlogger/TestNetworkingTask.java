package com.augmentari.roadworks.sensorlogger;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

/**
 * Tests networking.
 */
class TestNetworkingTask extends AsyncTask<String, Void, String> {

    private Context context;

    public TestNetworkingTask(Context context) {
        this.context = context;
    }

    public String readItSIC(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        HttpsURLConnection connection = null;
        try {
            KeyStore keyStore = loadKeystore();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            String realUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefActivity.KEY_PREF_API_BASE_URL, "") + "api/helloworld/2";
            URL url = new URL(realUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(new BrowserCompatHostnameVerifier());
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            is = connection.getInputStream();
            String s = readItSIC(is, 4000);
            return s;
        } catch (Exception e) {
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

    private KeyStore loadKeystore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        // get user password and file input stream
        char[] password = "changeit".toCharArray();

        InputStream fis = null;
        try {
            fis = context.getResources().openRawResource(R.raw.mystore);
            ks.load(fis, password);
            return ks;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        Toast.makeText(context, o, Toast.LENGTH_LONG);
    }

}
