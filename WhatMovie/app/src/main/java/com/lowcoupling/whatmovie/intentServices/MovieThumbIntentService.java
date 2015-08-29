package com.lowcoupling.whatmovie.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;

import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.datamodel.Movie;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import java.io.InputStream;

public class MovieThumbIntentService extends IntentService {
    private String imdbId;
    private String url;

    public MovieThumbIntentService() {
        super("MovieThumbIntentService");
    }

    public Object loadInBackground() {
        Drawable drw = null;
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }


            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    drw = Drawable.createFromStream(inputStream,url);

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            //Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        Movie movie = (Movie) DataViewHelper.getInstance().getEntities().get(imdbId);
        movie.setThumb(drw);
        DataViewHelper.getInstance().getEntities().put(imdbId, movie);
        return movie;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.imdbId = intent.getStringExtra("imdbId");
        this.url = intent.getStringExtra("thumbUrl");
        loadInBackground();
        sendMessage();
    }

    private void sendMessage() {
        Intent intent = new Intent("MovieThumbUpdate");
        intent.putExtra("imdbId", this.imdbId);
        sendBroadcast(intent);
    }

}

