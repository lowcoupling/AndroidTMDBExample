package com.lowcoupling.whatmovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {
    public final String DEBUG_TAG = "debug";
    private Fragment fragment;
    private Fragment fg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] tm = new TrustManager[] {
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                            // not implemented
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                            // not implemented
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                    }
            };
            //context.init (null, tm, new SecureRandom ());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            //context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            //HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }catch(Exception e){}
        setContentView(R.layout.activity_main);
        fg = (Fragment) getFragmentManager().findFragmentByTag("PopMoviesFragment");
        if (fg == null) {
            fg = new PopMoviesFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fg, "PopMoviesFragment")
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NullHostNameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }
    }

}
