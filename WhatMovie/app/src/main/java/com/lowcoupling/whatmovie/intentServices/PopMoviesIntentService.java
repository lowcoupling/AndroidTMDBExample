package com.lowcoupling.whatmovie.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

public  class PopMoviesIntentService extends IntentService {
    private int currentPage=1;

    private String result="";

    public String downloadUrl(String _url, String method) throws IOException {
        HashMap<String,String> properties = new HashMap<String,String>();
        properties.put("Content-Type", "application/json");
        properties.put("trakt-api-version","2");
        properties.put("trakt-api-key","5dea85dba121f1a58c4e583ddb64e5ff6f9348315e6e16fe24c810d72125c9d8");
        int timeout= 10000; //10 seconds
        return downloadUrl(_url, method, timeout, properties);
    }

    public PopMoviesIntentService( ){
        super("POPMOVIESINTENT");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        currentPage = intent.getIntExtra("page",1);
        loadInBackground();
    }

    public Object loadInBackground() {
        result = "";
        String authority ="api-v2launch.trakt.tv"; // Resources.getSystem().getString(R.string.authority);
        Uri.Builder builder = new Uri.Builder();
        Uri bu = builder.scheme("https").authority(authority).
                appendPath("movies").
                appendPath("popular").appendQueryParameter("page", "" + currentPage).build();
        try {
            result= downloadUrl(bu.toString(),"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
        sendMessage();
        return result;
    }

    private void sendMessage() {
        Intent intent = new Intent("PopMoviesUpdate");
        // add data
        Log.d("Movies ",result);
        intent.putExtra("movies", result);
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
        String line = "";
        result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        stream.close();
        return result;
    }

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int page){
        currentPage=page;
    }
}