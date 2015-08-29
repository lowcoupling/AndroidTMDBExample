package com.lowcoupling.whatmovie.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.datamodel.Movie;

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

public class MovieRatingIntentService extends IntentService {
    private Movie movie;
    private String authority;
    private String imdbid;
    private String result;
    private String traktKey;

    public MovieRatingIntentService(){
        super("MovieRatingIntentService");
    }

    public Object loadInBackground() {
        String authority = this.authority;
        Uri.Builder builder = new Uri.Builder();
        Uri bu = builder.scheme("https").authority(authority).appendPath("movies").appendPath(movie.getTraktId()).appendPath("ratings").build();
        try{
           // URL url = new URL(bu.toString());
            result= downloadUrl(bu.toString(),"GET");
        }catch(Exception e){
            return null;
        }
        return result;
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
        this.imdbid = intent.getStringExtra("imdbId");
        this.authority = intent.getStringExtra("authority");
        this.traktKey = intent.getStringExtra("traktKey");
        movie = (Movie) DataViewHelper.getInstance().getEntities().get(this.imdbid);
        loadInBackground();
        sendMessage();
    }



    private void sendMessage() {
        Intent intent = new Intent("MovieRatingUpdate");
        // add data
        intent.putExtra("movieRating", this.result);
        intent.putExtra("imdbId",this.imdbid);
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
