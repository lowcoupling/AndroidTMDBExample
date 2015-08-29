package com.lowcoupling.whatmovie.intentServices;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MovieDetailIntentService extends IntentService {

    private String imdbid = "";
    private String result = "";
    private String function ="";
    private String authority = "";
    private String traktKey = "";
    public MovieDetailIntentService(){
       super("MovieDetailIntentService");
    }

    public Object loadInBackground() {
        String authority = this.authority;
        String function = this.function;
        Uri.Builder builder = new Uri.Builder();
        Uri bu = builder.scheme("https").authority(authority).appendPath(function).appendQueryParameter("id_type","imdb").appendQueryParameter("id",imdbid).build();
        try {
            URL url = new URL(bu.toString());
            result= downloadUrl(bu.toString(),"GET");
            return result;
        }catch(Exception e){
            return null;
        }

    }

    public String downloadUrl(String _url, String method) throws IOException {
        HashMap<String,String> properties = new HashMap<String,String>();
        properties.put("Content-Type", "application/json");
        properties.put("trakt-api-version","2");
        properties.put("trakt-api-key",this.traktKey);
        int timeout= 10000; //10 seconds
        return downloadUrl(_url, method, timeout, properties);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        result = "";
        this.imdbid      = intent.getStringExtra("imdbId");
        this.authority   = intent.getStringExtra("authority");
        this.function    = intent.getStringExtra("function");
        this.traktKey    = intent.getStringExtra("traktKey");
        loadInBackground();
        sendMessage();
        //stopService(intent);
    }



    private void sendMessage() {
        Intent intent = new Intent("MovieDetailUpdate");
        // add data
        intent.putExtra("movieDetail", result);
        intent.putExtra("imdb", imdbid);

        sendBroadcast(intent);
    }



    protected String downloadUrl(String _url, String method,int timeout, HashMap<String,String> properties) throws IOException {
        InputStream is = null;
        String contentAsString = "";
        try {
            URL url = new URL(_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(timeout /* milliseconds */);
            conn.setConnectTimeout(timeout+5000 /* milliseconds */);
            Iterator<Map.Entry<String,String>> propIt = properties.entrySet().iterator();

            while(propIt.hasNext()){
                Map.Entry<String, String> entry = propIt.next();
                String key = entry.getKey();
                String value = entry.getValue();
                conn.addRequestProperty(key,value);
            }
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();
            contentAsString = readIt(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
                return contentAsString;
            }
        }
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(stream));
        String res = "";
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            res += line;
        }
        stream.close();
        return res;
    }


}
